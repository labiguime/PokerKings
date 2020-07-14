// Here we handle everything about the socket
exports = module.exports = (io) => {
	io.on('connection', (socket) => {
		console.log("Client Id: ["+socket.id+"] has connected to the server.");
		socket.on('disconnect', () => {
			console.log("Client Id: ["+socket.id+"] has disconnected from the server.");
		});
	});

	io.use(require('../routes/socket/room'));
	/*
	* Data to be broadcasted to an entire room will be passed to instructionSet
	* instructionSet contains the following field:
	*    room: A string that corresponds to the socket.io room to join
	*    route: The get function that is listened for by the Android app
	*    data: The data to pass to the Android app.
	*/

	io.use((socket, next) => {
		console.log("used");
		const instructionSet = socket.instructionSet;
		console.log(socket.instructionSet);
		if(instructionSet) {
			console.log(instructionSet);
			const room = instructionSet.room;
			const route = instructionSet.route;
			const data = instructionSet.data;
			io.to(room).emit(route, data);
		}
		next();
	});

};
