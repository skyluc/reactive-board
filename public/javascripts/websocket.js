function message(msgString) {
	
	var temp = document.createElement("div");
	temp.innerHTML = msgString;
	var board = document.getElementById("board-container")
	board.appendChild(temp.firstChild);
}

function createWebSocket() {
	var socket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/ws");
	
	socket.onopen = function() {
		console.log("websocket open");
		socket.send("go:");
	}
	
	socket.onmessage = function(event) {
		message(event.data);
	}
	
}

createWebSocket();