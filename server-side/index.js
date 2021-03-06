const app = require('express')();
const server = require('http').createServer(app);
const io = require('socket.io')(server);

// We load the module in charge of routing all socket.io requests
require('./routes/socket')(io);

const mongoose = require('mongoose');
try {
	mongoose.connect("mongodb://localhost/pokerkings", { useNewUrlParser: true, useUnifiedTopology: true, useFindAndModify: false });
} catch (error) {
	console.log('Database error: Could not establish connection.');
	console.log(error.message);
}

const database = mongoose.connection;

database.on('error', (error) => {
	console.log('Database error: ' + error.message);
});

database.on('open', (ref) => {
	console.log('The server has successfully connected to the database.');
});

app.get('/', (req, res) => {
	return res.send("You have landed on the server's main page.");
});

app.use('/admin/', require('./routes/admin'));

server.listen(3000, () => {
	console.log('The server is up and running on port 3000.');
});
