const Room = require('../models/room.model');
const Spot = require('../models/spot.model');
const User = require('../models/user.model');
let roomController = {};

roomController.joinRoom = async function (obj, socket) {
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

		const roomPlayers = await User.find({room_id: room._id});
		success = true;
		message = "Joining the room...";
		socket.emit('joinRoom', {success: success, message: message, roomPlayers: roomPlayers});
		console.log({success: success, message: message, roomPlayers: roomPlayers});
		return;

	} catch (e) {
		console.log(e.message);
	}
};

module.exports = roomController;
