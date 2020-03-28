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
			socket.emit('onJoinRoom', {success: success, message: message});
		}
		if (room.is_in_game == True) {
			success = false;
			message = "A game is currently being played in this room.";
			socket.emit('onJoinRoom', {success: success, message: message});
		}

		const spot = await Spot.findOneAndUpdate({room_id: room._id, player_id: 'None'}, {player_id: socket.id});
		if(!spot) {
			success = false;
			message = "This room is full.";
			socket.emit('onJoinRoom', {success: success, message: message});
		}

		const user = await User.save({name: obj.name, avatar: obj.avatar, room_id: room._id, spot_id: spot._id});
		if(!user) {
			success = false;
			message = "There are too many players connected to PokerKings.";
			socket.emit('onJoinRoom', {success: success, message: message});
		}


	} catch (e) {

	}
};

module.exports = roomController;
