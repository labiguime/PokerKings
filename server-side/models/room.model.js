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

	players_names: [{
		type: String,
	}],

	still_in_round: [{
		type: String,
	}],

	current_player: {
		type: String,
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

	players_money: [{
		type: Number,
	}],

	small_blind: {
		type: String,
	},

	current_starting_player: {
		type: String,
	},

	current_ending_player: {
		type: String,
	},

	round_current_minimum: {
		type: Number,
	},

	round_players_bets: [{
		type: Number,
	}]

});

module.exports = mongoose.model('Room', roomSchema);
