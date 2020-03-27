const app = require('express')();
const server = require('http').Server(app).listen(7000);
const io = require('socket.io')(server);

const mongoose = require('mongoose');
try {
	mongoose.connect("mongodb://localhost/pokerkings", { useNewUrlParser: true, useUnifiedTopology: true });
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

io.on('connection', (socket) => {
	console.log("Client Id: ["+socket.id+"] has connected to the server.");
	socket.on('disconnect', () => {
		console.log("Client Id: ["+socket.id+"] has disconnected from the server.");
	});

});

io.use(require('./routes/room'));

app.listen(3000, () => {
	console.log('The server is up and running on port 3000.');
});
