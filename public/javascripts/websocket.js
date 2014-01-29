var socket;

function message(msgString) {
	
	var temp = document.createElement("div");
	temp.innerHTML = msgString;
	var board = document.getElementById("board-container");
	board.appendChild(temp.firstChild);
}

function sendMessage() {
	var inputName = document.getElementById("input-name").value;
	var inputText = document.getElementById("input-text").value;

	socket.send("msg:" + inputName + ":" + inputText)
}

function createWebSocket() {
	socket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/ws");
	
	socket.onopen = function() {
		console.log("websocket open");
		var board = document.getElementById("board-container");
		board.innerHTML = "";
		socket.send("go:");
	}
	
	socket.onmessage = function(event) {
		message(event.data);
	}

    socket.onclose = function() {
        console.log("web socket closed");
        setTimeout(createWebSocket, 1000);
    }

    socket.onError = function() {
        console.log("web socket error");
        setTimeout(createWebSocket, 1000);
    }
	
}

createWebSocket();