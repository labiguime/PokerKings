const room = require('../../controllers/room.controller');

module.exports = (socket, next) => {

	socket.on("room/POST:join", (message) => {
		console.log('Request on: /routes/socket/room => POST:join');
		room.joinRoom(message, socket, next);
	});

	socket.on('room/POST:ready', (message) => {
    console.log('Request on: /routes/socket/room => POST:ready');
		room.setReady(message, socket, next);

	});

	/*socket.on('room/getPlayers', (message) => {
		//console.log(message);
		console.log('Sending request');

		room.getPlayers(message, socket, next);
	});*/
	next();
};
