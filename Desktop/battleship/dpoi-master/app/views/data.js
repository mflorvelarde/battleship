/**
 * Created by florenciavelarde on 6/4/16.
 */

window.onload = function () {
    draw();
}

function showLoading() {
    var element = document.getElementById("loading-gif");
    element.style.margin = 'auto';
    element.style.display = 'block';
}

function hideLoading() {
    var element = document.getElementById("loading-gif");
    element.style.display = 'none';
}

function load() {
    showLoading();
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var text = xhttp.responseText;
            var obj = JSON.parse(text);
            var items = obj.payload.items;
            var table = "<table id='users-table' class='full-width-table'><thead class='table-header'>" +
                "<tr><th rowspan='2'><h3>Firstname</h3></th><th rowspan='2'><h3>Lastname</h3></th><th rowspan='2'>" +
                "<h3>Mail</h3></th><th rowspan='2'><h3>Phone</h3></th><th colspan='3'><h3>Actions</h3></th>" +
                "</tr><tr><th><h3>View</h3></th><th><h3>Edit</h3></th><th><h3>Delete</h3></th></tr></thead><tbody>";

            for (var i in items) {
                var row = "<tr><td><label>" + items[i].firstname + "</label></td>" +
                    "<td><label>" + items[i].lastname + "</label></td>" +
                    "<td><label>" + items[i].mail + "</label></td>" +
                    "<td><label>" + items[i].phone + "</label></td>" +
                    "<td><i class='fa fa-eye' onclick='populateViewContainer(\"" + items[i].id + "\")'/></td>" +
                    "<td><i class='fa fa-pencil' onclick='populateEditForm(\"" + items[i].id + "\")'/></td>" +
                    "<td><i class='fa fa-times' onclick='deleteUser(\"" + items[i].id + "\")'/></td></tr>";

                table = table + row;
            }
            table = table + "</tbody></table>";
            document.getElementById("table-container").innerHTML = table;
            document.getElementById("new-user-button").style.display = 'block';
            hideLoading();
        }

    };
    xhttp.open("GET", "localhost9000/metrics", true);
    xhttp.send();
}

function draw() {
    showLoading();
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var text = xhttp.responseText;
            var obj = JSON.parse(text);
            alert(obj);

            var values = []

            for (var i in obj) {
                values.push(obj[i].articlesCount);
            }

            //var data = [1, 1, 2, 3, 5, 8, 13];

            var data = values;

            var canvas = document.querySelector("canvas"),
                context = canvas.getContext("2d");

            var width = canvas.width,
                height = canvas.height,
                radius = Math.min(width, height) / 2;

            var colors = [
                "#1f77b4", "#ff7f0e", "#2ca02c", "#d62728", "#9467bd",
                "#8c564b", "#e377c2", "#7f7f7f", "#bcbd22", "#17becf"
            ];

            var arc = d3.arc()
                .outerRadius(radius - 10)
                .innerRadius(0)
                .padAngle(0.03)
                .context(context);

            var pie = d3.pie();

            var arcs = pie(data);

            context.translate(width / 2, height / 2);

            context.globalAlpha = 0.5;
            arcs.forEach(function(d, i) {
                context.beginPath();
                arc(d);
                context.fillStyle = colors[i];
                context.fill();
            });

            context.globalAlpha = 1;
            context.beginPath();
            arcs.forEach(arc);
            context.lineWidth = 1.5;
            context.stroke();
        }
    };
    alert(1)
    xhttp.open("GET", "http://localhost:9000/getArticlesCountByAuthor", true);
    xhttp.send();
}
