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

app.get('/reset_room', (req, res) => {
	require('./models/room.model').remove({}, function(err) {
		if(err) {
			console.log("Couldn't reset room table!");
		}
		console.log("Room Table has been reset!");
	});
	const room = new require('./models/room.model')({name: 'Room#1', color: 0, players_in_room: 0, is_in_game: false});
	room.save(err => {
            if(err){
               return res.status(400).json({
                   error: "Cannot create room!",
                });
            } else {
				return res.status(200).json({result: "Room created!"});
			}
        });
});

io.on('connection', (socket) => {
	console.log("Client Id: ["+socket.id+"] has connected to the server.");
	socket.on('disconnect', () => {
		console.log("Client Id: ["+socket.id+"] has disconnected from the server.");
	});
});

app.listen(3000, () => {
	console.log('The server is up and running on port 3000.');
});
