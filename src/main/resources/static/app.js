let socket = null;
let stompClient = null;
let sessionId = "";

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
    socket = new SockJS('/secured/room');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function (frame) {
        let url = stompClient.ws._transport.url;
        url = url.replace("ws://localhost:8080/secured/room/", "");
        url = url.replace("/websocket", "");
        url = url.replace(/^[0-9]+\//, "");
        console.log("Your current session is: " + url);
        sessionId = url;

        setConnected(true);

        stompClient.subscribe('/secured/user/queue/specific-user' + '-user' + sessionId, function (action) {
            console.log(action.sessionid);
            console.log(action);
            Hand52.cards = action.cards;
            showActions(action);
        });
    });
}

function disconnect() {
    if (socket !== null) {
        socket.close();
    }
    setConnected(false);
    console.log("Disconnected");
}

function hit() {
    stompClient.send('/spring-security-mvc-socket/secured/room', {}, JSON.stringify(Hand52.cards));
}

function stand() {
    stompClient.send('/spring-security-mvc-socket/secured/room', {}, JSON.stringify(Hand52.cards));
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