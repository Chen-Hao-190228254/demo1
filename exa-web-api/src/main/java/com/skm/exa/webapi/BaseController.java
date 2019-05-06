package com.skm.exa.webapi;

import com.skm.exa.common.object.UnifyUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author dhc
 * 2019-03-07 15:52
 */
public abstract class BaseController {

    @Autowired
    protected HttpServletRequest request;

    @Resource   //资源
    protected HttpServletResponse response;

    protected UnifyUser getCurrentUser() {
        return getCurrentUser(true);
    }

    protected UnifyUser getCurrentUser(boolean required) {
        Object object = request.getUserPrincipal();
        if (object instanceof UsernamePasswordAuthenticationToken) {
            Object details = ((UsernamePasswordAuthenticationToken) object).getPrincipal();
            if (details instanceof UnifyUser) {
                return (UnifyUser) details;
            }
        }
        if (required) {
            throw new RuntimeException("用户未登录");
        }
        return null;
    }
}
