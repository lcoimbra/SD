package RMIserver;

import TCPserver.carregarPropriedades;
import TCPserver.tcpInterface;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by joao on 11/10/2016.
 */
public class RMIServer extends UnicastRemoteObject implements rmiInterface {
    private static ligacaoBD baseDados = new ligacaoBD();

    protected volatile CopyOnWriteArrayList<tcpInterface> client=new CopyOnWriteArrayList<tcpInterface>();

    private static final long serialVersionUID = 1L;
    private UdpMasterRMIServer udpMaster;
    private UdpSlaveRMIServer udpSlave;
    //############RMI#########
    private String IpServer;
    private String RMIPort;
    private String RMIName;
    public volatile boolean isMaster;
    //############UDP##########
    public String UdpRMIPort;

    public carregarPropriedades ps;

    protected RMIServer() throws RemoteException {
        this.ps = new carregarPropriedades("iBei.properties");
        this.setIpServer(this.ps.getProperty("ipServerRMI"));
        this.RMIPort = this.ps.getProperty("RMIPort");
        this.setRMIName(this.ps.getProperty("RMIName"));
        this.UdpRMIPort = this.ps.getProperty("UdpRMIPort");

    }

    public static void main(String args[]) {

        RMIServer rmserver = null;
        try {
            rmserver = new RMIServer();
        } catch (RemoteException e1) {}
        while(true)
        {
            //efetuar o bind
            rmserver.establishRMIConnection( rmserver );

            //criar as ligacoes UDP entre os servers RMI
            rmserver.establishUDPconnection(rmserver);

            //criar um objeto com os métodos a invocar

			/*while(rmserver.isMaster == false){//se o server RMI for secundario*/
            if(rmserver.isMaster == false)
            {
                try {
                    //System.out.println("ESPERAR QUE A SLAVE UDP THREAD MORRA");
                    rmserver.udpSlave.join();
                   // System.out.println("JA ESPEROU, VAI CONTINUAR AGORA: "+rmserver.isMaster);
                } catch (InterruptedException e) {
                    //System.out.println("ERRO A ESPERAR PELA THREAD SLAVE UDP");
                    //e.printStackTrace();
                }
            }else{
                try {
                    //System.out.println("ESPERAR QUE A MASTER UDP THREAD MORRA");
                    rmserver.udpMaster.join();
                    //System.out.println("JA ESPEROU, VAI CONTINUAR AGORA: "+rmserver.isMaster);
                } catch (InterruptedException e) {
                    //System.out.println("ERRO A ESPERAR PELA THREAD MASTER UDP");
                    //e.printStackTrace();
                }
            }
        }
    }

    public void establishUDPconnection(RMIServer rmserver) {
        boolean brokenUdp = true;
        while(brokenUdp)
        {
            if(rmserver.isMaster)
            {
                //criar a thread udpMaster - server UDP
                this.udpMaster = new UdpMasterRMIServer(this.IpServer, this.UdpRMIPort, rmserver);

            }else
            {
                this.udpSlave = new UdpSlaveRMIServer(this.IpServer, this.UdpRMIPort, rmserver);
            }
            brokenUdp = false;
        }
    }

    public void establishRMIConnection(RMIServer server) {
        Registry r = null;

        boolean brokenR = true;
        while(brokenR)
        {
    		/*primeiro verificar se o RMI está a correr n porto do server de backup*/
            try {
                System.out.println(this.getIpServer()+" VS "+this.RMIPort+" VS "+this.RMIName);
                LocateRegistry.getRegistry(this.getIpServer(), Integer.parseInt(this.RMIPort)).lookup(this.RMIName);

            } catch (RemoteException | NotBoundException e) {
                //System.out.println("RemoteException | NotBoundException "+this.getIpServer()+" AO VERIFICAR O RMI NO PORTO DO SERVER DE BACKUP"+this.RMIPort);

				/*segundo verificar se o RMI está a correr n porto do server MASTER*/
                try {
                    LocateRegistry.getRegistry(this.IpServer, Integer.parseInt(this.RMIPort)).lookup(this.RMIName);

                } catch ( RemoteException |NotBoundException e1) {

					/*O RMI N FOI ENCONTRADO NO IP DE BACKUP NEM NO IP LOCAL*/
                    //System.out.println("RemoteException |NotBoundException "+this.IpServer+" AO VERIFICAR O RMI NO PORTO DO SERVER MASTER "+this.RMIPort);

                    try {
                        r = LocateRegistry.createRegistry(Integer.parseInt(this.RMIPort));
                        r.rebind(this.RMIName, server);
                        System.out.println("RMI_Server ready");
                        server.isMaster = true;
                        brokenR = false;
                    } catch (RemoteException re) {

                        //System.out.println("Exception in RMI server (Rebind or Port)" );

                        System.out.println("Press enter to finish...");
                        try {
                            System.in.read();
                        } catch (IOException re1) {
                            //re1.printStackTrace();
                        }
                        brokenR = false;
                    }
                }
               // if(r == null)//System.out.println("R IS NULL - SECOUND");
            }
           // if(r == null)//System.out.println("R IS NULL - FIRST");
            brokenR = false;
        }
    }

