package beans;

import RMIserver.rmiInterface;
import TCPserver.TradutorComandos;
import javassist.runtime.Desc;
import sun.security.krb5.internal.crypto.Des;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kifel on 08/12/2016.
 */
public class AuctionBean {
    private rmiInterface rmi;
    private String Code, Title, Description, Deadline, Amount, Id, status, text;
    private ArrayList<String> message;
    private ArrayList<String> bids;
    private int usr;
    private String usrName;
    private ArrayList<AuctionBean> aucBeanArray;

    public AuctionBean(){}

    public String writeMessage(){
        Map<String, String> m = new HashMap<>();
        m.put("id",this.Id);
        m.put("text",this.text);
        try {
            return rmi.message(usr,m,usrName);
        } catch (RemoteException e) {
            // e.printStackTrace();
            return "type: bid, ok: false";
        }
    }

    public String bid(){
        Map<String, String> m = new HashMap<>();
        m.put("amount",this.Amount);
        m.put("id",this.Id);
        try {
            return rmi.bid(usr,m,usrName);
        } catch (RemoteException e) {
            // e.printStackTrace();
            return "type: bid, ok: false";
        }
    }

    public String cancel(){
        try {
            return rmi.cancelAuction(this.usr,Integer.parseInt(this.Id));
        } catch (RemoteException e) {
            // e.printStackTrace();
            return "type: create_auction, ok: false";
        }
    }

    public String createAuction(){
        Map<String, String> m = new HashMap<>();
        this.aucBeanArray=null;

        m.put("code", this.Code);
        m.put("title", this.Title);
        m.put("description",this.Description);
        m.put("deadline", this.Deadline);
        m.put("amount", this.Amount);

        try {
            return rmi.createAuction(usr,m);
        } catch (RemoteException e) {
            //e.printStackTrace();
            return "type: create_auction, ok: false";
        }
    }

    public String detailAuction(){
        Map<String, String> m = new HashMap<>();
        this.message=new ArrayList<>();
        this.bids=new ArrayList<>();
        this.aucBeanArray=null;

        m.put("id",this.Id);
        try {
            String res = rmi.detailAuction(m);
            if (!res.contains("ok: false")) {
                TradutorComandos tc = new TradutorComandos(res);
                if (tc.getM().containsKey("code")) {
                    this.Code = tc.getM().get("code").toString();
                } else {
                    this.Code = null;
                }
                if (tc.getM().containsKey("title")) {
                    this.Title = tc.getM().get("title").toString();
                } else {
                    this.Title = null;
                }
                //this.Description=tc.getM().get("description").toString();
                if (tc.getM().containsKey("description")) {
                    this.Description = tc.getM().get("description").toString();
                } else {
                    this.Description = null;
                }
                if (tc.getM().containsKey("deadline")) {
                    this.Deadline = tc.getM().get("deadline").toString();
                } else {
                    this.Deadline = null;
                }
                if (tc.getM().containsKey("amount")) {
                    this.Amount = tc.getM().get("amount").toString();
                } else {
                    this.Amount = null;
                }
                if (tc.getM().containsKey("status")) {
                    this.status = tc.getM().get("status").toString();
                } else {
                    this.status = null;
                }
                if (tc.getM().containsKey("messages_count")) {
                    for (int i = 0; i < Integer.parseInt(tc.getM().get("messages_count").toString()); i++) {
                        this.message.add(tc.getM().get("messages_" + i + "_user").toString() + ": " + tc.getM().get("messages_" + i + "_text").toString());
                    }
                }
                if (tc.getM().containsKey("bids_count")) {
                    int aux = Integer.parseInt(tc.getM().get("bids_count").toString());
                    for (int i = 0; i < aux; i++) {
                        this.bids.add(tc.getM().get("bids_" + i + "_user").toString() + ": " + tc.getM().get("bids_" + i + "_amount").toString());
                    }
                }
                return "type: detail_auction, ok: true";
            }
        } catch (RemoteException e) {
            // e.printStackTrace();
        }
        return "type: detail_auction, ok: false";
    }

