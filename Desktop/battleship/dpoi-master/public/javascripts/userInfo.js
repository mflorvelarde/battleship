/**
 * Created by florenciavelarde on 14/6/16.
 */

var facebookId = 123456;
var ws;

$(function() {
    ws = new WebSocket($("body").data("ws-url"));
    return ws.onmessage = function(event) {
        var message;
        message = JSON.parse(event.data);
        alert(message);
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
});

function gameCreated (message) {
    gameName = message.gameName;
    alert(gameName);
}

function joinGame(){
    console.log("Entre");
    var array = {};
    array["type"] = "joinGame";
    array["facebookId"] = facebookId;
    array["name"] = "Test";
    var jsonFinale = JSON.stringify(array);
    console.log(ws);
    ws.send(jsonFinale);
    window.location.href = "/play";
}