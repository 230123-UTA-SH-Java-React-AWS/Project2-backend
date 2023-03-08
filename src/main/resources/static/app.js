var stompClient = null;

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
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/gamestate', function (actions) {
            console.log(actions);
            Hand52.cards = JSON.parse(actions.body).cards;
            showActions(JSON.parse(actions.body).cards);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function hit() {
    stompClient.send("/app/hit", {}, JSON.stringify(Hand52.cards));
}

function stand() {
    stompClient.send("/app/stand", {}, JSON.stringify(Hand52.cards));
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