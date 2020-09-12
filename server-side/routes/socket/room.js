const room = require('../../controllers/game/room.controller');

module.exports = (socket, next) => {

	socket.on("room/POST:join", (message) => {
		console.log('-- Request on: /routes/socket/room => POST:join');
		room.joinRoom(message, socket, next);
	});

	socket.on('room/POST:ready', (message) => {
    console.log('-- Request on: /routes/socket/room => POST:ready');
		room.setReady(message, socket, next);
	});

	socket.on('room/POST:play', (message) => {
		console.log('-- Request on: /routes/socket/room => POST:play');
		room.play(message, socket, next);
	});

	socket.on('room/GET:preGamePlayerList', (message) => {
		console.log('-- Request on: /routes/socket/room => GET:preGamePlayerList');
		room.getPreGamePlayerList(message, socket, next);
	});

	next();
};
