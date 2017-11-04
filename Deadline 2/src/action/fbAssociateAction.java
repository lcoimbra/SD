package action;

import beans.AuthenticationBean;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.interceptor.SessionAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Map;

/**
 * Created by User on 15/12/2016.
 */
public class fbAssociateAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String code;

    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
    private static final Token EMPTY_TOKEN = null;
    private static final String apiKey = " ______ ";
    private static final String apiSecret = " _______ ";

    private String authorizationUrl;
    private String callback = "http://localhost:8080/fbAssociateAction";

    @Override
    public String execute() {
        if (code == null) {
            firstFB();
            if (this.authorizationUrl != null) {
                return "redirect";
            } else
                return ERROR;
        } else {
            return secondFB();
        }
    }

    @Override
    public void setSession(Map<String, Object> map) {
        this.session = map;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
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
        if (fb_id != -1 && ((AuthenticationBean) this.session.get("authenticationBean")).fbAssociate((int) session.get("userID"), fb_id, accessToken.getToken())) {
            return SUCCESS;
        } else {
            return ERROR;
        }
    }
}
