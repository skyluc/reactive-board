var socket;

function action(msgString) {

	var msg = JSON.parse(msgString);
	
	// ---
	
	if (msg.id = "message") {
		addMessage(msg.name, msg.data)
	}
	
}

function sendMessage() {
	var inputName = document.getElementById("input-name").value;
	var inputText = document.getElementById("input-text").value;

	// ---

    var action = {
			id : "message",
			name : inputName,
			data : inputText
		}
		
		socket.send(JSON.stringify(action))
	
}

function textFocus() {
	var inputName = document.getElementById("input-name").value;

	// ---

}

function textBlur() {
	var inputName = document.getElementById("input-name").value;

	// ---

}

function createWebSocket() {
	socket = new WebSocket("ws://" + location.hostname + ":" + location.port
			+ "/ws");

	socket.onopen = function() {
		console.log("websocket open");
		var bcontainer = document.getElementById("board-container");
		bcontainer.innerHTML = "";
		var beditors = document.getElementById("board-editors");
		beditors.innerHTML = ""
	
	    // ---
	    var action = {
			id : "go"
		}
		
		socket.send(JSON.stringify(action))

	}

	socket.onmessage = function(event) {
		// ---
        action(event.data)
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

// --- html modification methods ---

function addMessage(name, text) {

    var divMessage = document.createElement("div");
    divMessage.className = "message";
    	
    var spanName = document.createElement("span");
    spanName.className = "msg-name";
    spanName.appendChild(document.createTextNode(name));
    divMessage.appendChild(spanName);

    var spanSeparator = document.createElement("span");
    spanSeparator.className = "msg-separator";
    spanSeparator.appendChild(document.createTextNode(" - "));
    divMessage.appendChild(spanSeparator)
    
    var divText = document.createElement("div");
    divText.className = "msg-text";
    divText.appendChild(document.createTextNode(text));
    divMessage.appendChild(divText)

	var board = document.getElementById("board-container");
	board.appendChild(divMessage);
}

function setEditors(editorString) {
	
	var board = document.getElementById("board-editors");
	board.innerHTML = editorString;
}

