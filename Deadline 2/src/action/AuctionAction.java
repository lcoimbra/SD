package action;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.validator.validators.LongRangeFieldValidator;
import org.apache.struts2.interceptor.SessionAware;
import beans.*;

import java.util.Map;

/**
 * Created by kifel on 08/12/2016.
 */
public class AuctionAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String Code, Title, Description, Deadline, Amount;
    private AuctionBean ab = new AuctionBean();

    @Override
    public String execute() {
        if(Code!=null && Code!="" && Title!=null && Title!="" && Description!=null && Description!="" && Deadline!=null && Deadline!="" && Amount!=null && Amount!=""){
            this.ab.setCode(this.Code);
            this.ab.setTitle(this.Title);
            this.ab.setDescription(this.Description);
            this.ab.setAmount(this.Amount);

            //2016-12-09T21:30
            String [] str = Deadline.split("T");
            String [] str2 = str[1].split(":");

            this.ab.setDeadline(str[0]+" "+str2[0]+"-"+str2[1]);

            if(this.session.containsKey("authenticationBean")) {
                AuthenticationBean authb = (AuthenticationBean)this.session.get("authenticationBean");
                this.ab.setRmi(authb.getRmi());
                if(this.session.containsKey("userID")){
                    this.ab.setUsr((Integer)this.session.get("userID"));
                }else{
                    return "erro1"; //caso não exista authenticationBean quer dizer que não esta logado;
                }
            }else{
                return "erro1"; //caso não exista authenticationBean quer dizer que não esta logado;
            }

            String res = this.ab.createAuction();
            if(res.contains("ok: true")){

                this.ab.setBids(null);
                this.ab.setMessage(null);
                this.ab.setId(null);

                this.ab.searchAuction();
                int x = this.ab.getAucBeanArray().size();
                this.ab.setId(this.ab.getAucBeanArray().get(x-1).getId());

                session.put("auction",ab);

                return SUCCESS;
            }else{
                return ERROR;
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

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }

}
