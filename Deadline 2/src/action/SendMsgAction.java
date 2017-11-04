package action;

import TCPserver.TradutorComandos;
import TCPserver.tcpInterface;
import beans.AuctionBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by kifel on 10/12/2016.
 */
public class SendMsgAction extends ActionSupport implements SessionAware{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String message;
    private AuctionBean ab = new AuctionBean();

    @Override
    public String execute(){
        if (session.containsKey("auction") && session.containsKey("userID")&& session.containsKey("username")) {
            this.ab =(AuctionBean) session.get("auction");
            this.ab.setUsr((Integer)session.get("userID"));
            this.ab.setUsrName((String)session.get("username"));
            this.ab.setText(this.message);
            String res = this.ab.writeMessage();
            System.out.println(res);
            if (res.contains("ok: true")){
                ab.getMessage().add((String)session.get("username")+": "+this.message);
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
