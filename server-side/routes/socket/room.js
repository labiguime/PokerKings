const room = require('../../controllers/room.controller');


module.exports = (socket, next) => {
  console.log("Comes here at least");
	socket.on('room/join', (message) => {

		//console.log(message);
		console.log('Sending request');
		//io.emit("testtest");
		room.joinRoom(message, socket, next);
	});

	socket.on('room/POST:ready', (message) => {
		//console.log(message);
		console.log('Sending request');
		//io.emit("testtest");
		room.setReady(message, socket, next);

	});

	/*socket.on('room/getPlayers', (message) => {
		//console.log(message);
		console.log('Sending request');

		room.getPlayers(message, socket, next);
	});*/
	next();
};
