module.exports = {
	testF: (socket, next) => {
		socket.on('test', () => {
				console.log("message received!");
		});
		next();
	}
}