    public String getIpServer() {
        return IpServer;
    }

    public void setIpServer(String ipServer) {
        IpServer = ipServer;
    }

    public void setRMIName(String rMIName) {
        RMIName = rMIName;
    }

    public int login(Map m) throws RemoteException {
        if (m.containsKey("username") && m.containsKey("password")) {
            try {
                return baseDados.login(m.get("username").toString(), m.get("password").toString());
            } catch (Exception e) {
                //e.printStackTrace();
                System.out.println("ERRO");
            }
        }
        return -1;
    }

    public int register(Map m) throws RemoteException {
        //System.out.println("RMI REGISTO : " +m.get("username").toString()+" "+m.get("password").toString());
        if (m.containsKey("username") && m.containsKey("password")) {
            try {
                return baseDados.register(m.get("username").toString(), m.get("password").toString());
            } catch (Exception e) {
                //System.out.println("ERRO");
            }
        }
        return -1;
    }

    public String createAuction(int usr, Map m)throws RemoteException{
        if(m.containsKey("code")&& m.containsKey("title") && m.containsKey("description") && m.containsKey("deadline") && m.containsKey("amount")){
            try{
                String[] dl=m.get("deadline").toString().split(" ");
                String[] dl2=dl[1].split("-");
                String newDeadline= dl[0]+" "+dl2[0]+":"+dl2[1]+":00";
                return baseDados.createAuction(usr,Long.parseLong(m.get("code").toString()),m.get("title").toString(),m.get("description").toString(),newDeadline,Double.parseDouble(m.get("amount").toString()));
        }catch(Exception e){
                //e.printStackTrace();
            return "type: create_auction, ok: false";
            }
        }
        return "type: create_auction, ok: false";
    }

    public String searchAuction(Map m)throws RemoteException{
        if (m.containsKey("code")){
            try{
                //System.out.println(m.get("code").toString() +"pp");
                String str = m.get("code").toString();
               // str.replaceAll("(\\r|\\n)", "");
               // System.out.println(str+"pp");
                return baseDados.searchAuction(Long.parseLong(str));
            }catch(Exception e){
               // e.printStackTrace();
                return "type: search_auction, ok: false";
            }
        }
        return "type: search_auction, ok: false";
    }

    public String detailAuction(Map m)throws RemoteException{
        if (m.containsKey("id")){
            try {
                return baseDados.detailAuction(Integer.parseInt(m.get("id").toString()));
            }catch(Exception e){
                return "type: detail_auction, ok: false";
            }
        }
        return "type: detail_auction, ok: false";
    }

    public String myAuctions(int usr)throws RemoteException{
        try{
            return baseDados.myAuctions(usr);
        }catch (Exception e){
            return "type: my_auction, ok: false";
        }
    }

    public String bid(int usr, Map m, String name) throws RemoteException{
        if (m.containsKey("id") && m.containsKey("amount")){
            try{
                ArrayList<Integer> notificar = baseDados.bid(usr, Integer.parseInt(m.get("id").toString()),Double.parseDouble(m.get("amount").toString()));

                if (notificar == null)
                    return "type: bid, ok: false";
                else {
                    for (int j = 0; j < client.size(); j++) {
                        String MSG = "type: notificaton_bid, id: " + Integer.parseInt(m.get("id").toString()) + ", user: " + name + ", amount: " + m.get("amount").toString();
                        //System.out.println("RMI: "+MSG);
                        client.get(j).sendMsgToOnlineUsers(MSG, notificar, -1, usr);
                    }
                    return "type: bid, ok: true";
                }
            }catch (Exception e){
                return "type: bid, ok: false";
            }
        }
        return "type: bid, ok: false";
    }

