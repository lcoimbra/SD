package TCPserver;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MulticastReceiver extends Thread{
    public static final carregarPropriedades ps = new carregarPropriedades("iBei.properties");
    int porto= Integer.parseInt(ps.getProperty("multicastPort"));
    String grupo = ps.getProperty("multicastIp");


    public static Map users= new HashMap();
    ConcurrentMap<String, ArrayList <String>> servers=new ConcurrentHashMap<String, ArrayList <String>>();
    ArrayList<String> al;

    MulticastReceiver(){
        this.start();
    }

     public void run(){
        try {
            MulticastSocket ms = new MulticastSocket(porto);
            ms.joinGroup(InetAddress.getByName(grupo));
            while(true) {
                byte buff[] = new byte[1024];
                DatagramPacket dp = new DatagramPacket(buff, buff.length);
                ms.receive(dp);
                String data = new String(buff, 0, buff.length);

                //System.out.println("Multicast receiver data:"+data);
                String[] dat = data.split(":");
                String[] da = dat[0].split(" ");

                try {
                    String ipPorto = dp.getAddress().toString() + ":" + da[1];

                    if (servers.containsKey(ipPorto)) {
                        //System.out.println("MulticastReceiver: entrei -------");
                        al = servers.get(ipPorto);
                        al.set(0, da[0]);
                        al.set(3, "-1");
                        servers.put(ipPorto, al);
                        if (Integer.parseInt(da[0]) > 0)
                            users.put(ipPorto, dat[1]);
                    } else {
                        al = new ArrayList<>();
                        al.add(da[0]);
                        al.add(da[1]);
                        al.add(dp.getAddress().toString());
                        al.add("-1");
                        servers.put(ipPorto, al);
                        if (Integer.parseInt(da[0]) > 0) {
                            users.put(ipPorto, dat[1]);
                        }
                    }
                }catch(Exception e){}
                String texto="type: notification_load, server_list: "+ servers.size();
                Set keys = servers.keySet();
                int j=0;
                for(Iterator i = keys.iterator(); i.hasNext();){
                    String key = (String) i.next();
                    al = servers.get(key);
                    texto=texto+", server_"+j+"_hostname: "+al.get(2)+", server_"+j+"_port: "+al.get(1)+", server_"+j+++"_load: "+al.get(0);
                }

                Server.msg=texto;

                /*Editar se houver tempo*/
                for(Iterator i = keys.iterator(); i.hasNext();) {
                    String key = (String) i.next();
                    al = servers.get(key);
                    if(Integer.parseInt(al.get(3))==10){
                        servers.remove(key);
                    }else{
                        int aux=Integer.parseInt(al.get(3))+1;
                        al.set(3,""+aux);
                        servers.put(key,al);
                    }
                }
            }
        }catch (Exception e){/*e.printStackTrace();*/}
    }
}
