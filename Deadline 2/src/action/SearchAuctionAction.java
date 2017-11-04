package action;

import beans.AuctionBean;
import beans.AuthenticationBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * Created by kifel on 09/12/2016.
 */
public class SearchAuctionAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String Code;
    private AuctionBean ab = new AuctionBean();

    @Override
    public String execute() {
        if(Code!=null && Code!=""){
            this.ab.setCode(this.Code);
            if(this.session.containsKey("authenticationBean")) {
                AuthenticationBean authb = (AuthenticationBean)this.session.get("authenticationBean");
                this.ab.setRmi(authb.getRmi());
            }else{
                return "errorSession"; //caso não exista authenticationBean quer dizer que não esta logado;
            }
            String res = this.ab.searchAuction();
            if (res.contains("ok: true") && this.ab.getAucBeanArray().size() > 0) {
                session.put("searchAuction", ab);
                return SUCCESS;
            }
        }
        return ERROR;

    }


    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
}
