package action;

import beans.AuctionBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * Created by kifel on 10/12/2016.
 */
public class AdminCancelAuction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private AuctionBean ab = new AuctionBean();

    @Override
    public String execute(){
        if (session.containsKey("auction") && session.containsKey("userID")) {
            this.ab = (AuctionBean) session.get("auction");
            this.ab.setUsr((Integer)session.get("userID"));
            String res = this.ab.cancel();
            if (res.contains("ok: true")) {
                this.ab.setStatus("cancelled");
                this.session.put("auction", this.ab);
                return SUCCESS;
            }
        }
        return ERROR;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
}
