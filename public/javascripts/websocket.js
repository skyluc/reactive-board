var socket;

function message(msgString) {
	
	var temp = document.createElement("div");
	temp.innerHTML = msgString;
	var board = document.getElementById("board-container")
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
		socket.send("go:");
	}
	
	socket.onmessage = function(event) {
		message(event.data);
	}
	
}

createWebSocket();