package base.test.base.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

public class BaseAction extends ActionSupport implements ServletRequestAware, ServletResponseAware, Serializable {

    private Logger logger = Logger.getLogger(this.getClass());

    private static final long serialVersionUID = 2431965183453943398L;

    public HttpServletRequest request;

    public HttpServletResponse response;

    public static final String REQUEST_PIN_KEY = "user_pin";

    // 登录后跳转的应用域名
    private static String targetDomainUrl;

    // 登录时要跳转的url
    private static String loginUrl;

    // 登录时申请的appId
    private static int appId;

    // 设置到请求上下文的用户pin的key
    private static String pinName;

    /**
     * 页面缓存清除方法
     */
    public void noCache() {
        response.setHeader("Pragma", "No-Cache");
        response.setHeader("Cache-Control", "No-Cache");
        response.setDateHeader("Expires", 0);
    }

    /**
     * ajax调用此方法返回数据
     * 
     * @param str
     *            要返回的数据字符串
     */
    public void print(String str) {
        PrintWriter writer = null;
        try {
            response.setContentType("text/html; charset=utf-8");
            writer = response.getWriter();
            writer.print(str);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * 获取前台登录用户
     * 
     * @return 登陆的用户名
     */
    public String getPin() {
        String Pin = (String) request.getAttribute(pinName);
        System.out.println(Pin);
        return Pin;
    }

    /**
     * 跳转到登录页面
     * 
     * @throws IOException
     */
    public void redrictLogin() throws IOException {

        String redirectLoginUrl = loginUrl + "?appid=" + appId;
        try {
            String targetPath = request.getServletPath();
            if (StringUtils.isNotBlank(request.getQueryString())) {
                targetPath = targetPath + "?" + request.getQueryString();
            }
            String returnUrl = targetDomainUrl + targetPath;
            returnUrl = URLEncoder.encode(returnUrl, "UTF-8");
            redirectLoginUrl += "&returnurl=" + returnUrl;
            response.sendRedirect(redirectLoginUrl);
            return;
        } catch (UnsupportedEncodingException e) {
            response.sendRedirect(targetDomainUrl);
            return;
        } catch (IOException e) {
            response.sendRedirect(targetDomainUrl);
            return;
        }

    }

    public void putParamToVm(Map<String, Object> parmKeyValue) {
        ValueStack context = ActionContext.getContext().getValueStack();
        for (Entry<String, Object> entry : parmKeyValue.entrySet()) {
            context.set(entry.getKey(), entry.getValue());
            // request.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    public String getIpAddr() {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getPinName() {
        return pinName;
    }

    public void setPinName(String pinName) {
        this.pinName = pinName;
    }

    // public String getTargetDomainUrl() {
    // return targetDomainUrl;
    // }

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

}
