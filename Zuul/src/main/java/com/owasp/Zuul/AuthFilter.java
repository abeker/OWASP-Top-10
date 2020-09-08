package com.owasp.Zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import feign.FeignException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings({"IfStatementWithIdenticalBranches", "SpellCheckingInspection"})
@Component
public class AuthFilter extends ZuulFilter {

    private final AuthClient authClient;

    public AuthFilter(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    private void setFailedRequest(String body, int code) {
        RequestContext ctx = RequestContext.getCurrentContext();
        ctx.setResponseStatusCode(code);
        if (ctx.getResponseBody() == null) {
            ctx.setResponseBody(body);
            ctx.setSendZuulResponse(false);
        }
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        if(request.getHeader("Auth-Token") == null) {
            return null;
        }

        String token = request.getHeader("Auth-Token");
        try {
            String username = authClient.verify(token);
            if(username != null) {
                String permissionList = authClient.getPermission(token);
                ctx.addZuulRequestHeader("roles", permissionList);
                ctx.addZuulRequestHeader("username", username);
            }
        } catch (FeignException.NotFound e) {
            setFailedRequest("User does not exist!", 403);
        }

        return null;
    }


}
