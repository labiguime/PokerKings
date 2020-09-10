const Room = require('../../models/room.model');
const Spot = require('../../models/spot.model');
const User = require('../../models/user.model');

const core = require('../../controllers/game/core.controller');
let roomController = {};

roomController.joinRoom = async function (obj, socket, next) {
	try {
		const room = await Room.findOne({ name: obj.room });
		if(!room) {
			success = false;
			message = "This room doesn't exist.";
			socket.emit('getJoinRoomAuthorization', {success: success, message: message});
			console.log({success: success, message: message});
			return;
		}

		if (room.is_in_game == true) {
			success = false;
			message = "A game is currently being played in this room.";
			socket.emit('getJoinRoomAuthorization', {success: success, message: message});
			console.log({success: success, message: message});
			return;
		}

		const isNameTaken = await User.find({room_id: room._id, name: obj.name});
		if(isNameTaken.length == 1) {
			success = false;
			message = "This username is already taken!";
			socket.emit('getJoinRoomAuthorization', {success: success, message: message});
			console.log({success: success, message: message});
			return;
		}

		const spot = await Spot.findOneAndUpdate({room_id: room._id, player_id: 'None'}, {player_id: socket.id});
		if(!spot) {
			success = false;
			message = "This room is full.";
			socket.emit('getJoinRoomAuthorization', {success: success, message: message});
			console.log({success: success, message: message});
			return;
		}

		const user = await User.create({name: obj.name, avatar: obj.avatar, room_id: room._id, spot_id: spot._id});
		if(!user) {
			success = false;
			message = "There are too many players connected to PokerKings.";
			socket.emit('getJoinRoomAuthorization', {success: success, message: message});
			console.log({success: success, message: message});
			return;
		}

		success = true;
		message = "Joining the room...";
		const roomRoute = "room/"+room._id;
		socket.emit('getJoinRoomAuthorization', {success: success, message: message, spot: spot._id, room: room._id});
		socket.join(roomRoute);

		const roomPlayers = await User.find({room_id: room._id}, {name: 1, avatar: 1, spot_id: 1, ready: 1});
		socket.getRequest = [];
		socket.getRequest.push({room: roomRoute, route: "getPreGamePlayerList", data: {players: roomPlayers}});
		console.log("Request successfully fulfilled!");
		next();

	} catch (e) {
		console.log(e.message);
	}
};

roomController.setReady = async function (obj, socket, next) {
	try {
		// Must check for edge cases
		const result = await User.findOneAndUpdate({room_id: obj.room_id, name: obj.name}, {ready: true});
		//var data = {};
		//socket.getRequest = [];

		//const playerList = await User.find({room_id: obj.room_id, ready: false}, {name: 1});

		socket.emit('getReadyPlayerAuthorization', {success: true});

		const roomRoute = "room/"+obj.room_id;
		const roomPlayers = await User.find({room_id: obj.room_id}, {name: 1, avatar: 1, spot_id: 1, ready: 1});
		socket.getRequest = [];
		socket.getRequest.push({room: roomRoute, route: "getPreGamePlayerList", data: {players: roomPlayers}});

		/*if(playerList.length == 0) { // Everybody is ready

			const roomPlayers = await User.find({room_id: room._id}, {name: 1, avatar: 1, spot_id: 1, ready: 1});
			socket.getRequest = [];
			socket.getRequest.push({room: roomRoute, route: "getPreGamePlayerList", data: {players: roomPlayers}});

			const copySocket = socket;
			core.startGame(obj, socket, next);
		} else {

			const roomPlayers = await User.find({room_id: room._id}, {name: 1, avatar: 1, spot_id: 1, ready: 1});
			socket.getRequest = [];
			socket.getRequest.push({room: roomRoute, route: "getPreGamePlayerList", data: {players: roomPlayers}});
		}*/
		/* else {
			//core.startGame(obj, socket, next);
			const indexOfLastElement = playerList.length-1;
			var emitMessage = "Waiting for: ";
			playerList.forEach((item, index) => {
				emitMessage += item.name;
				if(index != indexOfLastElement) {
					emitMessage += ", ";
				} else {
					emitMessage += "...";
				}
				data = {success: true, gameIsStarting: false, message: emitMessage};
			});
		}*/
		//console.log(socket.getRequest);
		//socket.getRequest.push({room: "room/"+obj.room_id, route: "getReady", data: data});
		console.log("Request successfully fulfilled!");
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
		console.log("Request successfully fulfilled!");
		return;
	} catch {
		console.log("Cannot retrieve players!");
		socket.emit('getPreGamePlayerList', {players: roomPlayers});
	}
};

module.exports = roomController;
