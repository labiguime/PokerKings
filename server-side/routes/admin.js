const router = require('express').Router();

router.get('/reset_room', async function (req, res) {
	try {
		console.log('-------- reset_room command --');
		let result = await require('../models/room.model').deleteMany({});
		if(!result) {
			console.log("Admin: Couldn't reset room table!");
			return res.status(400);
		}
		console.log("Admin: Room table has been reset!");

		result = await require('../models/spot.model').deleteMany({});
		if(!result) {
			console.log("Admin: Couldn't reset spot table!");
			return res.status(400);
		}
		console.log("Admin: Spot table has been reset!");

		result = await require('../models/user.model').deleteMany({});
		if(!result) {
			console.log("Admin: Couldn't reset user table!");
			return res.status(400);
		}
		console.log("Admin: User table has been reset!");

		const room = new require('../models/room.model')({name: 'Room#1', color: 0, players_in_room: 0, is_in_game: false});
		result = await room.save();
		if(!result) {
			console.log('Admin: Cannot create room!');
			return res.status(400);
		}

		//const spot = new require('./models/spot.model')([]{room_id: result._id});
		result = await require('../models/spot.model').insertMany([{room_id: result._id}, {room_id: result._id}, {room_id: result._id}, {room_id: result._id}]);
		if(!result) { //
			console.log('Admin: Cannot create spots!');
			return res.status(400);
		}
		return res.send('Admin: Room table has been reset and new room has been created!');
	} catch(error) {
		console.log(error);
		return res.status(400);
	}
});

module.exports = router;
