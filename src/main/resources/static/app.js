var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('http://127.0.0.1:8082/boardCast');
    stompClient = Stomp.over(socket);
    var headers = {
      name: 'zcq',
      login: 'admin',
      passcode: '123'
    };
    var successCallback = function (frame) {
          console.log('Connected: ' + frame);
          setConnected(true);
          var subscribeHeaders = {ack: 'client', 'selector': "location = 'Europe'"};
          stompClient.subscribe('/topic/greetings', function (greeting) {
             var content = JSON.parse(greeting.body).content;
             console.log('content: '+ content);
             var ack = greeting.ack();
             console.log('ack: ' + ack);
             showGreeting(content);
          },subscribeHeaders);
      };
    var errorCallback = function (frame) {
          console.log('Connected: ' + frame);
    };
    stompClient.connect(headers, successCallback, errorCallback);
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

