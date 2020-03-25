const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const userSchema = new Schema({

	name: {
		type: String,
		required: [true, "User name must be provided."]
	},

	avatar: {
		type: Number,
		default: 0
	},

	room_id: {
		type: Schema.Types.ObjectId,
		ref: 'Room'
	},

	spot_id: {
		type: Schema.Types.ObjectId,
		ref: 'Spot'
	}
});

module.exports = mongoose.model('User', userSchema);
