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
		const getRequest = socket.getRequest;
		if(getRequest) {
			const room = getRequest.room;
			const route = getRequest.route;
			const data = getRequest.data;
			io.in(room).emit(route, data);
			console.log('-- GET route '+route+' has been broadcast on '+room);
			return;
		}
		next();
	});

};
