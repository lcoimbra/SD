package action;

import beans.AuctionBean;
import beans.AuthenticationBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by kifel on 09/12/2016.
 */
public class MyAuctionsAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private AuctionBean ab = new AuctionBean();

    @Override
    public String execute() {
        if(this.session.containsKey("authenticationBean") && this.session.containsKey("userID")) {
            AuthenticationBean authb = (AuthenticationBean)this.session.get("authenticationBean");
            this.ab.setRmi(authb.getRmi());
            this.ab.setUsr((Integer)this.session.get("userID"));
            String res = this.ab.myAuction();
            if(res.contains("ok: true")){
                this.session.put("myAuctions",ab);
                return SUCCESS;
            }
        }else{
            return "erro1"; //caso não exista authenticationBean quer dizer que não esta logado;
        }
        return ERROR;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
}
