package ws;


import RMIserver.rmiInterface;
import TCPserver.tcpInterface;

import java.io.IOException;
import java.io.NotSerializableException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnError;
import javax.websocket.Session;

@ServerEndpoint(value = "/ws")
public class WebSocketAnnotation {
    private static final Set<WebSocketAnnotation> users = new CopyOnWriteArraySet<>();
    private String username;
    private Session session;
    private String userId;
    private String id;  //id do leilão
    private Callback call ;

    public WebSocketAnnotation() {
        username="";
        id="";
        userId="";
    }

    @OnOpen
    public void start(Session session) {
        this.session = session;
        this.users.add(this);
        try {
            call = new Callback(this);
        } catch (RemoteException e) {
            //e.printStackTrace();
        }
        call.subscribe();
        //String message = "*" + username + "* connected.";

    }

    @OnClose
    public void end() {
        // clean up once the WebSocket connection is closed
        try {
            call.unsubscribe();
        } catch (RemoteException e) {
            //e.printStackTrace();
        }
        WebSocketAnnotation.users.remove(this);
    }

    @OnMessage
    public void receiveMessage(String message) {
        String Message = "";
        if (message.contains("offline")) {
            try {
                call.offlineMessage();
            } catch (RemoteException e) {
                //e.printStackTrace();
            }
        }else if(message.contains("auction-")) {
            String[] s = message.split("-");
            this.id = s[1];
            Iterator<WebSocketAnnotation> i = WebSocketAnnotation.users.iterator();
            WebSocketAnnotation aux = null;
            while (i.hasNext()) {
                aux = (WebSocketAnnotation) i.next();
                if (aux.id.equals(this.id)) {
                    Message = Message + aux.username;
                }
            }
            sendAuction(Message);
        }else{
            String[] s = message.split("-");
            username = s[0];
            userId = s[1];
            Iterator<WebSocketAnnotation> i = WebSocketAnnotation.users.iterator();
            WebSocketAnnotation aux = null;
            //this.session.getBasicRemote().sendText(text);
            while (i.hasNext()) {
                aux = (WebSocketAnnotation) i.next();
                if (aux.session.getId() != session.getId() /*!aux.username.equals(this.username) */) {
                    Message = Message + aux.username;
                }
            }
            sendMessage(Message);
        }
    }

    private void sendAuction(String text) {
        try {
            Iterator<WebSocketAnnotation> i = WebSocketAnnotation.users.iterator();
            WebSocketAnnotation aux = null;
            while (i.hasNext()) {
                aux = (WebSocketAnnotation) i.next();
                if(aux.id.equals(this.id))
                    aux.session.getBasicRemote().sendText(text);
            }
        } catch (IOException e) {
            // clean up once the WebSocket connection is closed
            // e.printStackTrace();
            try {
                this.session.close();
            } catch (IOException e1) {
                //e1.printStackTrace();
            }
        }
    }

    @OnError
    public void handleError(Throwable t) {
      //  t.printStackTrace();
    }

    private void sendMessage(String text) {
        // uses *this* object's session to call sendText()
        try {
            Iterator<WebSocketAnnotation> i = WebSocketAnnotation.users.iterator();
            WebSocketAnnotation aux = null;
            while (i.hasNext()) {
                aux = (WebSocketAnnotation) i.next();
                if (aux.id.isEmpty()) {
                    if (aux.session.getId() == session.getId() && !text.contains(this.username)) {
                        aux.session.getBasicRemote().sendText(text+this.username);
                    } else if (aux.session.getId() == session.getId() && text.contains(this.username)) {
                        aux.session.getBasicRemote().sendText(text);
                    } else if (!text.contains(this.username)) {
                        aux.session.getBasicRemote().sendText(text + this.username);
                    }
                }
            }
        } catch (IOException e) {
            // clean up once the WebSocket connection is closed
            // e.printStackTrace();
            try {
                this.session.close();
            } catch (IOException e1) {
                //e1.printStackTrace();
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}