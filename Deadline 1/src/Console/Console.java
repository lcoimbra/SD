package Console;

import RMIserver.rmiInterface;
import TCPserver.TradutorComandos;
import TCPserver.carregarPropriedades;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class Console {

    private static final int ADMIN_ID = -2;

    public static rmiInterface ri;
    public static final carregarPropriedades ps = new carregarPropriedades("iBei.properties");

    public static void main(String[] args) {
        ri=ligarAoRMI();

        new Thread() {
            public void run() {

                Scanner keyboardScanner = new Scanner(System.in);
                while (true) {
                    String msg = "";
                    System.out.println("Está ao nivel da comunicação RMI");
                    String readKeyboard = keyboardScanner.nextLine();
                    TradutorComandos tc = new TradutorComandos(readKeyboard);
                    int pedido=0;
                    while(pedido<15) {
                        try {
                            switch (tc.getType()) {
                                case "cancel_auction":
                                    if (tc.getM().containsKey("id")){

                                        msg=ri.cancelAuction(ADMIN_ID,Integer.parseInt(tc.getM().get("id").toString()));

                                    }else{
                                        msg="type: cancel_auction, ok: false";
                                    }
                                    System.out.println(msg);
                                    pedido=100;
                                    break;
                                case "ban_user":
                                    if (tc.getM().containsKey("username")) {
                                        msg = ri.banUser(ADMIN_ID, tc.getM().get("username").toString());
                                    }else{
                                        msg="type ban_user, ok: false";
                                    }
                                    System.out.println(msg);
                                    pedido=100;
                                    break;
                                case "stats":
                                    msg = ri.stats(ADMIN_ID);
                                    System.out.println(msg);
                                    pedido=100;
                                    break;
                                case "test_server":
                                    if (tc.getM().containsKey("ip") && tc.getM().containsKey("port")) {
                                        TCPClient c =new TCPClient(tc.getM().get("ip").toString(), Integer.parseInt(tc.getM().get("port").toString()),ri);
                                        /*if(c.msg.contains("erro1")){
                                            System.out.println("type: tcp server down");
                                            pedido=15;
                                        }*/
                                    }
                                    pedido=100;
                                    break;
                                default:
                                    System.out.println("type: unknown command");
                                    pedido=100;
                                    break;
                            }
                        }catch (ConnectException e){
                            if(pedido==14){
                                return;
                            }
                            pedido++;
                            try {
                                this.sleep(2000);
                            } catch (InterruptedException ex) {}
                            try {
                                ri = (rmiInterface) LocateRegistry.getRegistry(ps.getProperty("ipServerRMI"), Integer.parseInt(ps.getProperty("RMIPort"))).lookup(ps.getProperty("RMIName"));
                            }catch(Exception ee){}
                        }catch(Exception e){e.printStackTrace();}
                    }
                }
            }
        }.start();
    }

    public static synchronized rmiInterface ligarAoRMI(){
        int cont = 0;
        boolean on= false;
        while(!on){
            try {
                String rmiServer = ps.getProperty("ipServer1RMI");
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
}