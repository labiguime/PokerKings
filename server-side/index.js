const express  = require('express');
const socket   = require('socket.io');
const mongoose = require('mongoose');
const http = require('http');

const app = express();
const server = http.createServer(app);
const io = socket.listen(server);

app.listen(3000, () => {
	console.log('The server is up and running on port 3000.');
});

io.on('connection', (sock) => {
	console.log(sock);
});
