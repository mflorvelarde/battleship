/**
 * Created by florenciavelarde on 17/7/18.
 */

function locateShip(x, y, orientation, length, id) {
    var cell = (10 * ( y - 1)) + x;
    if (orientation == "orient-v") {
        var last = cell + 10 * (length - 1);
        for (var j = cell; j <= last; j += 10) {
            board[j - 1] = id;
        }
    } else {
        for (var i = cell; i < (cell + length); i++) {
            board[i - 1] = id;
        }
    }
}

function rotateShip(id) {
    var element = document.getElementById(id);
    if (element.className.match(/(?:^|\s)orient-v(?!\S)/)) {
        document.getElementById(id).className = element.className.replace(/(?:^|\s)orient-v(?!\S)/g, ' ');
    } else {
        document.getElementById(id).className += " orient-v";
    }
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

            //var cell = (10 * ( fila - 1)) + columna;

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