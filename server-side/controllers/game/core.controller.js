require('../../controllers/game/card.controller');
require('../../controllers/utils/game_constants');
const Room = require('../../models/room.model');
const Spot = require('../../models/spot.model');
const User = require('../../models/user.model');


let coreController = {};

coreController.startGame = async function (obj, socket, next) {
  const playerList = await User.find({room_id: obj.room_id}, {spot_id: 1, name: 1});
  if(!playerList) {
    console.log("Error: no players in room whom should have at least two. Room: "+obj.room_id);
    return;
  }
  // draw cards
  const nPlayers = playerList.length;
  const cards = drawCards(NUMBER_CARDS_TABLE+nPlayers);
  // distribute cards
  const roomCards = cards.slice(0, NUMBER_CARDS_TABLE);
  const userCards = cards.slice(NUMBER_CARDS_TABLE, nPlayers);
  // populate game room Database
  // tell player 1 to play
  // set countdown on acknowledgment
}

coreController.manageGame = async function (obj, socket, next) {
  // determine state
}

module.exports = coreController;
