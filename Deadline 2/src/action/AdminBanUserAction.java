package action;

import beans.AdminBean;
import beans.AuthenticationBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * Created by kifel on 10/12/2016.
 */
public class AdminBanUserAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String user;
    private AdminBean adminBean = new AdminBean();

    @Override
    public String execute() {
        if(user!=null && !user.isEmpty()){
            if(session.containsKey("authenticationBean") && session.containsKey("userID")){
                adminBean.setUsr((Integer) session.get("userID"));
                AuthenticationBean authb = (AuthenticationBean)this.session.get("authenticationBean");
                this.adminBean.setRmi(authb.getRmi());
            }else{
                return "erro1";
            }
            this.adminBean.setBan(this.user);
            String res = this.adminBean.banUser();
            if(res.contains("ok: true")){
                return SUCCESS;
            }
        }
        return ERROR;
    }

    public String stats(){
        if (session.containsKey("stats")){
            AdminBean adb = (AdminBean) session.get("stats");
            adb.stats();
            session.put("stats",adb);
            return SUCCESS;
        }else{
            return ERROR;
        }

    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }

}
