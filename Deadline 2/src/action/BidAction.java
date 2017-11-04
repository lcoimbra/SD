package action;

import beans.AuctionBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * Created by kifel on 10/12/2016.
 */
public class BidAction  extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String amount;
    private AuctionBean ab = new AuctionBean();

    @Override
    public String execute(){
        if (session.containsKey("auction") && session.containsKey("userID")&& session.containsKey("username")) {
            this.ab =(AuctionBean) session.get("auction");
            this.ab.setUsr((Integer)session.get("userID"));
            this.ab.setUsrName((String)session.get("username"));
            this.ab.setAmount(this.amount);
            String res = this.ab.bid();
            System.out.println(res);
            if (res.contains("ok: true")){
                ab.getBids().add((String)session.get("username")+": "+amount);
                session.put("auction",ab);
                return SUCCESS;
            }
        }
        return ERROR;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
