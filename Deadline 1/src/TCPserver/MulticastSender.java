package TCPserver;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by joao on 21/10/2016.
 */
public class MulticastSender extends Thread {
    public static final carregarPropriedades ps = new carregarPropriedades("iBei.properties");
    int porto= Integer.parseInt(ps.getProperty("multicastPort"));
    String grupo = ps.getProperty("multicastIp");
    public  static CopyOnWriteArrayList<ligacaoCliente> lista = new CopyOnWriteArrayList<ligacaoCliente>();
    int ttl=1;

    MulticastSender(){
        this.start();
    }

     public void run() {
        try {
            MulticastSocket ms = new MulticastSocket(porto);
            /*criar a string a enviar aqui*/
            while (true) {

                lista=Server.getList();
                String texto = lista.size()+" "+Server.porto+":";
                for(int i=0;i<lista.size();i++){
                    try {
                        if (lista.get(i).userId != -1) {
                           texto = texto + lista.get(i).userName+",";
                        }
                    }catch(Exception e){}
                }
               // System.out.println(texto);
                byte[] m = texto.getBytes();
                DatagramPacket dp = new DatagramPacket(m, m.length, InetAddress.getByName(grupo), porto);
                ms.send(dp);
                try {
                    this.sleep(1000); //enviamos msg de 1 em 1 segundo
                } catch (InterruptedException ex) {
                }
            }
        }catch(Exception e){
          //  e.printStackTrace();
        }
    }
}
