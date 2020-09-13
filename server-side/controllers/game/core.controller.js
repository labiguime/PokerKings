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
      is_in_game: true,
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

    const updateRoom = await Room.findOneAndUpdate({_id: obj.room_id}, room, {new: true});
    if(!updateRoom) {
      console.log("Big error in the room");
    }

    let data = {
      has_round_ended: hasRoundEnded,
      game_stage: room.game_stage,
      table_card: room.table_cards[room.game_stage+2],
      next_player: room.players_ids.indexOf(room.current_player),
      action_type: actionType,
      who_played: room.players_ids.indexOf(me),
      winner: winner,
      is_game_over: isGameOver,
      player_new_money: room.players_money[playerIndex],
      player_money_change: obj.raise,
      table_total: room.round_total_money,
      all_cards: [0],
      current_minimum: room.round_current_minimum,
      my_index: -1,
      number_of_players: room.players_ids.length
    };
    if(room.game_stage < 3 && winner == null) { // still playing
      if(hasRoundEnded) {
        // give new card
        data["table_card"] = room.table_cards[room.game_stage+2];
      }

    } else { // this game is over
      data["table_card"] = -1;
      if(!winner) {
        data["all_cards"] = room.users_cards;
        data["is_game_over"] = true;
        
        // TODO: decide who is winner from the remaining players
        data["winner"] = room.players_ids.indexOf(room.still_in_round[0]); 

        // TODO: Reset the room to get ready for a new round
        resetRoom();
      }
    }

    room.players_ids.forEach((item, i) => {
      data["current_minimum"] = room.round_current_minimum-room.round_players_bets[i];
      data["my_index"] = i;
      let shallowCopy = Object.assign({}, data);
      socket.getRequest.push({room: "spot/"+item, route: "getRoomState", data: shallowCopy});
    });
  } catch (e) {
    console.log(e);
  }

  
  // determine state
  next();
} 

async function resetRoom() {

}

function rankCard(playerCards, tableCards) {

  // We group all the cards together
  let allCardsTogether = [];

  allCardsTogether.push(playerCards[0]);
  allCardsTogether.push(playerCards[1]);
  for(let i = 0; i < tableCards.length; i++) {
    allCardsTogether.push(tableCards[i]);
  }

  // We set up a ranks array to count each rank
  let ranks = new Array(13); 
  for (let i = 0; i < 13; i++) {
    ranks[i] = 0;
  }

  // Increment rank when found
  for(let i = 0; i < allCardsTogether.length; i++) {
    ranks[(allCardsTogether[i]-1)%13]++;
  }

  // Check for straight
  let straightCount = 0;
  let straightStartingRank = -1;
  for(let i = 12; i > -1; i--) {
    if(ranks[i] >= 1) {
      straightCount++;
    } else {
      straightCount = 0;
      straightStartingRank = -1;
    }
    // Straight has been found;
    if(straightCount == 5) {
      straightStartingRank = i+4;
      break;
    }
    // Impossible to get a straight out of 4 cards
    if(i < 3 && straightCount == 0) break;

    // Edge case for this straight: A 2 3 4 5
    if(straightCount == 4 && i==0) {
      if(ranks[12]>=1) {
        straightCount=5;
        straightStartingRank=3;
      }
    }
  }

  // Check for any flush
  let flushType = new Array(0,0,0,0);
  let flush = -1;
  for(let i = 0; i < allCardsTogether.length; i++) {
    flushType[Math.floor(allCardsTogether[i]/13)]++;
    if(flushType[Math.floor(allCardsTogether[i]/13)] == 5) {
      flush = allCardsTogether[i];
      break;
    }
  }

  // Check for similar cards(pairs, X of a kind)
  let fourOfAKind = -1;
  let threeOfAKind = -1;
  let pairs = [-1, -1];
  let strongest = new Array(-1, -1, -1, -1, -1);
  for(let i = 12; i > -1; i--) {
    if(ranks[i] == 4) {
      fourOfAKind = ranks[i];
    } else if(ranks[i] == 3 && threeOfAKind == -1) {
      threeOfAKind = ranks[i];
    } else if(ranks[i] == 2) {
      pairs[0] = (pairs[0] == -1) ? ranks[i] : pairs[0];
      pairs[1] = (pairs[0] != -1 && pairs[1] == -1) ? ranks[i] : pairs[1];
    } else {
      for(let u = 0; u < 5; u++) {
        if(strongest[u] == -1) {
          strongest[u] = ranks[i];
        }
      }
    }
  }

  let combinationName = "No pair";
  let cardsCombination = new Array(-1, -1, -1, -1, -1);

  
  if(straightStartingRank == 12 && allCardsTogether.indexOf(12+13*flush) != -1) { // Check for royal flush
    combinationName = "Royal flush";
    cardsCombination = new Array(12, 11, 10, 9, 8); // Check for straight flush
  } else if(straightCount == 5 && allCardsTogether.indexOf(straightStartingRank+13*flush) != -1) {
    combinationName = "Straight flush"
    cardsCombination = new Array(straightStartingRank, straightStartingRank-1, straightStartingRank-2, straightStartingRank-3, straightStartingRank-4);
  } else if(fourOfAKind != -1) { // check for of a kind
    combinationName = "Four of a kind";
    cardsCombination = new Array(fourOfAKind, fourOfAKind+13, fourOfAKind+13*2, fourOfAKind+13*3, strongest[0]);
  } else if(threeOfAKind != -1 && pairs[0] != -1) { // check for Full house
    combinationName = "Full house";
    cardsCombination = new Array(threeOfAKind, threeOfAKind+13, threeOfAKind+13*2, pairs[0], pairs[0]+13);
  } else if() { // Check for flush (must redefine strongest)
  } else if(straightCount == 5) { // Regular straight
    combinationName = "Straight";
    cardsCombination = new Array(straightStartingRank, straightStartingRank-1, straightStartingRank-2, straightStartingRank-3, (straightStartingRank-4)+13);
  } else if(threeOfAKind != -1) { // Three of a kind
    combinationName = "Three of a kind";
    cardsCombination = new Array(threeOfAKind, threeOfAKind+13, threeOfAKind+13*2, strongest[0], strongest[1]);
  } else if(pairs[0] != -1 && pairs[1] != -1) { // Two pairs
    combinationName = "Two pairs";
    cardsCombination = new Array(pairs[0], pairs[0]+13, pairs[1], pairs[1]+13, strongest[0]);
  } else if(pairs[0] != -1 && pairs[1] != -1) { // A pair
    combinationName = "A Pair";
    cardsCombination = new Array(pairs[0], pairs[0]+13, strongest[0], strongest[1], strongest[2]);
  } else { // No pair
    combinationName = "High card";
    cardsCombination = new Array(strongest[0], strongest[1], strongest[2], strongest[3], strongest[4]);
  }
}

module.exports = coreController;
