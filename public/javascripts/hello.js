if (window.console) {
  console.log("Welcome to your Play application's JavaScript!");
}

var socket;

function cleanUpUI() {
	var boardMessages = document.getElementById("board-messages");
	var boardEditors = document.getElementById("board-editors");
	boardMessages.innerHTML = "";
	boardEditors.innerHTML = "";
}

function addNewMessage(event) {

    var divMessage = document.createElement("div");
    divMessage.className = "message";
    	
    var spanName = document.createElement("span");
    spanName.className = "msg-name";
    spanName.appendChild(document.createTextNode(event.name));
    divMessage.appendChild(spanName);

    var spanSeparator = document.createElement("span");
    spanSeparator.className = "msg-separator";
    spanSeparator.appendChild(document.createTextNode(" - "));
    divMessage.appendChild(spanSeparator)
    
    var divText = document.createElement("div");
    divText.className = "msg-text";
    divText.appendChild(document.createTextNode(event.text));
    divMessage.appendChild(divText)

	var board = document.getElementById("board-messages");
	board.appendChild(divMessage);
}

function setEditorList(event) {
	var board = document.getElementById("board-editors");
	board.innerHTML = event.editors;
}

function sendEvent(event) {
	socket.send(JSON.stringify(event));
}

function handleEvent(event) {
	console.log(event)
	
	if (event.event == "new-message") {
		addNewMessage(event)
	} else if (event.event == "editor-list") {
		setEditorList(event)
	}
}

function sendNewMessage() {
	var inputName = document.getElementById("input-name").value;
	var inputText = document.getElementById("input-text").value;
	
	sendEvent(newMessageEvent(inputName, inputText))
}

function textFocus() {
	var inputName = document.getElementById("input-name").value;
	
	sendEvent(editFocusEvent(inputName))
}

function textUnfocus() {
	var inputName = document.getElementById("input-name").value;
	
	sendEvent(editUnfocusEvent(inputName))
}

function connect() {
	
	socket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/ws");
	
	socket.onopen = function() {
		console.log("websocket open");
		cleanUpUI();
		
		// initiate communication
		sendEvent(initEvent());
	}
	
	socket.onmessage = function(event) {
		handleEvent(JSON.parse(event.data))
	}

	socket.onclose = function() {
		console.log("web socket closed");
		setTimeout(connect, 1000);
	}

	socket.onError = function() {
		console.log("web socket error");
		setTimeout(connect, 1000);
	}	
}


// ----- Json transformers ------

function initEvent() {
	return {
		"event" : "init",
		"id" : "id"
	}
}

function newMessageEvent(name, text) {
	return {
		"event" : "new-message",
		"name" : name,
		"text" : text
	}
}

function editFocusEvent(name) {
	return {
		"event" : "edit-focus",
		"name" : name
	}
}

function editUnfocusEvent(name) {
	return {
		"event" : "edit-unfocus",
		"name" : name
	}
}

connect()
