const app = require('express')();
const server = require('http').Server(app).listen(7000);
const io = require('socket.io')(server);

const mongoose = require('mongoose');

app.get('/', (req, res) => {
	return res.send("You are here!");
});



io.on('connection', (sock) => {
	console.log("Connected!");
});

app.listen(3000, () => {
	console.log('The server is up and running on port 3000.');
});
