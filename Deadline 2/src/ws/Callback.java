package ws;

import RMIserver.rmiInterface;
import TCPserver.TradutorComandos;
import TCPserver.tcpInterface;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static TCPserver.Server.list;

/**
 * Created by kifel on 16/12/2016.
 */
public class Callback extends UnicastRemoteObject implements tcpInterface{
    private final String ip="192.168.1.4";
    private final int porto=7000;
    private final String nameService = "iBei";
    private rmiInterface rmi;
    private WebSocketAnnotation wsa;

    Callback(WebSocketAnnotation wsa) throws RemoteException {
        //super();
        this.wsa=wsa;
        try {
            rmi = (rmiInterface) LocateRegistry.getRegistry(ip, porto).lookup(nameService);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    synchronized public void subscribe(){
        try {
            rmi.subscribe(this);
        } catch (RemoteException e) {
           // e.printStackTrace();
        }
    }

    @Override
    synchronized public void sendMsgToOnlineUsers(String s, ArrayList<Integer> usr, int idMsg, int originalUser) {
        try {
            if (idMsg==-1 && usr.contains(Integer.parseInt(wsa.getUserId().trim())) && Integer.parseInt(wsa.getUserId().trim()) != originalUser) {

                TradutorComandos tc = new TradutorComandos(s);
                String text = "Alert: user "+tc.getM().get("user")+" exceeded your bid in auction "+ tc.getM().get("id")+".\nThe new amount is "+ tc.getM().get("amount");
                try {
                    wsa.getSession().getBasicRemote().sendText(text);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if(idMsg!=-1 && usr.contains(Integer.parseInt(wsa.getUserId().trim()))) {
                TradutorComandos tc = new TradutorComandos(s);

                String text = "Alert: user "+tc.getM().get("user")+" sends a message to auction "+ tc.getM().get("id")+".\nMessage: "+ tc.getM().get("text");
                try {
                    wsa.getSession().getBasicRemote().sendText(text);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    rmi.removeFromMsgNotifcacao(Integer.parseInt(wsa.getUserId().trim()), idMsg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    synchronized public void unsubscribe() throws RemoteException {
        try {
            rmi.unsubscribe(this);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    synchronized public void offlineMessage() throws RemoteException {
       // try {
            //System.out.println(Integer.parseInt(wsa.getUserId().trim()));
            Map m = rmi.notificationMessage_offline(Integer.parseInt(wsa.getUserId().trim()));
            if (m != null) {
                Set keys = m.keySet();
                for (Iterator i = keys.iterator(); i.hasNext(); ) {
                    int key = (Integer) i.next();
                    String msg = m.get(key).toString();
                    TradutorComandos tc = new TradutorComandos(msg);
                    String text = "Alert: user "+tc.getM().get("user")+" sends a message to auction "+ tc.getM().get("id")+" during your absence\nMessage: "+ tc.getM().get("text");
                    //System.out.println(text);
                    try {
                        wsa.getSession().getBasicRemote().sendText(text);
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                    rmi.removeFromMsgNotifcacao(Integer.parseInt(wsa.getUserId().trim()), key);
                }
            }
    }
}
