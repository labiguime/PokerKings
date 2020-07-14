const room = require('../controllers/room.controller');
const io = require('socket.io')();

module.exports = (socket, next) => {
	socket.on('room/join', (message) => {
		console.log(message);
		console.log('Sending request');
		room.joinRoom(message, socket);
	});

	socket.on('room/setReady', (message) => {
		console.log(message);
		console.log('Sending request');
		room.setReady(message, socket);
	});

	socket.on('room/getPlayers', (message) => {
		console.log(message);
		console.log('Sending request');
		room.getPlayers(message, socket);
	});
	socket.jean = 3;
	next();
};
