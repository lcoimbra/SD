package TCPserver;

import java.rmi.Remote;
import java.util.ArrayList;


public interface tcpInterface extends Remote {

    public void sendMsgToOnlineUsers(String s, ArrayList<Integer> usr, int idMsg, int originalUser) throws java.rmi.RemoteException;

}

