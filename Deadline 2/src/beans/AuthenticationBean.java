package beans;

import RMIserver.rmiInterface;
import TCPserver.carregarPropriedades;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class AuthenticationBean implements java.io.Serializable {
    //public static final carregarPropriedades propriedades = new carregarPropriedades("iBei.properties");

    private final String ip = "192.168.1.4";
    private final int porto = 7000;
    private final String nameService = "iBei";

    private rmiInterface rmi;
    private String username;
    private String password;


    public AuthenticationBean() {
        try {
            rmi = (rmiInterface) LocateRegistry.getRegistry(ip, porto).lookup(nameService);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public int register(){
        Map<String, String> m = new HashMap<>();

        m.put("username", this.username);
        m.put("password", this.password);

        try {
            return rmi.register(m);
        } catch (RemoteException e) {
            // e.printStackTrace();
            return -1;
        }
    }

    public int login() {
        Map<String, String> m = new HashMap<>();

        m.put("username", this.username);
        m.put("password", this.password);
        try {
            return rmi.login(m);
        } catch (RemoteException e) {
            //System.out.println("RMI DESLIGADO");
            //e.printStackTrace();
            return -1;
        }
    }

    public int fbConnect(long fb_id, String fb_token) {
        try {
            return rmi.fbVerifyUser(fb_id, fb_token);
        } catch (RemoteException e) {
            //e.printStackTrace();
            return -1;
        }
    }

    public boolean fbAssociate(int userID, long fb_id, String fb_token) {
        try {
            return rmi.fbAssociate(userID, fb_id, fb_token);
        } catch (RemoteException e) {
            //e.printStackTrace();
            return false;
        }
    }

    public boolean fbIsAssociated(int userID) { // called by header.jsp
        try {
            return rmi.fbIsAssociated(userID);
        } catch (RemoteException e) {
            //e.printStackTrace();
            return false;
        }
    }
/*
    public ArrayList<String> fbGetToken(int userID) {
        try {
            return rmi.fbGetToken(userID);
        } catch (RemoteException e) {
            //e.printStackTrace();
            return null;
        }
    }
*/
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public rmiInterface getRmi() {
        return rmi;
    }

    public String getUsername(int userID) {
        if (this.username == null) {
            try {
                return rmi.getUsername(userID);
            } catch (RemoteException e) {
                //e.printStackTrace();
                return null;
            }
        } else {
            return username;
        }
    }
}
