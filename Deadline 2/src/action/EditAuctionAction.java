package action;

import beans.AuctionBean;
import beans.AuthenticationBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.util.Map;

/**
 * Created by kifel on 09/12/2016.
 */
public class EditAuctionAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String Id;
    private String Code, Title, Description, Deadline, Amount;
    private AuctionBean ab = new AuctionBean();

    @Override
    public String execute() {
        if(Id!=null && Id!=""){
            this.ab.setId(this.Id);
            if(this.session.containsKey("authenticationBean")) {
                AuthenticationBean authb = (AuthenticationBean)this.session.get("authenticationBean");
                this.ab.setRmi(authb.getRmi());
            }else{
                return "erro1"; //caso não exista authenticationBean quer dizer que não esta logado;
            }
            String res = this.ab.detailAuction();
            if(res.contains("ok: true")){
                session.put("editAuction",ab);
                return SUCCESS;
            }
        }
        return ERROR;
    }

    public String edit(){
        System.out.println("estou aqui edit()");
        if(this.session.containsKey("editAuction")) {
            this.ab = (AuctionBean) this.session.get("editAuction");
            System.out.println("estou aqui edit() 22222");
            if (this.session.containsKey("userID")){
                this.ab.setUsr((Integer) this.session.get("userID"));
            }else {
                System.out.println("entrou else");
                return "erro1";
            }
            if(this.Code != null && !this.Code.isEmpty()){
                this.ab.setCode(this.Code);
            }
            if(this.Title != null && !this.Title.isEmpty()){
                this.ab.setTitle(this.Title);
            }
            if(this.Description != null && !this.Description.isEmpty()){
                this.ab.setDescription(this.Description);
            }
            if(this.Deadline.contains("T")){
                String [] str = Deadline.split("T");
                String [] str2 = str[1].split(":");
                this.ab.setDeadline(str[0]+" "+str2[0]+"-"+str2[1]);
            }
            if(this.Amount != null && !this.Amount.isEmpty()){
                this.ab.setAmount(this.Amount);
            }
            String res = this.ab.editAuction();
            System.out.println(res);
            if(res.contains("ok: true")){
                this.session.put("auction",ab);
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

    public AuctionBean getAb() {
        return ab;
    }

    public void setAb(AuctionBean ab) {
        this.ab = ab;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
}
