package beans;

import RMIserver.rmiInterface;
import TCPserver.TradutorComandos;

import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Created by kifel on 10/12/2016.
 */
public class AdminBean {
    private rmiInterface rmi;
    private String ban;
    private int usr;
    private ArrayList<String> creation;
    private ArrayList<String> won;
    private String nClose;

    public AdminBean(){}

    public String stats(){
        this.creation=new ArrayList<>();
        this.won=new ArrayList<>();
        try {
            String res = rmi.stats(this.usr);
            TradutorComandos tc = new TradutorComandos(res);
            for(int i=0; i<10; i++){
                if(tc.getM().containsKey("top10_auction_creation_"+i)){
                     creation.add(tc.getM().get("top10_auction_creation_"+i).toString());
                }else{
                    break;
                }
            }
            for(int i=0; i<10; i++){
                if(tc.getM().containsKey("top10_auction_won_"+i)){
                    won.add(tc.getM().get("top10_auction_won_"+i).toString());
                }else{
                    break;
                }
            }
            if(tc.getM().containsKey("auction_closed_last_10_days")) {
                this.nClose = tc.getM().get("auction_closed_last_10_days").toString();
            }
            return "type: stats, ok: true";
        } catch (RemoteException e) {
            // e.printStackTrace();
            return "type: stats, ok: false";
        }

    }

    public String banUser(){
        try {
            return rmi.banUser(this.usr,this.ban);
        } catch (RemoteException e) {
            // e.printStackTrace();
            return "type: ban_user, ok: false";
        }
    }

    public boolean unlinkUser(){
        try {
            return rmi.fbUnlinkAccount(this.ban);
        } catch (RemoteException e) {
            // e.printStackTrace();
            return false;
        }
    }

    public ArrayList<String> getCreation() {
        return creation;
    }

    public void setCreation(ArrayList<String> creation) {
        this.creation = creation;
    }

    public ArrayList<String> getWon() {
        return won;
    }

    public void setWon(ArrayList<String> won) {
        this.won = won;
    }

    public String getnClose() {
        return nClose;
    }

    public void setnClose(String nClose) {
        this.nClose = nClose;
    }

    public String getBan() {
        return ban;
    }

    public void setBan(String ban) {
        this.ban = ban;
    }

    public rmiInterface getRmi() {
        return rmi;
    }

    public void setRmi(rmiInterface rmi) {
        this.rmi = rmi;
    }

    public int getUsr() {
        return usr;
    }

    public void setUsr(int usr) {
        this.usr = usr;
    }
}
