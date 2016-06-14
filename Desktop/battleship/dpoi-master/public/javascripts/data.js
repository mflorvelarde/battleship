$(function () {
    $(".draggable").draggable({
        grid: [50, 50], containment: ".containment-wrapper", scroll: false,
        stop: function() {
            var columna = ( Math.floor(($(this).position().left) / 50) ) + 1;
            var fila = Math.floor(($(this).position().top) / 50)
            alert(fila);

        }
    });
});

function rotateShip(id) {
    var element = document.getElementById(id);
    if (element.className.match(/(?:^|\s)orient-v(?!\S)/) ) {
        document.getElementById(id).className = element.className.replace(/(?:^|\s)orient-v(?!\S)/g , '' );
    } else {
        document.getElementById(id).className += " orient-v";
    }
}

function shoot(element) {
    var fila = ((element.offsetTop) - 20 ) / 50;
    var columna = ((element.offsetLeft - 15 ) / 50 ) + 1;
    alert("shoot");
    turnCellToFire(element);
}

function turnCellToFire(element) {
    element.className += " fired-cell";
    element.disabled = true;
    $element.prop( "onclick", null );
    console.log( "onclick property: ", $element[ 0 ].onclick );}

function turnCellToWater(element) {
    element.className += " water-cell"
}