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
		type: Number,
	}],

	users_cards: [{
		type: Number,
	}],

	players_ids: [{
		type: String,
	}],

	current_player: {
		type: Number,
	},

	game_stage: {
		type: Number,
	},

	current_minimum: {
		type: Number,
	},

	room_total_money: {
		type: Number,
	},

	round_total_money: {
		type: Number,
	},

	players_money: [{
		type: Number,
	}],

	first_to_start: {
		type: Number,
		default: 0
	},
});

module.exports = mongoose.model('Room', roomSchema);
