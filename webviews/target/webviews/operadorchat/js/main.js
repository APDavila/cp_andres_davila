// to keep the session id
window.onload = alert(localStorage.getItem("sala"));
var sessionId = '';
 
// name of the client
var name = '';

var sala = ' ';
 
// socket connection url and port lola.nuo.com.ec
var socket_url = 'lola.nuo.com.ec/webhook';
 
$(document).ready(function() {
	sala=localStorage.getItem("sala");
	openSocket();
	$("#form_send_message").submit(function(e) {
        e.preventDefault();
        openSocket();
    });
});
 
var webSocket;
 
/**
 * Connecting to socket
 */
 
/**
 * Will open the socket connection
 */
function openSocket() {
	
    // Ensures only one connection is open at a time
    if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
        return;
    }
    //https://68678b3f.ngrok.io/webhook/resources/
    // Create a new instance of the websocket
    webSocket = new WebSocket("wss://" + socket_url + "/chat?name="+sala);
 
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
        alert('Error! La conección esta cerrada. Intente conectando nuevamente.');
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
    webSocket.close();
 
    
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
        // if the flag is 'new', a client joined the chat room
        var new_name = 'Tú';
 
        // number of people online
        var online_count = jObj.onlineCount;
        
        var room_name = jObj.roomnumber;
 
        $('p.online_count').html(
                'Hola, hay <span class="green">' + name + '</span> <b>'
                        + online_count + '</b> personas en línea en este momento')
                .fadeIn();
 
        if (jObj.sessionId != sessionId) {
            new_name = jObj.name;
        }
 
        var li = '<li class="new"><span class="name">' + new_name + '</span> '
                + jObj.message + '</li>';
      
        
        $('#messages').append(li);
 
        $('#input_message').val('');
 
    } else if (jObj.flag == 'message') {
        // if the json flag is 'message', it means somebody sent the chat
        // message
 
        var from_name = ' ';
 
        if (jObj.sessionId != sessionId) {
            from_name = jObj.name;
        }
 
        var li = '<li><span class="name">' + from_name + '</span> '
                + jObj.message + '</li>';
        
        
        // appending the chat message to list
        appendChatMessage(li);
      
 
        $('#input_message').val('');
 
    } else if (jObj.flag == 'exit') {
        // if the json flag is 'exit', it means somebody left the chat room
        var li = '<li class="exit"><span class="name red">' + jObj.name
                + '</span> ' + jObj.message + '</li>';
 
        var online_count = jObj.onlineCount;
 
        $('p.online_count').html(
                'Hola, hay <span class="green">' + name + '</span><b>'
                        + online_count + '</b> personas en línea en este momento');
        appendChatMessage(li);
    }
}
 
/**
 * Appending the chat message to list
 */
function appendChatMessage(li) {
    $('#messages').append(li);
 
    // scrolling the list to bottom so that new message will be visible
    $('#messages').scrollTop($('#messages').height());
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
    webSocket.send(json);
}