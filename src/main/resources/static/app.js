let stompClient = null;

let Hand52 = {
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
    let socket = new SockJS('/secured/room');
    stompClient = Stomp.over(socket);
    let sessionId = "";

    stompClient.connect({}, function () {
        setConnected(true);
        let url = stompClient.ws._transport.url;
        url = url.replace("ws://localhost:8080/socket/secured/room/", "");
        url = url.replace("/websocket", "");
        url = url.replace(/^\d+\//, "");
        console.log("Your current session is: " + url);
        sessionId = url;

        stompClient.subscribe('/secured/user/queue/specific-user' + '-user' + that.sessionId, function (actions) {
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

function reveal() {
    stompClient.send("/app/revealSessionId", {}, "");
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
    $( "#reveal" ).click(function() { reveal(); });
});