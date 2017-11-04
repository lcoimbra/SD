package TCPserver;

import RMIserver.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by joao on 14/10/2016.
 */
public class ligacaoCliente extends Thread {
    private DataInputStream in;
    public DataOutputStream out;
    public Socket clientSocket;
    private rmiInterface rmi;
    public int userId = -1;
    public String userName=null;
    public int pedido;

    public static final carregarPropriedades ps = new carregarPropriedades("iBei.properties");
    CopyOnWriteArrayList<ligacaoCliente> lista;

    ligacaoCliente(Socket s, CopyOnWriteArrayList<ligacaoCliente> lista, rmiInterface rmi) {
        try {
            this.rmi = rmi;
            this.clientSocket = s;
            this.in = new DataInputStream(clientSocket.getInputStream());
            this.out = new DataOutputStream(clientSocket.getOutputStream());
            this.lista = lista;
            this.start();
        } catch (IOException e) {
            System.out.println("Erro a criar a class ligacao");;
        }
    }

    public void sendMsg(String msg){
        System.out.println("SEND: "+msg);
        try {
            out.write((msg+"\n").getBytes("UTF-8"));
        }catch(Exception e){
            //System.out.println("I like trains");
        }
    }

    synchronized public void run() {
        boolean ativo = true;
        int count;

        byte[] messageByte = new byte[1024];
        String data = "";
        int bytesRead = 0;
        String msg;

        while (ativo) {
            count = 0;
            try {
                bytesRead = this.in.read(messageByte);
                data = new String(messageByte, 0, bytesRead);
                System.out.println("BYTES LIDOS: " + bytesRead + " DADOS: " + data);
                bytesRead = 0;

                TradutorComandos tc = new TradutorComandos(data);
                //System.out.println(tc.type);
                //System.out.println(tc.erro);
                pedido=0;
                while (pedido<15) {
                    try {
                        if (tc.erro == true || tc.type == null) {
                            msg = "\nUsage: <chave1>: <valor1>, <chave2>: <valor2>\n for help use -> type: help";
                        } else {
                            //System.out.println("entrou else");
                            if (userId == -1) {
                                switch (tc.type) {
                                    case "register":
                                        userId = rmi.register(tc.m);
                                        if (userId == -1) {
                                            msg = "type: register, ok: false";
                                        } else {
                                            this.userName = tc.m.get("username").toString();
                                            msg = "type: register, ok: true";
                                        }
                                        break;
                                    case "login":
                                        userId = rmi.login(tc.m);
                                        if (userId == -1) {
                                            msg = "type: login, ok: false";
                                        } else {
                                            this.userName = tc.m.get("username").toString();
                                            msg = "type: login, ok: true";
                                            Map m = rmi.notificationMessage_offline(userId);
                                            if (m != null) {
                                                Set keys = m.keySet();
                                                for (Iterator i = keys.iterator(); i.hasNext(); ) {
                                                    int key = (Integer) i.next();
                                                    msg=msg+"\n"+m.get(key).toString();
                                                    rmi.removeFromMsgNotifcacao(this.userId, key);
                                                }
                                            }
                                        }
                                        break;
                                    case "help":
                                        System.out.println("ok");
                                        msg = imprimeAjuda();
                                        System.out.println(msg);
                                        break;
                                    case "exit":
                                        msg = "";
                                        for (int i = 0; i < lista.size(); i++) {
                                            if (lista.get(i).clientSocket == this.clientSocket) {
                                                lista.remove(i);
                                                break;
                                            }
                                        }
                                        clientSocket.close();
                                        break;
                                    default:
                                        msg = "\nUsage: <chave1>: <valor1>, <chave2>: <valor2>\n for help use -> type: help";
                                        break;
                                }
                            } else {
                                switch (tc.type) {
                                    case "register":
                                        userId = rmi.register(tc.m);
                                        if (userId == -1) {
                                            msg = "type: register, ok: false";
                                        } else {
                                            userName = tc.m.get("username").toString();
                                            msg = "type: register, ok: true";
                                        }
                                        break;
                                    case "login":
                                        userId = rmi.login(tc.m);
                                        if (userId == -1) {
                                            msg = "type: login, ok: false";
                                        } else {
                                            this.userName = tc.m.get("username").toString();
                                            msg = "type: login, ok: true";
                                            Map m = rmi.notificationMessage_offline(userId);
                                            if (m != null) {
                                                Set keys = m.keySet();
                                                for (Iterator i = keys.iterator(); i.hasNext(); ) {
                                                    int key = (Integer) i.next();
                                                    msg=msg+"\n"+m.get(key).toString();
                                                    rmi.removeFromMsgNotifcacao(this.userId, key);
                                                }
                                            }
                                        }
                                        break;
                                    case "create_auction":
                                        msg = rmi.createAuction(userId, tc.m);
                                        break;
                                    case "search_auction":
                                        msg = rmi.searchAuction(tc.m);
                                        break;
                                    case "detail_auction":
                                        msg = rmi.detailAuction(tc.m);
                                        break;
                                    case "my_auctions":
                                        msg = rmi.myAuctions(userId);
                                        break;
                                    case "bid":
                                        msg = rmi.bid(userId, tc.m, this.userName);
                                        break;
                                    case "edit_auction":
                                        try {
                                            if(tc.getM().containsKey("code"))
                                                Integer.parseInt(tc.getM().get("code").toString());
                                        } catch(NumberFormatException e) {
                                            msg="type: edit_auction, ok: false";
                                            break;
                                        } catch(NullPointerException e) {
                                            msg="type: edit_auction, ok: false";
                                            break;
                                        }
                                        msg = rmi.editAuction(userId, tc.m);
                                        break;
                                    case "message":
                                        msg = rmi.message(userId, tc.m, userName);
                                        break;
                                    case "online_users":
                                        ArrayList<String> als = new ArrayList<String>();
                                        Map usr = MulticastReceiver.users;
                                        Set keys = usr.keySet();
                                        for (Iterator i = keys.iterator(); i.hasNext(); ) {
                                            String key = (String) i.next();
                                            String[] st = usr.get(key).toString().split(",");
                                            for (int k = 0; k < st.length - 1; k++) {
                                                String u = st[k];
                                                System.out.println(u);
                                                if (!als.contains(u) && u != null && u != "" && u != " ") {
                                                    als.add(u);
                                                }
                                            }
                                        }
                                        msg = "type: online_users, users_count: " + als.size();
                                        for (int j = 0; j < als.size(); j++) {
                                            msg = msg + ", users_" + j + "_username: " + als.get(j);
                                        }
                                        break;
                                    case "help":
                                        msg = imprimeAjuda();
                                        break;
                                    case "exit":
                                        msg = "";
                                        for (int i = 0; i < lista.size(); i++) {
                                            if (lista.get(i).clientSocket == this.clientSocket) {
                                                lista.remove(i);
                                                break;
                                            }
                                        }
                                        clientSocket.close();
                                        break;
                                    default:
                                        msg = "\nUsage: <chave1>: <valor1>, <chave2>: <valor2>\n for help use -> type: help";
                                        break;
                                }
                            }
                        }
                        sendMsg(msg);
                        msg = "";
                        break;

                    } catch (StringIndexOutOfBoundsException e) {
                        //e.printStackTrace();
                        try {
                            for (int i = 0; i < lista.size(); i++) {
                                if (lista.get(i).clientSocket == this.clientSocket) {
                                    lista.remove(i);
                                    break;
                                }
                            }
                            clientSocket.close();
                            //System.out.println("O cliente saiu");
                            return;
                        } catch (Exception ee) {
                            ee.printStackTrace();
                            return;
                        }
                    } catch (SocketException e) {
                        // e.printStackTrace();
                        return;
                    } catch (ConnectException e) {
                        // e.printStackTrace();
                        if(pedido==14){
                            try {
                                sendMsg("type: rmi connection error ");
                                for (int i = 0; i < lista.size(); i++) {
                                    if (lista.get(i).clientSocket == this.clientSocket) {
                                        lista.remove(i);
                                        break;
                                    }
                                }
                                clientSocket.close();
                                //System.out.println("O cliente saiu");
                                return;
                            } catch (Exception ee) {ee.printStackTrace();return;}
                        }
                        //System.out.println("-----------"+pedido);
                        pedido++;
                        try {
                            this.sleep(2000);
                        } catch (InterruptedException ex) {}
                        try {
                            rmi = (rmiInterface) LocateRegistry.getRegistry(ps.getProperty("ipServerRMI"), Integer.parseInt(ps.getProperty("RMIPort"))).lookup(ps.getProperty("RMIName"));
                            rmi.subscribe(Server.c);
                        }catch(Exception ee){}
                    } catch (Exception e) {
                        e.printStackTrace();
                        pedido=100;
                    }
                }
            }catch (StringIndexOutOfBoundsException e) {
                try {
                    for (int i = 0; i < lista.size(); i++) {
                        if (lista.get(i).clientSocket == this.clientSocket) {
                            lista.remove(i);
                            break;
                        }
                    }
                    clientSocket.close();
                    //System.out.println("O cliente saiu");
                    return;
                } catch (Exception ee) {
                    ee.printStackTrace();
                    return;
                }
            }catch (Exception e){}
        }
    }

    public String imprimeAjuda() {
        return "Comandos Disponiveis:\n" +
                "type: register, username: <valor1>, password: <valor2>\n" +
                "type: login, username: <valor1>, password: <valor2>\n" +
                "type: create_auction, code: <valor1>, title: <valor2>, description: <valor3>, deadline: <valor4>, amount: <valor5>\n" +
                "type: search_auction, code: <valor1>\n" +
                "type: detail_auction, id: <valor1>\n" +
                "type: my_auctions\n" +
                "type: bid, id: <valor1>, amount: <valor3>\n" +
                "type: edit_auction, id: <valor1>, <campo a editar>: <valor>, <campo a editar>: <valor>\n" +
                "type: message, id <valor1>, text: <valor2>\n" +
                "type: online_users\n" +
                "type: help";
    }
}