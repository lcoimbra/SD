/*
  substituir no header.jsp o botao de logout pela seguinte linha
  




*/
package action;

import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import ws.WebSocketAnnotation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by kifel on 17/12/2016.
 */
public class LogoutAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;


    @Override
    public String execute() {
        Iterator i = this.session.keySet().iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            session.remove(s);
        }
        return SUCCESS;
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }
}
