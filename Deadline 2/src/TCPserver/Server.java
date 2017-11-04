package TCPserver;

import RMIserver.*;
import java.net.*;
import java.rmi.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 * Created by joao on 10/10/2016.
 */
public class Server extends UnicastRemoteObject implements tcpInterface{
    public static rmiInterface ri;

    public static CopyOnWriteArrayList <ligacaoCliente> list = new CopyOnWriteArrayList<ligacaoCliente>();
    public static final carregarPropriedades ps = new carregarPropriedades("iBei.properties");
    public static String msg="";
    public static int porto=-1;
    public static Server c;

    protected Server() throws RemoteException {}

    public static void main(String args[]) throws RemoteException {
        ServerSocket listenSocket;
        boolean ligado=true;

        while(ligado){
                /*começamos por definir a porta em que vai trabalhar*/
                int portoAux=Integer.parseInt(ps.getProperty("starterPort"));
                while(porto==-1) {
                    if (isAvailable(portoAux)) {
                        porto = portoAux;
                    } else {
                        portoAux++;
                    }
                }
                /*estabelecemos ligação ao RMI*/
                ri=ligarAoRMI();
                c = new Server();
                ri.subscribe(c);
                if(ri!=null) {
                    while (true) {
                        try {
                            /*** parte que cuida da comunicação Multicast ***/
                            new MulticastReceiver();
                            new MulticastSender();
                            Thread read = new Thread(){
                                public void run(){
                                    while(true){
                                        try {
                                            this.sleep(60000); //enviamos msg de 1 em 1 minuto
                                        } catch (InterruptedException ex) {
                                        }
                                        try{
                                            for(int i=0;i<list.size();i++){
                                                list.get(i).sendMsg(Server.msg);
                                            }
                                        }catch(Exception e){}
                                    }
                                }
                            };
                            read.start();
                            /**cria socket de escuta*/
                            listenSocket = new ServerSocket(porto);
                            System.out.println("LISTEN SOCKET=" + listenSocket);
                            while (true) {
                                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                                System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
                                list.add(new ligacaoCliente(clientSocket,list, ri));
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                        }
                }
        }
    }

    public static synchronized rmiInterface ligarAoRMI(){
        int cont = 0;
        boolean on= false;
        while(!on){
            try {
                String rmiServer = ps.getProperty("ipServerRMI");
                int rmiPort = Integer.parseInt(ps.getProperty("RMIPort"));
                String rmiName = ps.getProperty("RMIName");
                ri = (rmiInterface) LocateRegistry.getRegistry(rmiServer, rmiPort).lookup(rmiName);
                on = true;
                System.out.println("ligou ao RMI");
                return ri;
            } catch (RemoteException ex) {
                cont++;
                if(cont>3) {
                    System.out.println("RMI não está conectado");
                    System.exit(0);
                }
                //Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotBoundException ex) {

                //Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static boolean isAvailable(int porto) {
        System.out.println("Ver disponibilidade do porto: " + porto);
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(porto);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(porto);
            ds.setReuseAddress(true);
            return true;
        } catch (Exception e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (Exception e) {
                }
            }
        }
        return false;
    }

    public void sendMsgToOnlineUsers(String s, ArrayList<Integer> usr, int idMsg, int originalUser) throws java.rmi.RemoteException{
        //System.out.println("OOOOOOOOOOLLLLLLLLLLLAAAAAAAA "+usr.size());
        if (idMsg == -1) {
            for(int i = 0; i < list.size(); i++){
                if(usr.contains(list.get(i).userId) && list.get(i).userId != originalUser){
                    list.get(i).sendMsg(s);
                }
            }
            return;
        }

        int pedido=0,k=0;
        while(pedido<15) {
            try{
                for(int i=k;i<list.size();i++){
                    if(usr.contains(list.get(i).userId)){
                        ri.removeFromMsgNotifcacao(list.get(i).userId, idMsg);
                        list.get(i).sendMsg(s);
                    }
                }
                break;
            }catch (Exception e){
                //e.printStackTrace();
                pedido++;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {}
                try {
                    ri = (rmiInterface) LocateRegistry.getRegistry(ps.getProperty("ipServerRMI"), Integer.parseInt(ps.getProperty("RMIPort"))).lookup(ps.getProperty("RMIName"));
                }catch(Exception ee){}
            }
        }
    }

    public static CopyOnWriteArrayList <ligacaoCliente> getList(){
        return list;
    }
}