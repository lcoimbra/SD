/**
 * Created by kifel on 12/12/2016.
 */

//<script src="wsScript.js" type="text/javascript">
    var websocket = null;

    window.onload = function() { // execute once the page loads
        initialize();
     }

    function initialize() { // URI = ws://10.16.0.165:8080/WebSocket/ws
        connect('ws://' + window.location.host + '/ws');
    }

    function connect(host) { //connect to the host websocket
        if ('WebSocket' in window) {
            websocket = new WebSocket(host);
        }else if ('MozWebSocket' in window) {
            websocket = new MozWebSocket(host);
        }else {
            writeToHistory('Get a real browser which supports WebSocket.');
            return;
        }
        websocket.onopen    = onOpen; // set the event listeners below
        websocket.onclose   = onClose;
        websocket.onmessage = onMessage;
        websocket.onerror   = onError;
    }

    function onError(event) {
        //writeToHistory('WebSocket error (' + event.data + ').');
        //document.getElementById('chat').onkeydown = null;
    }

    function onOpen(event) {
        doSend();
        websocket.send("offline");
    }

    function onClose(event) {
        //writeToHistory('WebSocket closed.');
        //document.getElementById('chat').onkeydown = null;
    }

    function onMessage(message) { // print the received message
        writeToHistory(message.data);
    }

    function doSend() {
        var message = document.getElementById('hidUser').innerHTML;
        var hidUId = document.getElementById('hidUserId').innerHTML;
        if (message != '') {
            websocket.send(message+"-"+hidUId); // send the message
        }else {
            return"";
        }
        if (document.getElementById("myListAuction")) {
            var id = document.getElementById('hidId').innerHTML;
            if (id != '') {
                websocket.send("auction-" + id); // send the message
            }
        }
    }

    function writeToHistory(text) {
        if(text.toString().search("Alert")==0){
            alert(text);
        }
        else if(document.getElementById("myList")!=null){
            var newItem = document.createElement('p');
           // var textnode = document.createTextNode("");
            newItem.innerHTML=(text);

            var list = document.getElementById("myList");
            list.replaceChild(newItem, list.childNodes[0]);
        }else if(document.getElementById("myListAuction")!=null){
            var newItem = document.createElement('p');
            // var textnode = document.createTextNode("");
            newItem.innerHTML=(text);

            var list = document.getElementById("myListAuction");
            list.replaceChild(newItem, list.childNodes[0]);
        }
       // var history = document.getElementById('myList');

       // var line = document.createElement('p');

        //line.style.wordWrap = 'break-word';
       // line.innerHTML = text;

        //history.clear();
        //history.replaceChild(line,);
        //history.appendChild(line);

       // history.scrollTop = history.scrollHeight;

    }
//</script>