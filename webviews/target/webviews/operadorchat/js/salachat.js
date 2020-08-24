// to keep the session id
var sessionId = '';

// name of the client
var name = '';

var nameRoom = '';

// socket connection url and port lola.nuo.com.ec
var socket_url = 'lola.nuo.com.ec/webhook';

$(document).ready(function() {

	openSocket();
});

var webSocket;

var webSocket2;

var chatRooms = [];


/**
 * Connecting to socket
 */

/**
 * Will open the socket connection
 */
function join(nameRoom) {
	if (webSocket2 !== undefined && webSocket2.readyState !== WebSocket.CLOSED) {
		closeSocket();
	}
	
	setTimeout(function(){ openSocketRoom(nameRoom)
		; }, 500);
	
	$("#form_send_message").submit(function(e) {
		e.preventDefault();
		openSocketRoom(nameRoom);
	});
	
	var nombreOperadorChat = nameRoom; 
	console.log ('valor nombre'+nombreOperadorChat);
    document.getElementById("formvar:nombrevar").value = nombreOperadorChat;
    
	$('[id$=obtenerUltimos]').click();
	
	
    
	$('#messagesRoom li').remove();
	

}

function openSocketRoom(nameRoom) {
	// Ensures only one connection is open at a time
	if (webSocket2 !== undefined && webSocket2.readyState !== WebSocket.CLOSED) {
		return;
	}
	// https://68678b3f.ngrok.io/webhook/resources/
	// Create a new instance of the websocket
	webSocket2 = new WebSocket("wss://" + socket_url + "/chat?name=" + nameRoom);

	/**
	 * Binds functions to the listeners for the websocket.
	 */
	webSocket2.onopen = function(event) {
		console.log('El socket se abrio');
		$('#message_containerRoom').fadeIn();

		if (event.data === undefined)
			return;

	};

	webSocket2.onmessage = function(event) {
		// parsing the json data
		parseMessageRoom(event.data);
	};

	webSocket2.onclose = function(event) {
		console.log('El socket se a cerrado');
	};
}

function openSocket() {
	// Ensures only one connection is open at a time
	if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
		return;
	}
	// https://68678b3f.ngrok.io/webhook/resources/
	// Create a new instance of the websocket
	webSocket = new WebSocket("wss://" + socket_url + "/chat?name=lola");

	/**
	 * Binds functions to the listeners for the websocket.
	 */
	webSocket.onopen = function(event) {
		$('#message_container').fadeIn();

		if (event.data === undefined)
			return;

	};

	webSocket.onmessage = function(event) {

		// parsing the json data
		parseMessage(event.data);
	};

	webSocket.onclose = function(event) {
		console.log('Error! La conección esta cerrada. Intente conectando nuevamente.');
	};
}

/**
 * Sending the chat message to server
 */
function send() {
	var message = $('#input_message').val();

	if (message.trim().length > 0) {
		sendMessageToServer('message', message);
	} else {
		alert('Por favor ingrese el mensaje a enviar!');
	}

}

/**
 * Closing the socket connection
 */
function closeSocket() {
	webSocket2.close();

}

/**
 * Parsing the json message. The type of message is identified by 'flag' node
 * value flag can be self, new, message, exit
 */

