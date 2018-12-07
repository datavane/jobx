package com.jobxhub.server.util;

import com.jobxhub.common.Constants;
import com.jobxhub.service.model.User;

import javax.servlet.http.HttpSession;

public class SessionUtils {

    public static void invalidSession(HttpSession session) {
        String xsrf = (String) session.getAttribute(Constants.PARAM_XSRF_NAME_KEY);
        if (xsrf != null) {
            session.removeAttribute(xsrf);
        }
        session.removeAttribute(Constants.PARAM_XSRF_NAME_KEY);
        session.invalidate();
    }

    public static User getUser(HttpSession session) {
        String xsrf = (String) session.getAttribute(Constants.PARAM_XSRF_NAME_KEY);
        if (xsrf == null) {
            return null;
        }
        return (User) session.getAttribute(xsrf);
    }

}
