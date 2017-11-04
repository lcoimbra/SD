package Console;
import RMIserver.rmiInterface;
import TCPserver.TradutorComandos;

import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

class TCPClient {
    public String msg;

    public TCPClient(String ip, int porto, rmiInterface ri) {
        int auctionId=0;
        Socket socket;
        PrintWriter outToServer;
        BufferedReader inFromServer = null;
        try {
            socket=new Socket(ip,porto);
            // create streams for writing to and reading from the socket
            inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outToServer = new PrintWriter(socket.getOutputStream(), true);

            String messageFromServer;
            System.out.println("Esta ao nivel da comunicação TCP");

                String str = "type: login, username: dummy1, password: dummy1";
                outToServer.println(str);
                while(((messageFromServer = inFromServer.readLine()) != null)) {
                    System.out.println(messageFromServer);
                    TradutorComandos tc = new TradutorComandos(messageFromServer);
                    if(tc.getType().contains("login")){
                        break;
                    }
                }
                Date dt = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(dt);
                c.add(Calendar.DATE, 1);
                dt = c.getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm");
                String s= dateFormat.format(dt);
                str = "type: create_auction, code: 0000000000000, title: this is a test, description: this is a test, deadline: "+s+", amount: 2";
                outToServer.println(str);
                while((messageFromServer = inFromServer.readLine()) != null) {
                    System.out.println(messageFromServer);
                    TradutorComandos tc = new TradutorComandos(messageFromServer);
                    if(tc.getType().contains("create_auction")){break;}
                }
                str = "type: my_auctions";
                outToServer.println(str);
                while((messageFromServer = inFromServer.readLine()) != null) {
                    System.out.println(messageFromServer);
                    TradutorComandos tc = new TradutorComandos(messageFromServer);
                    if(tc.getType().contains("my_auctions")){
                        if(tc.getM().containsKey("items_0_id")) {
                           // System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                            auctionId = Integer.parseInt(tc.getM().get("items_0_id").toString());
                           // System.out.println("----"+auctionId);
                        }
                        break;
                    }
                }
                str = "type: login, username: dummy2, password: dummy2";
                outToServer.println(str);
                while((messageFromServer = inFromServer.readLine()) != null) {
                    System.out.println(messageFromServer);
                    TradutorComandos tc = new TradutorComandos(messageFromServer);
                    if(tc.getType().contains("login")){break;}
                }
                str = "type: bid, id: "+auctionId+", amount: 1";
                outToServer.println(str);
                while((messageFromServer = inFromServer.readLine()) != null) {
                    System.out.println(messageFromServer);
                    TradutorComandos tc = new TradutorComandos(messageFromServer);
                    if(tc.getType().contains("bid")){break;}
                }
                str = "type: detail_auction, id: "+auctionId;
                outToServer.println(str);
                while((messageFromServer = inFromServer.readLine()) != null) {
                    System.out.println(messageFromServer);
                    TradutorComandos tc = new TradutorComandos(messageFromServer);
                    if (tc.getType().contains("detail_auction")) {
                        break;
                    }
                }

                ri.removeAuction(auctionId);
                socket.close();

            // the main thread loops reading from the server and writing to System.out
        } catch (IOException e) {
            if(inFromServer == null)
                System.out.println("\nUsage: java TCPClient <host> <port>\n");
                //System.out.println(e.getMessage());
                msg="erro1";
        } finally {
            try { inFromServer.close(); } catch (Exception e) {}
        }
    }

}

