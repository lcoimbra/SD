package interceptor;

import action.AuthenticationAction;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

import static org.apache.struts2.StrutsStatics.HTTP_REQUEST;

public class MyInterceptor implements Interceptor {
    private static final long serialVersionUID = 4L;

    @Override
    public void destroy() {

    }

    @Override
    public void init() {

    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> session = invocation.getInvocationContext().getSession();

        Action action = (Action) invocation.getAction();

        if(session.containsKey("username")) {
            return invocation.invoke();
        } else if (action instanceof AuthenticationAction) {
            return invocation.invoke();
        } else {
            return Action.LOGIN;
        }
    }
}
