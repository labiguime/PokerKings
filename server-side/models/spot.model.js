const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const spotSchema = new Schema({
	room_id: {
		type: Schema.Types.ObjectId,
		ref: 'Room',
		required: true
	},

	player_id: {
		type: String,
		default: 'None'
	},

	socket_id: {
		type: String,
		default: 'None'
	}
});

module.exports = mongoose.model('Spot', spotSchema);
