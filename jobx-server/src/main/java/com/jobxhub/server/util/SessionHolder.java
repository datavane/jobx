package com.jobxhub.server.util;

import com.jobxhub.common.Constants;
import com.jobxhub.service.model.User;

import javax.servlet.http.HttpSession;

public class SessionHolder {

    private static ThreadLocal<HttpSession> localSession = new ThreadLocal<HttpSession>();

    public static void holdSession(HttpSession httpSession) {
        localSession.remove();
        localSession.set(httpSession);
    }

    public static void invalidSession() {
        HttpSession session = localSession.get();
        String xsrf = (String) session.getAttribute(Constants.PARAM_XSRF_NAME_KEY);
        if (xsrf != null) {
            session.removeAttribute(xsrf);
        }
        session.removeAttribute(Constants.PARAM_XSRF_NAME_KEY);
        localSession.remove();
    }

    public static User getUser() {
        if (localSession.get() == null) {
            return null;
        }
        HttpSession session = localSession.get();
        String xsrf = (String) session.getAttribute(Constants.PARAM_XSRF_NAME_KEY);
        return (User) localSession.get().getAttribute(xsrf);
    }

}