    public String searchAuction(){
        Map<String, String> m = new HashMap<>();
        this.message=null;
        this.bids=null;
        this.aucBeanArray=new ArrayList<>();
        m.put("code",this.Code);
        try {
            String res = rmi.searchAuction(m);
            TradutorComandos tc = new TradutorComandos(res);
            for(int i=0; i < Integer.parseInt(tc.getM().get("items_count").toString());i++){
                AuctionBean auxbean = new AuctionBean();
                auxbean.setCode(tc.getM().get("items_"+i+"_code").toString());
                auxbean.setTitle(tc.getM().get("items_"+i+"_title").toString());
                auxbean.setDescription(null);
                auxbean.setDeadline(null);
                auxbean.setAmount(null);
                auxbean.setId(tc.getM().get("items_"+i+"_id").toString());
                auxbean.setMessage(null);
                auxbean.setBids(null);
                auxbean.setAucBeanArray(null);
                aucBeanArray.add(auxbean);
            }
            return "type: search_auction, ok: true";
        } catch (RemoteException e) {
            // e.printStackTrace();
            return "type: search_auction, ok: false";
        }
    }

    public String myAuction(){
        this.message=null;
        this.bids=null;
        this.aucBeanArray=new ArrayList<>();
        try {
            String res = rmi.myAuctions(this.usr);
            TradutorComandos tc = new TradutorComandos(res);
            for(int i=0;i<Integer.parseInt(tc.getM().get("items_count").toString());i++){
                AuctionBean auxbean = new AuctionBean();
                auxbean.setCode(tc.getM().get("items_"+i+"_code").toString());
                auxbean.setTitle(tc.getM().get("items_"+i+"_title").toString());
                auxbean.setDescription(null);
                auxbean.setDeadline(null);
                auxbean.setAmount(null);
                auxbean.setId(tc.getM().get("items_"+i+"_id").toString());
                auxbean.setMessage(null);
                auxbean.setBids(null);
                auxbean.setAucBeanArray(null);
                aucBeanArray.add(auxbean);
            }
            return "type: my_auction, ok: true";
        } catch (RemoteException e) {
            // e.printStackTrace();
            return "type: my_auction, ok: false";
        }
    }

    public String editAuction(){
        Map<String, String> m = new HashMap<>();

        try {
            m.put("id", this.Id);
            if (this.Code!=null && !this.Code.isEmpty()) {
                m.put("code", this.Code);
            }
            if (this.Title!=null && !this.Title.isEmpty()) {
                m.put("title", this.Title);
            }
            if (this.Description!=null  && !this.Description.isEmpty()) {
                m.put("description", this.Description);
            }
            if (this.Deadline!=null  && !this.Deadline.isEmpty()) {
                m.put("deadline", this.Deadline);
            }
            if (this.Amount!=null && !this.Amount.isEmpty()) {
                m.put("amount", this.Amount);
            }

            System.out.println(this.Id);
            System.out.println(this.Code);
            System.out.println(this.Title);
            System.out.println(this.Description);
            System.out.println(this.Deadline);
            System.out.println(this.Amount);
            System.out.println("usr: " + this.usr);
            String cenas = rmi.editAuction(usr,m);
            System.out.println(cenas);
            return cenas;
        } catch (Exception e) {
            e.printStackTrace();
            return "type: edit_auction, ok: false";
        }
    }

    public ArrayList<AuctionBean> getAucBeanArray() {
        return aucBeanArray;
    }

    public void setAucBeanArray(ArrayList<AuctionBean> aucBeanArray) {
        this.aucBeanArray = aucBeanArray;
    }

    public String getId() {
        return Id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public ArrayList<String> getBids() {
        return bids;
    }

    public void setBids(ArrayList<String> bids) {
        this.bids = bids;
    }

    public ArrayList<String> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<String> message) {
        this.message = message;
    }

    public int getUsr() {
        return usr;
    }

    public void setUsr(int usr) {
        this.usr = usr;
    }

    public rmiInterface getRmi() {
        return rmi;
    }

    public void setRmi(rmiInterface rmi) {
        this.rmi = rmi;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDeadline() {
        return Deadline;
    }

    public void setDeadline(String deadline) {
        Deadline = deadline;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getUsrName() {
        return usrName;
    }

    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
