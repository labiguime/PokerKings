require('../../controllers/game/card.controller');
require('../../controllers/utils/game_constants');
const Room = require('../../models/room.model');
const Spot = require('../../models/spot.model');
const User = require('../../models/user.model');


let coreController = {};

coreController.startGame = async function (obj, socket, next) {
  const playerList = await User.find({room_id: obj.room_id}, {spot_id: 1, name: 1});
  if(!playerList) {
    console.log("Error: This room is empty but should have at least two players to start game. Room: "+obj.room_id);
    return;
  }

  const room = await Room.find({_id: obj.room_id});
  if(!room) {
    console.log("Error: Room id "+obj.room_id+" does not exist.");
    return;
  }
  // draw cards
  const nPlayers = playerList.length;
  const cards = drawCards(NUMBER_CARDS_TABLE+nPlayers);
  // distribute cards
  const roomCards = cards.slice(0, NUMBER_CARDS_TABLE);
  const userCards = cards.slice(NUMBER_CARDS_TABLE, (nPlayers*2)+NUMBER_CARDS_TABLE);
  // populate game room Database
  
  // tell player 1 to play
  // set countdown on acknowledgment
}

coreController.manageGame = async function (obj, socket, next) {
  // determine state
}

module.exports = coreController;
