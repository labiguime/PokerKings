const Cards = require('../../controllers/game/card.controller');
const constants = require('../../controllers/utils/game_constants');
const Room = require('../../models/room.model');
const Spot = require('../../models/spot.model');
const User = require('../../models/user.model');
const roomController = require('./room.controller');


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
      current_ending_player: players_ids[1],
      round_current_minimum: constants.START_MINIMUM_BET,
      round_players_bets: [Math.floor((constants.START_MINIMUM_BET)/2), constants.START_MINIMUM_BET, 0, 0]
    };
    // populate game room Database
    const room = await Room.findOneAndUpdate({_id: obj.room_id}, roomUpdate, {new: true});

    players_ids.forEach((item, i) => {
      const player_minimum = roomUpdate.round_current_minimum-roomUpdate.round_players_bets[i];
      socket.getRequest.push({room: "spot/"+item, route: "getInitialRoomData", data: {my_index: i, number_of_players: nPlayers, current_minimum: player_minimum, current_player: (nPlayers==2?0:2), start_money: roomUpdate.players_money[i], card_1: userCards[0+i*2], card_2: userCards[0+i*2+1], table_card_1: roomCards[0], table_card_2: roomCards[1], table_card_3: roomCards[2]}});
    });
    next();
  } catch(e) {
    console.log("Error: "+ e);
  }
  // set countdown on acknowledgment
}

coreController.manageGame = async function (obj, socket, next, room) {

  try {
    // TODO: Set func parameter back to room
    //const room = await Room.find({room_id: obj.room_id});
    const playerIndex = room.players_ids.indexOf(obj.spot_id);
    const me = obj.spot_id;
    let hasRoundEnded = false;
    let winner = null;
    let actionType = 0;
    let isGameOver = false;
    if(obj.is_folding) {
      actionType = 1;
      if(room.still_in_round.length == 2) {
        // Round winner is the one that's not me
        hasRoundEnded = true;
        room.still_in_round.splice(playerIndex, 1);
        winner = room.players_ids.indexOf(room.still_in_round[0]);
        isGameOver = true;
      }
      else if(room.current_ending_player == me) {
        // go to next round
        hasRoundEnded = true;
        const newIndex = (room.still_in_round.indexOf(room.current_starting_player)-1)%room.still_in_round.length;
        room.current_player = room.current_starting_player;
        room.current_ending_player = room.still_in_round[newIndex];
      } else {
        const newIndex = (room.still_in_round.indexOf(me)+1)%room.still_in_round.length;
        if(room.current_starting_player == me) {
          room.current_starting_player = room.still_in_round[newIndex];
        }
        room.current_player = room.still_in_round[newIndex];
        room.still_in_round.splice(playerIndex, 1);
      } 
    } else {
      const absoluteRaise = obj.raise+room.round_players_bets[playerIndex];
      room.players_money[playerIndex] -= obj.raise;
      room.round_total_money += obj.raise;
      room.round_players_bets[playerIndex] = absoluteRaise;
      

    // TODO: DONT FORGET TO CHANGE CHECKS FOR RAISE IN ROOM CONTROLLER
      if(absoluteRaise > room.round_current_minimum) { // It's an increase
        actionType = 3;
        // The new ender is the one before me
        const newIndex = (room.still_in_round.indexOf(me)-1)%room.still_in_round.length;
        room.round_current_minimum = absoluteRaise;
        room.current_ending_player = room.still_in_round[newIndex];

        const increasedIndex = (room.still_in_round.indexOf(me)+1)%room.still_in_round.length;
        room.current_player = room.still_in_round[increasedIndex];
        // It should then continue normally
      } else { // It's a match
        actionType = 2;
        if(room.current_ending_player == me) {
          // end of the round, next round please
          const newIndex = (room.still_in_round.indexOf(room.current_starting_player)-1)%room.still_in_round.length;
          room.current_player = room.current_starting_player;
          room.current_ending_player = room.still_in_round[newIndex];
          
          // set current_ending_player
          hasRoundEnded = true;
        } else {
          const increasedIndex = (room.still_in_round.indexOf(me)+1)%room.still_in_round.length;
          room.current_player = room.still_in_round[increasedIndex];
          // Continue the round normally
        }
      }
    }

    // TODO: Change current player

    // Who is playing next ?
    // What's the current minimum ?
    // Who has won ?
    // Who just played?
    // Have they folded, matched or increased?
    // What's their new money?
    // By how much have their money changed?
    // What's the new total on the table?
    // What's the new card on the table?
    // What is everyone's card in case the game came to the last round


    if(hasRoundEnded) {
      room.game_stage += 1;
      room.round_current_minimum = 0;
      room.round_players_bets = [0, 0, 0, 0];
    }

    const data = {
      has_round_ended: hasRoundEnded,
      table_card: room.table_cards[room.game_stage+2],
      next_player: room.players_ids.indexOf(room.current_player),
      action_type: actionType,
      who_played: room.players_ids.indexOf(me),
      winner: winner,
      is_game_over: isGameOver,
      player_new_money: room.players_money[playerIndex],
      player_money_change: obj.raise,
      table_total: room.round_total_money,
      all_cards: null,
      current_minimum: room.round_current_minimum,
      my_index: -1,
      number_of_players: room.players_in_room.length
    };
    if(room.game_stage < 3 && winner == null) { // still playing
      if(hasRoundEnded) {
        // give new card
        data.push({table_card: room.table_cards[room.game_stage+2]});
      }

    } else { // this game is over
      if(!winner) {
        data["all_cards"] = room.users_cards;
        data["is_game_over"] = true;
        // TODO: decide who is winner from the remaining players
        winner = room.still_in_round[0]; 
        resetRoom();
      }
    }

    room.players_ids.forEach((item, i) => {
      data["current_minimum"] = room.round_current_minimum-room.round_players_bets[i];
      data["my_index"] = i;
      socket.getRequest.push({room: "spot/"+item, route: "getRoomState", data});
    });
  } catch (e) {
    console.log(e);
  }

  
  // determine state
  next();
} 

async function resetRoom() {

}

module.exports = coreController;
