package com.ms.cookie.util;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CookieUtils {
	
    private Map<String, MsCookie> cookieMap;

    /**
     * 根据cookie名称获取客户端cookie的值
     *
     * @param servletRequest
     * @param name
     * @return
     */
    public String getCookieValue(HttpServletRequest servletRequest, String name) {
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if (cookieName.equals(name)) {
                    if (cookieMap != null && cookieMap.containsKey(name)) {
                    	MsCookie msCookie = cookieMap.get(name);
                        return msCookie.getValue(cookie.getValue());
                    }
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 删除一个cookie
     *
     * @param servletResponse
     * @param name
     */
    public void deleteCookie(HttpServletResponse servletResponse, String name) {
        Cookie cookie;
        if (cookieMap != null && cookieMap.containsKey(name)) {
        	MsCookie msCookie = cookieMap.get(name);
            cookie = msCookie.newCookie(null);
        } else {
            cookie = new Cookie(name, null);
        }
        cookie.setMaxAge(0);
        servletResponse.addCookie(cookie);
    }

    /**
     * 将cookie写入到客户端
     *
     * @param servletResponse
     * @param name
     * @param value
     */
    public void addCookie(HttpServletResponse servletResponse, String name, String value) {
        if (cookieMap != null && cookieMap.containsKey(name)) {
        	MsCookie msCookie = cookieMap.get(name);
            Cookie cookie = msCookie.newCookie(value);
            servletResponse.addCookie(cookie);
        } else {
            throw new RuntimeException("Cookie " + name + " is undefined!");
        }
    }


    /**
     * 初始化配置好的有效的cookie列表
     * @param msCookieList
     */
    public void setMsCookie(List<MsCookie> msCookieList) {
        if (msCookieList != null) {
            HashMap<String, MsCookie> msCookieHashMap = new HashMap<String, MsCookie>(msCookieList.size());
            for (MsCookie msCookie : msCookieList) {
                msCookieHashMap.put(msCookie.getName(), msCookie);
            }
            cookieMap = msCookieHashMap;
        }
    }

    /**
     * 退出时将所有的cookie置为失效
     *
     * @param request
     * @param response
     */
    public void invalidate(HttpServletRequest request, HttpServletResponse response) {
        if (cookieMap != null && cookieMap.size() > 0) {
            for (Map.Entry<String, MsCookie> entry : cookieMap.entrySet()) {
                String key = entry.getKey();
                MsCookie msCookie = entry.getValue();
                if (msCookie.getExpiry() < 1) {
                    if (StringUtils.isNotEmpty(getCookieValue(request, key))) {
                        deleteCookie(response, key);
                    }
                }
            }
        }
    }

}

