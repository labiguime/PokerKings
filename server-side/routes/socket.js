/*
* This module manages connections to the socket, makes use of middlewares for
* re-routing socket calls and implements global broadcast.
*/

exports = module.exports = (io) => {

  //
	io.on('connection', (socket) => {
		console.log("Client Id: ["+socket.id+"] has connected to the server.");
		socket.on('disconnect', () => {
			console.log("Client Id: ["+socket.id+"] has disconnected from the server.");
		});
	});

  // We check for all routes using middlewares
	io.use(require('../routes/socket/room'));

  // If socker.getRequest is provided room, route & data fields
	// it will be broadcast accordingly. It's a custom implementation of GET.
	io.use((socket, next) => {
		const getRequest = socket.getRequest;
		if(getRequest === undefined || getRequest.length == 0) next();
		else {
			getRequest.forEach((item, i) => {
				const room = item.room;
				const route = item.route;
				const data = item.data;
				io.in(room).emit(route, data);
				console.log('-- GET route '+route+' has been broadcast on '+room+'\n');
			});
			socket.getRequest = [];
			return;
		}
	});

};
