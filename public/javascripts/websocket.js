var socket;

function action(msgString) {

	var msg = JSON.parse(msgText);
	
	if (msg.id == "msg") {
		actionMsg(msg);
	}
}

function actionMsg(msg)

    var divMessage = document.createElement("div");
    divMessage.className = "message";
    	
    var spanName = document.createElement("span");
    spanName.className = "msg-name";
    spanName.appendChild(document.createTextNode(msg.name));
    divMessage.appendChild(spanName)

    var spanSeparator = document.createElement("span");
    spanSeparator.className = "msg-separator";
    spanSeparator.appendChild(document.createTextNode(" - "));
    divMessage.appendSeparator(spanName)
    
    var divText = document.createElement("div");
    divText.className = "msg-text";
    divText.appendChild(document.createTextNode(msg.text));
    divMessage.appendChild(divText)

	var board = document.getElementById("board-container");
	board.appendChild(divMessage);
}

function sendMessage() {
	var inputName = document.getElementById("input-name").value;
	var inputText = document.getElementById("input-text").value;
	
	var action = {
	  "id" : "msg",
	  "name" : inputName,
	  "text" : inputText
	}

	socket.send(JSON.stringify(action))
}

function createWebSocket() {
	socket = new WebSocket("ws://" + location.hostname + ":" + location.port
			+ "/ws");

	socket.onopen = function() {
		console.log("websocket open");
		var board = document.getElementById("board-container");
		board.innerHTML = "";
		var action = {
			"id" : "go"
		};
		socket.send(JSON.stringify(action));
	}

	socket.onmessage = function(event) {
		action(event.data);
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