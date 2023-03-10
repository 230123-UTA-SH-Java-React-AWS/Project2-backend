var socket = null;

var Hand52 = {
    cards: [{suit:"CLUB", rank:"THREE"}, {suit:"DIAMOND", rank:"FIVE"}]
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#gamestate").show();
    }
    else {
        $("#gamestate").hide();
    }
    $("#actions").empty();
}

function connect() {
    socket = new WebSocket("ws://localhost:8080/gamestate");
    socket.onmessage = function(data) {
        console.log(data);
        showActions(data.data);
    };
    setConnected(true);
}

function disconnect() {
    if (socket !== null) {
        socket.close();
    }
    setConnected(false);
    console.log("Disconnected");
}

function hit() {
    socket.send(JSON.stringify(Hand52));
}

function stand() {
    socket.send(JSON.stringify(Hand52));
}

function showActions(message) {
    $("#actions").append("<tr><td>" + JSON.stringify(message) + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#hit" ).click(function() { hit(); });
    $( "#stand" ).click(function() { stand(); });
});