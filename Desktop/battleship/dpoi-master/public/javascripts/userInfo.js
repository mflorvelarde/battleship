/**
 * Created by florenciavelarde on 14/6/16.
 */

window.onload = function () {

/*    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var text = xhttp.responseText;
            var obj = JSON.parse(text);
            var player = obj.payload.items;
            var username = "dsf";
            document.getElementById("user-name").innerHTML = username;
            getUserStatistics();
        }

    };
    xhttp.open("GET", "localhost9000/metrics", true);
    xhttp.send();*/
}

function getUserStatistics(id) {

}

function getPlayerName() {
    var facebookid = document.getElementById("facebookid").getAttribute("class");
    alert(facebookid);
}