const Room = require('../../models/room.model');
const Spot = require('../../models/spot.model');
const User = require('../../models/user.model');

const core = require('../../controllers/game/core.controller');
const room = require('../../routes/socket/room');
let roomController = {};

roomController.joinRoom = async function (obj, socket, next) {
	try {
		const room = await Room.findOne({ name: obj.room });
		if(!room) {
			success = false;
			message = "This room doesn't exist.";
			socket.emit('getJoinRoomAuthorization', {success, message});
			console.log({success, message});
			return;
		}

		if (room.is_in_game == true) {
			success = false;
			message = "A game is currently being played in this room.";
			socket.emit('getJoinRoomAuthorization', {success, message});
			console.log({success, message});
			return;
		}

		const isNameTaken = await User.find({room_id: room._id, name: obj.name});
		if(isNameTaken.length == 1) {
			success = false;
			message = "This username is already taken!";
			socket.emit('getJoinRoomAuthorization', {success, message});
			console.log({success, message});
			return;
		}

		const spot = await Spot.findOneAndUpdate({room_id: room._id, player_id: 'None'}, {player_id: socket.id});
		if(!spot) {
			success = false;
			message = "This room is full.";
			socket.emit('getJoinRoomAuthorization', {success, message});
			console.log({success, message});
			return;
		}

		const user = await User.create({name: obj.name, avatar: obj.avatar, room_id: room._id, spot_id: spot._id});
		if(!user) {
			success = false;
			message = "There are too many players connected to PokerKings.";
			socket.emit('getJoinRoomAuthorization', {success, message});
			console.log({success, message});
			return;
		}

		const updateRoomResult = await Room.findOneAndUpdate({_id: room._id}, {players_in_room: room.players_in_room+1}, {new: true});
		if(!updateRoomResult) {
			success = false;
			message = "Error while updating the room. Try again!";
			socket.emit('getJoinRoomAuthorization', {success, message});
			console.log({success, message});
			return;	
		}

		const updatePlayerResult = await User.find({room_id: room._id}, {name: 1, avatar: 1, spot_id: 1, ready: 1});
		if(!updatePlayerResult) {
			success = false;
			message = "Error while updating your information. Try again!";
			socket.emit('getJoinRoomAuthorization', {success, message});
			console.log({success, message});
			return;	
		}

		const roomRoute = "room/"+room._id;
		socket.join("room/"+room._id);
		socket.join("spot/"+spot._id);
		socket.emit('getJoinRoomAuthorization', {success: true, spot: spot._id, room: room._id});

		const roomPlayers = await User.find({room_id: room._id}, {name: 1, avatar: 1, spot_id: 1, ready: 1});
		socket.getRequest = [];
		socket.getRequest.push({room: roomRoute, route: "getPreGamePlayerList", data: {players: roomPlayers}});

		console.log("Request successfully fulfilled!\n");
		next();
	} catch (e) {
		console.log(e.message);
	}
};

roomController.setReady = async function (obj, socket, next) {
	try {
		// Must check for edge cases
		const result = await User.findOneAndUpdate({room_id: obj.room_id, name: obj.name}, {ready: true});

		var data = {};
		socket.getRequest = [];
		const playerList = await User.find({room_id: obj.room_id, ready: false}, {name: 1});

		socket.emit('getReadyPlayerAuthorization', {success: true});

		const room = await Room.findOne({_id: obj.room_id});
		if(!room) {
			// This room doesn't exist
			console.log("this room doesn't exist!");
			socket.emit('getReadyPlayerAuthorization', {success: false});
			return;
		}

		// TODO: Fix the pre game player list received
		const roomRoute = "room/"+obj.room_id;
		const roomPlayers = await User.find({room_id: obj.room_id}, {name: 1, avatar: 1, spot_id: 1, ready: 1});

		socket.getRequest.push({room: roomRoute, route: "getPreGamePlayerList", data: {players: roomPlayers}});

		// There must be more than 1 player in the room for the game to start
		if(playerList.length == 0 && room.players_in_room > 1) {
			core.startGame(obj, socket, next);
		}

		console.log("Request successfully fulfilled!\n");
		next();
	} catch {
		console.log("Cannot retrieve ready players!");
		socket.emit('getReadyPlayerAuthorization', {success: false});
	}
};

roomController.getPreGamePlayerList = async function (obj, socket, next) {
	try {
		const roomPlayers = await User.find({room_id: obj.room_id}, {name: 1, avatar: 1, spot_id: 1, ready: 1});
		socket.emit('getPreGamePlayerList', {players: roomPlayers});
		console.log("Request successfully fulfilled!\n");
		return;
	} catch {
		console.log("Cannot retrieve players!");
		socket.emit('getPreGamePlayerList', {players: roomPlayers});
	}
};

roomController.play = async function (obj, socket, next) {
	try {
		// TODO: Check if the room is in a state to receive a play

		const room = await Room.findOne({_id: obj.room_id});
		if(!room) {
			// This room doesn't exist
			success = false;
			message = "This room doesn't exist!";
			socket.emit('getAuthorizationToPlay', {success, message});
			return;
		}

		if(room.current_player != obj.spot_id) {
			// Not your turn
			success = false;
			message = "This is not your turn!";
			socket.emit('getAuthorizationToPlay', {success, message});
			return;
		}
		
		const playerIndex = room.players_ids.indexOf(obj.spot_id);
		socket.getRequest = [];
		const absoluteRaise = obj.raise+room.round_players_bets[playerIndex];
		// TODO: FIX IF PLAYER DOESNT HAVE ENOUGH MONEY BUT WANTS TO CONTINUE PLAYING
		// Maybe we can go negative if player has less money than minimum raise to implement it
		if(!obj.is_folding) {
	
			if(absoluteRaise < room.round_current_minimum) {
				// The raise is too low
				success = false;
				message = "The raise is lower than the current minimum!";
				socket.emit('getAuthorizationToPlay', {success, message});
				return ;
			}

			if(obj.raise > room.players_money[playerIndex]) {
				// Not enough money
				success = false;
				message = "You don't have enough money to raise by this amount!";
				socket.emit('getAuthorizationToPlay', {success, message});
				return;
			}
		}	
		success = true;
		socket.emit('getAuthorizationToPlay', {success});
		core.manageGame(obj, socket, next, room);

		console.log("Request successfully fulfilled!\n");
		next();
	} catch (e){
		success = false;
		message = e.message;
		console.log(e);
		socket.emit('getAuthorizationToPlay', {success: false});
	}
};

module.exports = roomController;
