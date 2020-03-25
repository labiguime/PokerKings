const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const roomSchema = new Schema({
	name: {
		type: String,
		required: [true, "Room name must be provided."]
	},

	color: {
		type: Number,
		default: 0
	},

	players_in_room: {
		type: Number,
		default: 0
	},

	is_in_game: {
		type: Boolean,
		default: false
	},

	last_used: {
		type: Date,
		default: Date.now
	}
});

module.exports = mongoose.model('Room', roomSchema);
