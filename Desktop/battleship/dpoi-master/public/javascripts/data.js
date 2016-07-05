window.onload = function() {
 alert("Waiting for an oponent to join. You can locate your ships");
 document.getElementById("ready-button").style.display = 'none';
};

var facebookId = 1234;
var gameName;
var ws;
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

$(function() {
    ws = new WebSocket($("body").data("ws-url"));
    // joinGame();
    ws.onmessage = function(event) {
        var message;
        message = JSON.parse(event.data);
        console.log(message);
        switch (message.type) {
            case "gameCreated":
                return gameCreated(message);
            case "shootResult":
                return shootResult(message);
            case "receiveShoot":
                return receiveShot(message);
            case "endGame":
                return endGame(message);
            case "yourTurn":
                return yourTurn(message);
            default:
                return console.log(message);
        }
    };
    ws.onopen = function () {
        joinGame();
    }
});

function gameCreated (message) {
    this.gameName = message.gameName;
    alert("Your opponent joined. Let's play!");
    document.getElementById("ready-button").style.display = 'block';
}

function yourTurn(message) {
/*    document.getElementById("player-header").style.background = "#87CEEB !important";
    document.getElementById("opponent-header").style.background = "#4169E1 !important";*/
    document.getElementById("player-header").className += " active-player";
    if (document.getElementById("opponent-header").className.match(/(?:^|\s)active-player(?!\S)/)) {
        document.getElementById("opponent-header").className = document.getElementById("opponent-header").className.replace(/(?:^|\s)active-player(?!\S)/g, ' ');
    }

    alert("It's your turn to play");

    var elements = document.getElementsByClassName("active-cell");
    for(var i =0 ;  i < elements.length; i++) {
        elements[i].setAttribute("onclick", "shoot(this)");
   }
}

function shootResult(message) {
    var rowResult = message.row;
    var colResult = message.col;
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

function endGame(message) {
    alert(message);
}


function joinGame () {
    var array = {};
    array["type"] = "joinGame";
    array["facebookId"] = facebookId;
    array["name"] = "Test";
    var jsonFinale = JSON.stringify(array);
    ws.send(jsonFinale);
}

$(function () {
    $(".draggable").draggable({
        grid: [50, 50], containment: ".containment-wrapper", scroll: false,
        stop: function () {
            var columna = ( Math.floor(($(this).position().left) / 50) ) + 1;
            var fila = Math.floor(($(this).position().top) / 50);
            console.log("columna: " + columna);
            console.log("fila: " + fila);
            var orientation;
            var length;
            var id = this.getAttribute("id");

            if (this.className.match(/(?:^|\s)orient-v(?!\S)/)) {
                orientation = "orient-v";
            }
            else {
                orientation = "orient-h";
            }

            if (this.className.match(/(?:^|\s)length-1(?!\S)/)) length = 1;
            else if (this.className.match(/(?:^|\s)length-2(?!\S)/)) length = 2;
            else length = 3;

            var cell = (10 * ( fila - 1)) + columna;

            for (var i = 1; i <= 100; i++) {
                if (board[i] == id) board[i] = 0;
            }

            if (verifyCellsAvailability(columna, fila, orientation, length)) {
                if (document.getElementById(id).className.match(/(?:^|\s)wrong-location(?!\S)/)) {
                    document.getElementById(id).className = document.getElementById(id).className.replace(/(?:^|\s)wrong-location(?!\S)/g, '');
                }
                locateShip(columna, fila, orientation, length, id);
            }
            else {
                document.getElementById(id).className += " wrong-location";
                alert("Ops! You already have a ship on that position");
            }


        }
    });
});

function rotateShip(id) {
    var element = document.getElementById(id);
    if (element.className.match(/(?:^|\s)orient-v(?!\S)/)) {
        document.getElementById(id).className = element.className.replace(/(?:^|\s)orient-v(?!\S)/g, ' ');
    } else {
        document.getElementById(id).className += " orient-v";
    }
}

function shoot(element) {
    this.currentCell = element;
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


function turnCellToFire(element) {
    element.className += " fired-cell";
/*    element.disabled = true;
    element.prop("onclick", null);
    console.log("onclick property: ", $element[0].onclick);*/
}

function turnCellToWater(element) {
    element.className += " water-cell"
}

function locateShip(x, y, orientation, length, id) {
    var cell = (10 * ( y - 1)) + x;
    if (orientation == "orient-v") {
        var last = cell + 10 * (length - 1);
        for (var j = cell; j <= last; j += 10) {
            board[j - 1] = id;
        }
    } else {
        for (i = cell; i < (cell + length); i++) {
            board[i - 1] = id;
        }
    }
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


function setReadyToPlay(element) {
    element.style.display = 'none';
    var id;
    $(".draggable").draggable('disable');
    var locationShip1 = [];
    var locationShip2 = [];
    var locationShip3 = [];
    var locationShip4 = [];
    var locationShip5 = [];
    var locationShip6 = [];
    var locationShip7 = [];
    var locationShip8 = [];
    for (var i = 0; i < board.length; i++) {
        if (board[i] != 0) {
            if (board[i] == "ship-1") {
                locationShip1.push(i);
            }
            if (board[i] == "ship-2") {
                locationShip2.push(i);
            }
            if (board[i] == "ship-3") {
                locationShip3.push(i);
            }
            if (board[i] == "ship-4") {
                locationShip4.push(i);
            }
            if (board[i] == "ship-5") {
                locationShip5.push(i);
            }
            if (board[i] == "ship-6") {
                locationShip6.push(i);
            }
            if (board[i] == "ship-7") {
                locationShip7.push(i);
            }
            if (board[i] == "ship-8") {
                locationShip8.push(i);
            }
        }
    }
    setShipReady(locationShip1);
    setShipReady(locationShip2);
    setShipReady(locationShip3);
    setShipReady(locationShip4);
    setShipReady(locationShip5);
    setShipReady(locationShip6);
    setShipReady(locationShip7);
    setShipReady(locationShip8);

    var ready = {};

    ready["type"] = "ready";
    ready["gameName"] = gameName;
    var json = JSON.stringify(ready);
    ws.send(json);

}


function setShipReady(locationShip) {
    if (locationShip.length === 0) return;
    var shipRow;
    var shipCol;
    var arrayRows = [];
    var arrayCols = [];
    var jsonObj = [];
    var arrayFinal = {};
    var setShip = {};

    for (var j = 0; j < locationShip.length; j++) {
        shipRow = Math.floor(locationShip[j] / 10) + 1;
        shipCol = locationShip[j] % 10 + 1;
        arrayRows.push(shipRow);
        arrayCols.push(shipCol);
    }
    setShip["type"] = "setShip";
    setShip["gameName"] = gameName;
    setShip["row"] = arrayRows;
    setShip["col"] = arrayCols;
    setShip["len"] = locationShip.length;
    arrayFinal[""] = setShip;
    jsonObj.push(arrayFinal);
    var jsonFinale = JSON.stringify(setShip);
    ws.send(jsonFinale);
}


function openWinnningModal() {
    alert("Congratulations: you won!");
    window.location.href = "/";
}

function openLoosingModal() {
    alert("Ooohh: you lost!");
    window.location.href = "/";
}
function leaveGame(){
    var jsonObj = [];
    var leftGame = {};
    var array = {};
    array["type"] = "leaveGame";
    array["gameName"] = gameName;
    leftGame[""] = array;
    jsonObj.push(leftGame);
    var jsonFinale = JSON.stringify(array);
    ws.send(jsonFinale);
    window.location.href = "/";
}