    public String editAuction(int usr, Map m) throws RemoteException{
        long code;
        String title, description,deadline;
        double amount;
        if (m.containsKey("id")) {
            if (m.containsKey("code")) {
                code = Long.parseLong(m.get("code").toString());
            } else {
                code =-1;
            }
            if (m.containsKey("title")) {
                title = m.get("title").toString();
            } else {
                title = null;
            }
            if (m.containsKey("description")) {
                description = m.get("description").toString();
            } else {
                description = null;
            }
            if (m.containsKey("deadline")) {
                deadline = m.get("deadline").toString();
            } else {
                deadline = null;
            }
            if (m.containsKey("amount")) {
                amount = Double.parseDouble(m.get("amount").toString());
            } else {
                amount = -1;
            }
            try {
                return baseDados.editAuction(usr,Integer.parseInt(m.get("id").toString()),code,title,description,deadline,amount);
            } catch (Exception e) {
                //e.printStackTrace();
                return "type: edit_Auction, ok: false2";
            }
        }
        return "type: edit_Auction, ok: false2";
    }

    public String message(int usr, Map m, String name) throws RemoteException{
        if(m.containsKey("id") && m.containsKey("text")){
            try{
                Map mm = baseDados.message(usr,Integer.parseInt(m.get("id").toString()),m.get("text").toString());
                if (mm != null) {
                    Set keys = mm.keySet();
                    for (Iterator i = keys.iterator(); i.hasNext(); ) {
                        int key = (Integer) i.next();
                        ArrayList<Integer> utilizadores = baseDados.createNotifications(key);
                        if(utilizadores!=null) {
                            for (int j = 0; j < client.size(); j++) {
                                String MSG = "type: notificaton_message, id: " + Integer.parseInt(m.get("id").toString()) + ", user: " + name + ", text: " + mm.get(key).toString();
                                client.get(j).sendMsgToOnlineUsers(MSG, utilizadores, key, -1);
                            }
                        }
                    }
                    return "type: message, ok: true";
                }

            }catch(Exception e){
                //e.printStackTrace();
                return "type: message, ok: false";
            }
        }
        return "type: message, ok: false";
    }

    public Map notificationMessage_offline(int usr) {
        try {
            return baseDados.notificationMessage_offline(usr);
        } catch (Exception e) {
            return null;
        }
    }

    public void subscribe(tcpInterface c) throws RemoteException {
        client.add(c);
    }

    public void unsubscribe(tcpInterface c) throws RemoteException {
        if(client.contains(c))
            client.remove(c);
    }

    public void removeAuction(int id) throws RemoteException{
        try{
            baseDados.removeAuction(id);
        }catch (Exception e){}
    }

    public void removeFromMsgNotifcacao(int utilizador, int msgID) throws RemoteException{
        try{
            baseDados.removeFromMsgNotifcacao(utilizador,msgID);
        }catch (Exception e){}
    }

    public String stats (int utilizador) throws RemoteException{
        try{
            return baseDados.stats(utilizador);
        }catch (Exception e){
            return "type: stats, ok: false";
        }
    }

    public String banUser (int utilizador, String ban) throws RemoteException{
        try{
            ArrayList<Integer> m = baseDados.banUser(utilizador,ban);

            if (m != null) {
                if (m.get(0) != -1) {
                    for (int i = 0; i < m.size(); i++) {
                         Map<String, String> mm = new HashMap<>();
                        mm.put("id", m.get(i).toString());
                        mm.put("text", "[BAN HAMMER] - Bids modified, we are sorry for any trouble this may cause :)");
                        message(utilizador, mm, "admin");
                    }
                    return "type: ban_User, ok: true";
                } else{
                    return "type: ban_User, ok: true";
                }

            } else
                return "type: ban_User, ok: false";
        }catch (Exception e){
            return "type: ban_User, ok: false";
        }
    }

    public String cancelAuction (int utilizador, int auctionID) throws RemoteException{
        try{
            return baseDados.cancelAuction(utilizador,auctionID);
        }catch(Exception e){
            return "type: cancelAuction, ok: false";
        }
    }

    public int fbVerifyUser(long fb_id, String fb_token) throws RemoteException {
        try {
            return baseDados.fbVerifyUser(fb_id, fb_token);
        } catch (Exception e) {
            //e.printStackTrace();
            return -1;
        }
    }

    public boolean fbAssociate(int userID, long fb_id, String fb_token) throws RemoteException {
        try {
            return baseDados.fbAssociate(userID, fb_id, fb_token);
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean fbIsAssociated(int userID) throws RemoteException {
        try {
            return baseDados.fbIsAssociated(userID);
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }
/*
    @Override
    public ArrayList<String> fbGetToken(int userID) throws RemoteException {
        try {
            return baseDados.fbGetToken(userID);
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
*/
    @Override
    public boolean fbUnlinkAccount(String user) throws RemoteException {
        try {
            return baseDados.fbUnlinkAccount(user);
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getUsername(int userID) throws RemoteException {
        try {
            return baseDados.getUsername(userID);
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }

}