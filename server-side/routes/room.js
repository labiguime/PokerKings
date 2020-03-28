const room = require('../controllers/room.controller');
const io = require('socket.io')();

module.exports = (socket, next) => {
	socket.on('room/join', (message) => {
		console.log(message);
		console.log('Sending request');
		room.joinRoom(message, socket);
	});

	next();
};
