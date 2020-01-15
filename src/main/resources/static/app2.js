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
    var socket = new SockJS('http://127.0.0.1:8082/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).name);
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

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function sendName2() {
    stompClient.subscribe('/app/hello2', function (greeting) {
            showGreeting(JSON.parse(greeting.body).name);
    },{},{'name': $("#name").val(),'target':$("#target").val()});
}

function sendName3() {
    var adminJSON = JSON.stringify({'name': $("#name").val()});
        stompClient.send("/app/hello3", {}, adminJSON);
        stompClient.subscribe("user/app/hello3",function (message) {
            var content = message.body;
            var obj = JSON.parse(content);
            console.log("admin用户特定的消息1：" + obj.message);
        });
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
    $( "#send2" ).click(function() { sendName2(); });
    $( "#send3" ).click(function() { sendName3(); });
});

