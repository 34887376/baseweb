/**
 * 
 */
package base.test.interceptor.login;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import base.test.base.util.JsonUtil;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * @author zhoushanjie
 * 
 */
public class LoginInterceptor implements Interceptor {

    private static final long serialVersionUID = 5058991461480309218L;

    Logger logger = Logger.getLogger(this.getClass());

    private static final String ACTIVE_USER_KEY = "activeUser&";

    // 用户最后一次登录时间间隔，在此之前的为活跃用户
    private static final int ACTIVE_TIME_KEEP = 60 * 60 * 24 * 30;

    // 用户pin写入的cookie名字
    private String pinCookieName;

    // 校验用户pin是否有效的key值
    private String keyCookieName;

    // 登录后跳转的应用域名
    private String targetDomainUrl;

    // 登录时要跳转的url
    private String loginUrl;

    private String loginCheckUrl;

    // 登录时申请的appId
    private int appId;

    // 设置到请求上下文的用户pin的key
    private String pinName;

    public String intercept(ActionInvocation invocation) throws Exception {
        try {
            // 取得请求相关的ActionContext实例
            ActionContext actionContext = invocation.getInvocationContext();
            HttpServletResponse response = ServletActionContext.getResponse();
            HttpServletRequest request = ServletActionContext.getRequest();
            String clientIP = request.getRemoteAddr();
            String redirectLoginUrl = loginUrl + "?appid=" + appId;
            // TODO 登录拦截，测试环境写死，上线前改回来
             String pin = cookieValue(request, pinCookieName);
            String pt_key = cookieValue(request, keyCookieName);
            // fix by chenchangqun
            // 修正原来的逻辑，防止空指针异常
            // 通过校验获得pin
             if (pin == null) {
             redirectLogin(response, request, redirectLoginUrl);
             return null;
             }
//            String pin = "testnewuser";
            String pinDecodeStr = URLDecoder.decode(pin, "UTF-8");
            // 设置pin到请求上下文，和baseaction相对应
            request.setAttribute(pinName, pinDecodeStr);
            request.setCharacterEncoding("UTF-8");
            // 判断用户是否为活跃用户，如果不是活跃用户则同步该用户的全量数据，并设置为活跃用户
            String path = request.getContextPath();
            String serveletPath = request.getServletPath();
            String queryParm = request.getQueryString();
            Map paramMap = request.getParameterMap();
            logger.error(redirectLoginUrl);
            System.out.println("拦截器路径:   " + path + "  serveletPath=" + serveletPath + "    queryParm=" + queryParm + "       paramMap="
                    + JsonUtil.toJson(paramMap));
            return invocation.invoke();
        } catch (Exception e) {
            logger.error("LoginInterceptor.intercept登录拦截器，校验用户登录过程中发生异常！！！", e);
        }
        return invocation.invoke();

    }

    private void redirectLogin(HttpServletResponse response, HttpServletRequest request, String redirectLoginUrl) {
        try {
            String targetPath = request.getServletPath();
            // if (StringUtils.isNotBlank(request.getQueryString())) {
            // targetPath = targetPath + "?" + request.getQueryString();
            // }
            Map<String, String[]> paramMap = request.getParameterMap();
            if (MapUtils.isNotEmpty(paramMap)) {
                boolean isFirstParam = true;
                for (Entry<String, String[]> param : paramMap.entrySet()) {
                    String paramKey = param.getKey();
                    String[] value = param.getValue();
                    if (value != null && value.length > 0) {
                        if (isFirstParam) {
                            targetPath += "?" + paramKey + "=";
                            isFirstParam = false;
                        } else {
                            targetPath += "&" + paramKey + "=";
                        }
                        int valueLen = value.length;
                        for (int i = 0; i < valueLen; i++) {
                            targetPath += value[i] + ",";
                        }
                        targetPath = targetPath.substring(0, targetPath.length() - 1);
                    }

                }
            }
            String returnUrl = targetDomainUrl + targetPath;
            logger.error(returnUrl);
            returnUrl = URLEncoder.encode(returnUrl, "UTF-8");
            redirectLoginUrl = redirectLoginUrl + "&returnurl=" + returnUrl;
            System.out.println(redirectLoginUrl);
            logger.error(redirectLoginUrl);
            response.sendRedirect(redirectLoginUrl);
        } catch (UnsupportedEncodingException e) {
            logger.error("LoginInterceptor.redirectLogin 登录拦截器，跳转登录页面过程中发生UnsupportedEncodingException异常！！！", e);
        } catch (IOException e) {
            logger.error("LoginInterceptor.redirectLogin 登录拦截器，跳转登录页面过程中发生IOException异常！！！", e);
        }

    }

    public void destroy() {

    }

    public void init() {
    }

    /**
     * 获取cookie
     * 
     * @param request
     * @param cookieKey
     * @return
     */
    private String cookieValue(HttpServletRequest request, String cookieKey) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cook : cookies) {
                if (cook.getName().endsWith(cookieKey)) {
                    return cook.getValue();
                }
            }
        }
        return null;
    }


    public String getTargetDomainUrl() {
        return targetDomainUrl;
    }

    public void setTargetDomainUrl(String targetDomainUrl) {
        this.targetDomainUrl = targetDomainUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public String getPinName() {
        return pinName;
    }

    public void setPinName(String pinName) {
        this.pinName = pinName;
    }

    public String getPinCookieName() {
        return pinCookieName;
    }

    public void setPinCookieName(String pinCookieName) {
        this.pinCookieName = pinCookieName;
    }

    public String getKeyCookieName() {
        return keyCookieName;
    }

    public void setKeyCookieName(String keyCookieName) {
        this.keyCookieName = keyCookieName;
    }

    public String getLoginCheckUrl() {
        return loginCheckUrl;
    }

    public void setLoginCheckUrl(String loginCheckUrl) {
        this.loginCheckUrl = loginCheckUrl;
    }

}
