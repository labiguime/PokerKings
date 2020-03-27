module.exports = (socket, next) => {
	socket.on('room/join', (message) => {
		console.log(message);
	});
	next();
};
