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
	},

	table_cards: [{
		type: Integer,
	}],

	users_cards: [{
		type: Integer,
	}],

	players_ids: [{
		type: Integer,
	}],

	current_player: {
		type: Integer,
	},

	game_stage: {
		type: Integer,
	},

	current_minimum: {
		type: Integer,
	},

	room_total_money: {
		type: Integer,
	},

	round_total_money: {
		type: Integer,
	},

	players_money: [{
		type: Integer,
	}]
});

module.exports = mongoose.model('Room', roomSchema);
