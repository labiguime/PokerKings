const express  = require('express');
const socket   = require('socket.io');
const mongoose = require('mongoose');

const app = express();

app.listen(3000, () => {
	console.log('The server is up and running on port 3000.');
});
