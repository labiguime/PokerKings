const Cards = require('../../controllers/game/card.controller');
const constants = require('../../controllers/utils/game_constants');
const Room = require('../../models/room.model');
const Spot = require('../../models/spot.model');
const User = require('../../models/user.model');


let coreController = {};

coreController.startGame = async function (obj, socket, next) {
  try {
    const playerList = await User.find({room_id: obj.room_id}, {spot_id: 1, name: 1});
    if(!playerList) {
      console.log("Error: This room is empty but should have at least two players to start game. Room: "+obj.room_id);
      return;
    }
    
    var players_ids = [];
    playerList.forEach((item, i) => {
      players_ids.push(item.spot_id);
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
      players_money: [constants.START_MONEY-(Math.floor((constants.START_MINIMUM_BET)/2)), constants.START_MONEY-(constants.START_MINIMUM_BET), constants.START_MONEY, constants.START_MONEY],
      round_total_money: 0,
      room_total_money: 0,
      players_ids: players_ids,
      still_in_round: players_ids,
      players_in_room: nPlayers,
      current_player: players_ids[(nPlayers==2?0:2)],
      game_stage: 0,
      current_minimum: constants.START_MINIMUM_BET,
      big_blind: players_ids[1],
      player_who_started: players_ids[(nPlayers==2?0:2)],
      current_starting_player: players_ids[(nPlayers==2?0:2)],
      current_ending_player: players_ids[1]
    };
    // populate game room Database
    const room = await Room.findOneAndUpdate({_id: obj.room_id}, roomUpdate, {new: true});

    players_ids.forEach((item, i) => {
      socket.getRequest.push({room: "spot/"+item, route: "getInitialRoomData", data: {my_index: i, number_of_players: nPlayers, current_minimum: constants.START_MINIMUM_BET, current_player: (nPlayers==2?0:2), start_money: constants.START_MONEY, card_1: userCards[0+i*2], card_2: userCards[0+i*2+1], table_card_1: roomCards[0], table_card_2: roomCards[1], table_card_3: roomCards[2]}});
    });
    next();
  } catch(e) {
    console.log("Error: "+ e);
  }
  // set countdown on acknowledgment
}

coreController.manageGame = async function (obj, socket, next, room) {

  const playerIndex = room.players_ids.indexOf(obj.spot_id);
  const me = obj.spot_id;

  if(obj.is_folding) {
    if(room.still_in_round.length == 2) {
      // Round winner is the one that's not me
    }
    else if(room.current_ending_player == me) {
      // go to next round
    } else if(room.current_starting_player == me) {
      // Change the player who's supposed to start
    } else {
      room.still_in_round.splice(playerIndex, 1);
      // prepare for next player
    }
  } else {
    const absoluteRaise = obj.raise+room.round_players_bets[playerIndex];
    room.players_money[playerIndex] -= obj.raise;
    room.round_total_money += obj.raise;

  // TODO: DONT FORGET TO CHANGE CHECKS FOR RAISE IN ROOM CONTROLLER
    if(absoluteRaise > room.round_current_minimum) { // It's an increase
      // The new ender is the one before me
    } else { // It's a match
      // Continue the round normally, check for if it's ended
    }
    // TODO: If raise is more than current minimum we should do something
    // change current ending player to be the one before me

  }
  
  room.current_player = (room.still_in_round.indexOf(me)+1)%room.still_in_round.length;

  if(room.still_in_round.length == 1) {
    // Round winner

  } else {
    if(room.current_ending_player == me) {

      // depends on starting min and ending min bet
      if(obj.is_folding) {
        // it's next round
      }
    }
  }
  

  
  
  // determine state
  next();
} 

module.exports = coreController;
