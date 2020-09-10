var constants = require('../../controllers/utils/game_constants');

function shuffle(array) {
  var currentIndex = array.length, temporaryValue, randomIndex;

  // While there remain elements to shuffle...
  while (0 !== currentIndex) {

    // Pick a remaining element...
    randomIndex = Math.floor(Math.random() * currentIndex);
    currentIndex -= 1;

    // And swap it with the current element.
    temporaryValue = array[currentIndex];
    array[currentIndex] = array[randomIndex];
    array[randomIndex] = temporaryValue;
  }

  return array;
}

module.exports.draw = function(q) {
  var array = [];
  for(var i = 0; i < constants.NUMBER_CARDS_DECK; i++) {
    array.push(i);
  }
  shuffle(array);
  array.slice(0, q);
  return array;
}
