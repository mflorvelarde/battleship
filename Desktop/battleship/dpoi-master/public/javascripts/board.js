/**
 * Created by florenciavelarde on 17/7/18.
 */
var currentCell;

var board = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
    0, 0, 0, 0, 0, 0, 0, 0, 0, 0
];

function turnCellToFire(element) {
    element.className += " fired-cell";
    /*    element.disabled = true;
     element.prop("onclick", null);
     console.log("onclick property: ", $element[0].onclick);*/
}

function turnCellToWater(element) {
    element.className += " water-cell"
}

function verifyCellsAvailability(x, y, orientation, length) {
    var cell = (10 * ( y - 1)) + x;
    if (orientation == "orient-v") {
        var last = cell + 10 * (length - 1);
        for (i = cell; i <= last; i += 10) {
            if (board[i - 1] != 0) return false;
        }
    } else {
        for (i = cell; i < (cell + length); i++) {
            if (board[i - 1] != 0) return false;
        }
    }
    return true;
}

function receiveShot(message) {
    var x = message.col;
    var y = message.row;
    var cellNumber = (10 * ( y - 1)) + x;
    var cellId = "cell-" + cellNumber.toString();
    if (board[cellNumber - 1] == 0) document.getElementById(cellId).className += " water-cell";
    else document.getElementById(cellId).className += " fired-cell";

    if(message.result === "loose") openLoosingModal();
}

function shootResult(message) {
    /* var rowResult = message.row;
     var colResult = message.col;*/
    var result = message.result;

    if(result == "sink") {
        turnCellToFire(currentCell);
        alert("You sank the ship!");
    } else if(result == "hit") {
        turnCellToFire(currentCell);
        alert("You hit a ship!");
    } else if(result == "miss") {
        turnCellToWater(currentCell);
    }else if (result == "win") {
        turnCellToFire(currentCell);
        openWinnningModal();
    } else {
        turnCellToWater(currentCell);
    }
}

function shoot(element) {
    //  this.currentCell = element;
    var fila = ((element.offsetTop) - 20 ) / 50;
    var columna = ((element.offsetLeft - 15 ) / 50 ) + 1;
    var jsonObj = [];
    var arrayFinal = {};
    var shoot = {};
    var row = {};
    var col = {};
    shoot["type"] = "shoot";
    shoot["gameName"] = gameName;
    shoot["row"] = fila;
    shoot["col"] = columna;
    arrayFinal[""] = shoot;
    jsonObj.push(arrayFinal);
    var jsonFinale = JSON.stringify(shoot);
    ws.send(jsonFinale);

    element.removeAttribute("onclick");
    element.className = element.className.replace(/(?:^|\s)active-cell(?!\S)/g, ' ');

    var elements = document.getElementsByClassName("active-cell");
    for(var i =0 ;  i < elements.length; i++) {
        elements[i].removeAttribute("onclick")
    }

    document.getElementById("opponent-header").className += " active-player";
    document.getElementById("player-header").className = document.getElementById("opponent-header").className.replace(/(?:^|\s)active-player(?!\S)/g, ' ');

}

