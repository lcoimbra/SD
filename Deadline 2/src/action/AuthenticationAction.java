package action;

import beans.AdminBean;
import beans.AuthenticationBean;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;



import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuthService;
import com.sun.jmx.snmp.SnmpUnknownAccContrModelException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Map;

public class AuthenticationAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username, password , fb, register, code;
    private AdminBean adBean = new AdminBean();

    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
    private static final Token EMPTY_TOKEN = null;
    private static final String apiKey = "1892950157592964";
    private static final String apiSecret = "2d14e49663769d331d8991530da2eb41";

    private String authorizationUrl;
    private String callback = "http://localhost:8080/fbLoginAction";

    @Override
    public String execute() {
        this.authorizationUrl = null;

        if (this.session.containsKey("username")) {
            return SUCCESS;
        } if (code != null) {
            return secondFB();
        } else if (fb != null) {
            firstFB();
            if (this.authorizationUrl != null) {
                return "redirect";
            } else
                return ERROR;
        } else if (this.register != null && (this.username != null && !this.username.equals("")) && (this.password != null && !this.password.equals(""))) {
            this.getAuthenticatinoBean().setUsername(this.username);
            this.getAuthenticatinoBean().setPassword(this.password);
            int res;

            if ((res = this.getAuthenticatinoBean().register()) != -1) {
                session.put("username", this.username);
                session.put("userID", res);
                AuthenticationBean authb = (AuthenticationBean)this.session.get("authenticationBean");
                return "true-user";
            } else {
                return ERROR;
            }
        } else if ((this.username != null && !this.username.equals("")) && (this.password != null && !this.password.equals(""))) {
            this.getAuthenticatinoBean().setUsername(this.username);
            this.getAuthenticatinoBean().setPassword(this.password);
            int res;

            if ((res = this.getAuthenticatinoBean().login()) != -1) {
                session.put("username", this.username);
                session.put("userID", res);
                AuthenticationBean authb = (AuthenticationBean)this.session.get("authenticationBean");
                if (res > 0) {
                    return "true-user";
                } else {
                    adBean.setUsr(res);
                    adBean.setRmi(authb.getRmi());
                    adBean.stats();
                    session.put("stats",adBean);
                    return "true-admin";
                }
            } else {
                return ERROR;
            }
        } else {
            return ERROR;
        }
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public void firstFB() {
        OAuthService service = new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(callback)
                .scope("publish_actions")
                .build();

        // Obtain the Authorization URL
        this.authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);
        System.out.println("auth_url: " + authorizationUrl);
    }

    public String secondFB() {
        OAuthService service = new ServiceBuilder()
                .provider(FacebookApi.class)
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .callback(callback)
                .scope("publish_actions")
                .build();

        System.out.println(authorizationUrl);
        System.out.println();
        Verifier verifier = new Verifier(this.code);

        // Trade the Request Token and Verfier for the Access Token
        Token accessToken;
        try {
            accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        } catch (Exception e) {
            return ERROR;
        }
        System.out.println("(if your curious it looks like this: " + accessToken.getToken() + " )");

        // Now let's go and ask for a protected resource!
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service);
        service.signRequest(accessToken, request);
        Response response = request.send();
        System.out.println(response.getCode());
        System.out.println(response.getBody());

        long fb_id = -1;
        JSONObject dict = (JSONObject) JSONValue.parse(response.getBody());
        fb_id = Long.parseLong(dict.get("id").toString());

        int res = -1;
        if (fb_id != -1 && ((res = this.getAuthenticatinoBean().fbConnect(fb_id, accessToken.getToken())) != -1 )) {
            session.put("userID", res);
            session.put("username", this.getAuthenticatinoBean().getUsername(res));
            return SUCCESS;
        } else {
            return ERROR;
        }
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setAuthenticatinoBean(AuthenticationBean authenticationBean) {
        this.session.put("authenticationBean", authenticationBean);
    }

    public AuthenticationBean getAuthenticatinoBean() {
        if (!session.containsKey("authenticationBean"))
            this.setAuthenticatinoBean(new AuthenticationBean());
        return (AuthenticationBean) session.get("authenticationBean");
    }
}