function parseMessage(message) {
	var jObj = $.parseJSON(message);

	// if the flag is 'self' message contains the session id
	if (jObj.flag == 'self') {

		sessionId = jObj.sessionId;

	} else if (jObj.flag == 'new') {
		var new_name = jObj.name;
		console.log(new_name);
		var flag = false;
		var test = '\"'+new_name+'\"';
		console.log(new_name);
		if (chatRooms.includes(new_name))
		{
			flag = true;
		}
		
		// if the flag is 'new', a client joined the chat room
		

		// number of people online
		var online_count = jObj.onlineCount;

		var room_name = jObj.roomnumber;

		$('p.online_count').html(
				'Hola, hay <span class="green">' + name + '</span><b>'
						+ online_count
						+ '</b> personas en línea en este momento').fadeIn();

		if (jObj.sessionId != sessionId) {
			new_name = jObj.name;
		}

		var li = '<li class="new" id="'+new_name+'"><span class="name">' + new_name + '</span> '
				+ jObj.message + '<a href="#" onclick="join(\'' + new_name
				+ '\')"> Responder</a></li>';
		
		console.log(flag);
		console.log(chatRooms);
		if (!flag)
		{
			chatRooms.push(new_name);
		$('#messages').append(li);
		}

		

		$('#input_message').val('');

	} else if (jObj.flag == 'message') {
		// if the json flag is 'message', it means somebody sent the chat
		// message

		var from_name = ' ';
		
		var new_name = jObj.name;
		
		var flag = false;

		if (jObj.sessionId != sessionId) {
			from_name = jObj.name;
		}

		var myAudio = document.getElementById("audio");

		myAudio.play();

		var operador = 'operadorchat.jsf';
		var li = '<li class="new" id="'+new_name+'"><span class="name">' + new_name + '</span> '
		+ jObj.message + '<a href="#" onclick="join(\'' + new_name
		+ '\')"> Responder</a></li>';

		var lir = '<li><span class="name"><b>Tienes mensajes por responder del establecimiento:</b> '
				+ jObj.chatRoom + '</span></li>';
		
		// appending the chat message to list
		// appendChatMessage(li);

		$('p.notifications_count').html(
				'Tienes un nuevo mensaje de <b>' + jObj.chatRoom + '</b>')
				.fadeIn();
		// /appendChatRooms(lir);
		if (chatRooms.includes(new_name))
		{
			flag = true;
		}
		
		
		if (!flag)
		{
			chatRooms.push(new_name);
		$('#messages').append(li);
		}
		
		
		$('#input_message').val('');

	} else if (jObj.flag == 'exit') {
		// if the json flag is 'exit', it means somebody left the chat room
		var li = '<li class="exit"><span class="name red">' + jObj.name
				+ '</span> ' + jObj.message + '</li>';

		var online_count = jObj.onlineCount;

		$('p.online_count').html(
				'Hola, hay<span class="green">' + name + '</span> <b>'
						+ online_count
						+ '</b> personas en línea en este momento');
		
	
	}
}

function saveRoom(jObj) {
	alert("sala" + jObj.name);
	localStorage.setItem("sala", jObj.name);
}

/**
 * Appending the chat message to list
 */
function appendChatMessage(li) {
	$('#messages').append(li);

	// scrolling the list to bottom so that new message will be visible
	$('#messages').scrollTop($('#messages').height());
}

function appendChatRooms(li) {

	$('#rooms').append(li);

	// scrolling the list to bottom so that new message will be visible
	$('#rooms').scrollTop($('#rooms').height());
}

/**
 * Sending message to socket server message will be in json format
 */
function sendMessageToServer(flag, message) {
	var json = '{""}';

	// preparing json object
	var myObject = new Object();
	myObject.sessionId = sessionId;
	myObject.message = message;
	myObject.flag = flag;

	// converting json object to json string
	json = JSON.stringify(myObject);

	// sending message to server
	webSocket2.send(json);
}

// CODIGO DEL ROOM PARA CONECTARSE

function parseMessageRoom(message) {
	var jObj = $.parseJSON(message);

	// if the flag is 'self' message contains the session id
	if (jObj.flag == 'self') {

		sessionId = jObj.sessionId;

	} else if (jObj.flag == 'new') {
		
		
		// if the flag is 'new', a client joined the chat room
		var new_name = 'lola';

		// number of people online
		var online_count = jObj.onlineCount;

		var room_name = jObj.roomnumber;

		if (jObj.sessionId != sessionId) {
			new_name = jObj.name;
		}

		var li = '<li class="new"><span class="name">' + new_name + '</span> '
				+ jObj.message + '</li>';

		$('#messagesRoom').append(li);

		$('#input_message').val('');

	} else if (jObj.flag == 'message') {
		// if the json flag is 'message', it means somebody sent the chat
		// message

		var from_name = 'lola';

		if (jObj.sessionId != sessionId) {
			from_name = jObj.name;
		}

		var li = '<li><span class="name">' + from_name + '</span> '
				+ jObj.message + '</li>';

		// appending the chat message to list
		appendChatMessageRoom(li);

		$('#input_message').val('');

	} else if (jObj.flag == 'exit') {
		// if the json flag is 'exit', it means somebody left the chat room
		var li = '<li class="exit"><span class="name red">' + jObj.name
				+ '</span> ' + jObj.message + '</li>';

		var online_count = jObj.onlineCount;
		appendChatMessageRoom(li);
		$('#messages #'+jObj.name+'').remove();
		var index = chatRooms.indexOf(jObj.name);
		if (index > -1) {
			console.log("si encontro el objeto en el array")
			chatRooms.splice(index, 1);
			closeSocket();
		}
		
	}
}

/**
 * Appending the chat message to list
 */
function appendChatMessageRoom(li) {
	$('#messagesRoom').append(li);

	// scrolling the list to bottom so that new message will be visible
	$('#messagesRoom').scrollTop($('#messagesRoom').height());
}

/**
 * Sending message to socket server message will be in json format
 */
