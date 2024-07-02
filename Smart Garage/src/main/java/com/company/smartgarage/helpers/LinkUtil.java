package com.company.smartgarage.helpers;

import jakarta.servlet.http.HttpServletRequest;

public class LinkUtil {
    public static String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}
