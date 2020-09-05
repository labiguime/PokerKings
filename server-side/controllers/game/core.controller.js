const Cards = require('../../controllers/game/card.controller');
const constants = require('../../controllers/utils/game_constants');
const Room = require('../../models/room.model');
const Spot = require('../../models/spot.model');
const User = require('../../models/user.model');


let coreController = {};

coreController.startGame = async function (obj, socket, next) {
  try {
    console.log(socket.getRequest);
    const playerList = await User.find({room_id: obj.room_id}, {spot_id: 1, name: 1});
    if(!playerList) {
      console.log("Error: This room is empty but should have at least two players to start game. Room: "+obj.room_id);
      return;
    }

    var players_ids = [];
    playerList.forEach((item, i) => {
      players_ids.push(item._id);
    });

    // draw cards
    const nPlayers = playerList.length;
    const cards = Array.from(Cards.draw(constants.NUMBER_CARDS_TABLE+nPlayers));
    // distribute cards
    const roomCards = cards.slice(0, constants.NUMBER_CARDS_TABLE);
    const userCards = cards.slice(constants.NUMBER_CARDS_TABLE, (nPlayers*2)+constants.NUMBER_CARDS_TABLE);
    const roomUpdate = {
      users_cards: userCards,
      table_cards: roomCards,
      players_money: [10000, 10000, 10000, 10000],
      round_total_money: 0,
      room_total_money: 0,
      players_ids: players_ids,
      current_player: players_ids[0],
      game_stage: 0,
      current_minimum: 0
    };
    // populate game room Database
    const room = await Room.findOneAndUpdate({_id: obj.room_id}, roomUpdate);
    // tell player 1 to play
    socket.getRequest.push({room: "room/"+obj.room_id, route: "getCompleteRoomData", data: roomUpdate});
    next();
  } catch(e) {
    console.log("Error: "+ e);
  }


  // set countdown on acknowledgment
}

coreController.manageGame = async function (obj, socket, next) {
  // determine state
}

module.exports = coreController;
