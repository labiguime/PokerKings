const Room = require('../models/room.model');
const Spot = require('../models/spot.model');
const User = require('../models/user.model');
let roomController = {};

roomController.joinRoom = async function (obj, socket, next) {
	try {
		const room = await Room.findOne({ name: obj.room });
		if(!room) {
			success = false;
			message = "This room doesn't exist.";
			socket.emit('joinRoom', {success: success, message: message});
			console.log({success: success, message: message});
			return;
		}

		if (room.is_in_game == true) {
			success = false;
			message = "A game is currently being played in this room.";
			socket.emit('joinRoom', {success: success, message: message});
			console.log({success: success, message: message});
			return;
		}

		const spot = await Spot.findOneAndUpdate({room_id: room._id, player_id: 'None'}, {player_id: socket.id});
		if(!spot) {
			success = false;
			message = "This room is full.";
			socket.emit('joinRoom', {success: success, message: message});
			console.log({success: success, message: message});
			return;
		}

		const user = await User.create({name: obj.name, avatar: obj.avatar, room_id: room._id, spot_id: spot._id});
		if(!user) {
			success = false;
			message = "There are too many players connected to PokerKings.";
			socket.emit('joinRoom', {success: success, message: message});
			console.log({success: success, message: message});
			return;
		}

		success = true;
		message = "Joining the room...";
		socket.emit('joinRoom', {success: success, message: message, spot: spot._id, room: room._id});
		socket.join("room/"+room._id);

	} catch (e) {
		console.log(e.message);
	}
};

roomController.setReady = async function (obj, socket, next) {
	try {
		// Must check for edge cases
		const result = await User.findOneAndUpdate({room_id: obj.room_id, name: obj.name}, {ready: true});

		const playerList = await User.find({ready: false}, {name: 1});
		if(playerList.length == 0) { // Everybody is ready
			socket.emit('getReady', {success: true, gameIsStarting: true, message: "The game is starting..."});
			socket.broadcast.emit('getReady', {success: true, gameIsStarting: true, message: "The game is starting..."});
		} else {
			const indexOfLastElement = playerList.length-1;
			var emitMessage = "Waiting for: ";
			playerList.forEach((item, index) => {
				emitMessage += item.name;
				if(index != indexOfLastElement) {
					emitMessage += ", ";
				} else {
					emitMessage += "...";
				}
			});
			console.log(emitMessage);
			socket.emit('getReady', {success: true, gameIsStarting: false, message: emitMessage});
			socket.broadcast.emit('getReady', {success: true, gameIsStarting: false, message: emitMessage});
			next();
		}
	} catch {
		console.log("Cannot retrieve ready players!");
		socket.emit('getReady', {success: false, gameIsStarting: false, message: null});
	}
};

roomController.getPlayers = async function (obj, socket, next) {
	try {
		const roomPlayers = await User.find({room_id: obj.room_id});
		console.log({players: roomPlayers});
		socket.emit('getPlayers', {players: roomPlayers});
		socket.broadcast.emit('getPlayers', {players: roomPlayers});
	} catch {
		console.log("Cannot retrieve players!");
		socket.emit('getPlayers', {players: roomPlayers});
	}
};

module.exports = roomController;
