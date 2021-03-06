const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const userSchema = new Schema({

	name: {
		type: String,
		required: [true, "User name must be provided."]
	},

	avatar: {
		type: String,
		default: 0
	},

	room_id: {
		type: Schema.Types.ObjectId,
		ref: 'Room'
	},

	spot_id: {
		type: Schema.Types.ObjectId,
		ref: 'Spot'
	},
	
	ready: {
		type: Boolean,
		default: false
	}
});

module.exports = mongoose.model('User', userSchema);
