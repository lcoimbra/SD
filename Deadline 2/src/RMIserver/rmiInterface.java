package RMIserver;

import TCPserver.tcpInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

public interface rmiInterface extends Remote {

    public int login(Map m)throws RemoteException;
    public int register(Map m) throws RemoteException;
    public String createAuction(int usr, Map m) throws RemoteException;
    public String searchAuction(Map m)throws RemoteException;
    public String detailAuction(Map m)throws RemoteException;
    public String myAuctions(int usr)throws RemoteException;
    public String bid(int usr, Map m, String name) throws RemoteException;
    public String editAuction(int usr, Map m) throws RemoteException;
    public String message(int usr, Map m,String name) throws RemoteException;
    public void subscribe(tcpInterface c) throws RemoteException;
    public Map notificationMessage_offline(int usr) throws RemoteException;
    public void removeAuction(int id) throws RemoteException;
    public void removeFromMsgNotifcacao(int utilizador, int msgID) throws RemoteException;
    public String stats (int utilizador) throws RemoteException;
    public String banUser (int utilizador, String ban) throws RemoteException;
    public String cancelAuction (int utilizador, int auctionID) throws RemoteException;
    public void unsubscribe(tcpInterface c) throws RemoteException;
    public int fbVerifyUser(long fb_id, String fb_token) throws RemoteException;
    public boolean fbAssociate(int userID, long fb_id, String fb_token) throws RemoteException;
    public boolean fbIsAssociated(int userID) throws RemoteException;
    //public ArrayList<String> fbGetToken(int userID) throws RemoteException;
    public boolean fbUnlinkAccount(String user) throws RemoteException;
    public String getUsername(int userID) throws RemoteException;
}