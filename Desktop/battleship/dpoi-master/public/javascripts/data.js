window.onload = function () {
    drawPie();
    drawBars();
}
function drawPie() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var text = xhttp.responseText;
            var dataSet = JSON.parse(text);

            var canvasWidth = 750, //width
                canvasHeight = 750,   //height
                outerRadius = 300,   //radius
                color = d3.scale.category20(); //builtin range of colors


            var vis = d3.select("#pie-container")
                .append("svg:svg") //create the SVG element inside the <body>
                .data([dataSet]) //associate our data with the document
                .attr("width", canvasWidth) //set the width of the canvas
                .attr("height", canvasHeight) //set the height of the canvas
                .append("svg:g") //make a group to hold our pie chart
                .attr("transform", "translate(" + 1.5 * outerRadius + "," + 1.5 * outerRadius + ")") // relocate center of pie to 'outerRadius,outerRadius'

// This will create <path> elements for us using arc data...
            var arc = d3.svg.arc()
                .outerRadius(outerRadius);

            var pie = d3.layout.pie() //this will create arc data for us given a list of values
                .value(function (d) {
                    return d.articlesCount;
                }) // Binding each value to the pie
                .sort(function (d) {
                    return null;
                });

// Select all <g> elements with class slice (there aren't any yet)
            var arcs = vis.selectAll("g.slice")
                // Associate the generated pie data (an array of arcs, each having startAngle,
                // endAngle and value properties)
                .data(pie)
                // This will create <g> elements for every "extra" data element that should be associated
                // with a selection. The result is creating a <g> for every object in the data array
                .enter()
                // Create a group to hold each slice (we will have a <path> and a <text>
                // element associated with each slice)
                .append("svg:g")
                .attr("class", "slice");    //allow us to style things in the slices (like text)

            arcs.append("svg:path")
                //set the color for each slice to be chosen from the color function defined above
                .attr("fill", function (d, i) {
                    return color(i);
                })
                //this creates the actual SVG path using the associated data (pie) with the arc drawing function
                .attr("d", arc);

// Add a legendLabel to each arc slice...
            arcs.append("svg:text")
                .attr("transform", function (d) { //set the label's origin to the center of the arc
                    //we have to make sure to set these before calling arc.centroid
                    d.outerRadius = outerRadius + 80; // Set Outer Coordinate
                    d.innerRadius = outerRadius + 75; // Set Inner Coordinate
                    return "translate(" + arc.centroid(d) + ")";
                })
                .attr("text-anchor", "middle") //center the text on it's origin
                .style("fill", "Purple")
                .style("font", "bold 8px Arial")
                .text(function (d, i) {
                    return dataSet[i].author;
                }); //get the label from our original data array

// Add a magnitude value to the larger arcs, translated to the arc centroid and rotated.
            arcs.filter(function (d) {
                return d.endAngle - d.startAngle > .2;
            }).append("svg:text")
                .attr("dy", ".35em")
                .attr("text-anchor", "middle")
                //.attr("transform", function(d) { return "translate(" + arc.centroid(d) + ")rotate(" + angle(d) + ")"; })
                .attr("transform", function (d) { //set the label's origin to the center of the arc
                    //we have to make sure to set these before calling arc.centroid
                    d.outerRadius = outerRadius; // Set Outer Coordinate
                    d.innerRadius = outerRadius / 2; // Set Inner Coordinate
                    return "translate(" + arc.centroid(d) + ")rotate(" + angle(d) + ")";
                })
                .style("fill", "White")
                .style("font", "bold 12px Arial")
                .text(function (d) {
                    return d.data.articlesCount;
                });
        }
    };
    xhttp.open("GET", "http://localhost:9000/getArticlesCountByAuthor", true);
    xhttp.send();
}
// Computes the angle of an arc, converting from radians to degrees.
function angle(d) {
    var a = (d.startAngle + d.endAngle) * 90 / Math.PI - 90;
    return a > 90 ? a - 180 : a;
}

function drawBars() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
        if (xhttp.readyState == 4 && xhttp.status == 200) {
            var text = xhttp.responseText;
            var barData = JSON.parse(text);
            var hours = [];
            var counts = [];
            var labels = [];

            for(var i in barData) {
                hours.push(barData[i].hour);
                counts.push(barData[i].count);
                labels.push(barData[i].hour + " hs: " + barData[i].count + " articles")
            }

            var xMaleLE = hours;
            var yFemaleLE = counts;
            var rMedianIncome =[5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5,5];
            var tCountry = labels;
            var cCountry = ["rgb(127, 201, 127)","rgb(190, 174, 212)","rgb(253, 192, 134)",
                "rgb(255, 255, 153)", "rgb(56, 108, 176)", "rgb(240, 2, 127)",
                "rgb(191, 91, 23)", "rgb(102, 102, 102)", "rgb(127, 201, 127)","rgb(190, 174, 212)","rgb(253, 192, 134)",
                "rgb(255, 255, 153)", "rgb(56, 108, 176)", "rgb(240, 2, 127)",
                "rgb(191, 91, 23)", "rgb(102, 102, 102)"];
            var margin = {top: 20, right: 15, bottom: 60, left: 60}
                , width = 1000 - margin.left - margin.right
                , height = 730 - margin.top - margin.bottom;
            var x = d3.scale.linear()
                .domain([0, 23 ])
                .range([ 0, width ]);
            var y = d3.scale.linear()
                .domain([0, d3.max(yFemaleLE) + 2])
                .range([ height, 0 ]);
            var r = d3.scale.linear()
                .domain([d3.min(rMedianIncome), d3.max(rMedianIncome)])
                .range([5, 35]);

            var chart = d3.select('#scatterplot')
                .append('svg:svg')
                .attr('width', width + margin.right + margin.left)
                .attr('height', height + margin.top + margin.bottom)
                .attr('class', 'chart');

            var main = chart.append('g')
                .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')')
                .attr('width', width)
                .attr('height', height)
                .attr('class', 'main')
            var xAxis = d3.svg.axis()
                .scale(x)
                .orient('bottom');

            main.append('g')
                .attr('transform', 'translate(0,' + height + ')')
                .attr('class', 'main axis date')
                .call(xAxis);
            var yAxis = d3.svg.axis()
                .scale(y)
                .orient('left');

            main.append('g')
                .attr('transform', 'translate(0,0)')
                .attr('class', 'main axis date')
                .call(yAxis);

            var g = main.append("svg:g");

            g.selectAll('scatterplot')
                .data(yFemaleLE) // using the values in the yFemaleLE array
                .enter().append("svg:circle")
                .attr("cy", function (d) { return y(d); } )
                .attr("cx", function (d,i) { return x(xMaleLE[i]); } )
                .attr("r", function(d,i){ return r(rMedianIncome[i]);})
                .style("fill", function(d, i){return cCountry[i];});

            g.selectAll('scatterplot')
                .data(yFemaleLE)
                .enter().append("text") //Add a text element
                .attr("y", function (d) { return y(d); })
                .attr("x", function (d,i) { return x(xMaleLE[i]); })
                .attr("dx", function(d,i){ return -r(rMedianIncome[i]);})
                .text(function(d, i){return tCountry[i];});

        }
    };

    xhttp.open("GET", "http://localhost:9000/getArticlesCountByHour", true);
    xhttp.send();
}



