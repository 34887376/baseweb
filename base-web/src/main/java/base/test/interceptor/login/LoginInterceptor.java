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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import base.test.base.util.JsonUtil;

import com.ms.cookie.util.CookieUtils;
import com.ms.login.constant.LoginParamConstant;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * @author zhoushanjie
 * 
 */
public class LoginInterceptor implements Interceptor {

    private static final long serialVersionUID = 5058991461480309218L;

    Logger logger = Logger.getLogger(this.getClass());
    
    private CookieUtils cookieUtils;

    public String intercept(ActionInvocation invocation) throws Exception {
        try {
            // 取得请求相关的ActionContext实例
            HttpServletResponse response = ServletActionContext.getResponse();
            HttpServletRequest request = ServletActionContext.getRequest();
            String redirectLoginUrl = LoginParamConstant.getLoginUrl() + "?hello=you";
            //从cookie中获取登录的用户名
            String pin = cookieUtils.getCookieValue(request, LoginParamConstant.getCookieName());
             if (pin == null) {
	             redirectLogin(response, request, redirectLoginUrl);
	             return null;
             }
            String pinDecodeStr = URLDecoder.decode(pin, "UTF-8");
            // 设置pin到请求上下文，和baseaction相对应
            request.setAttribute(LoginParamConstant.getPinContextKey(), pinDecodeStr);
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
            String returnUrl = LoginParamConstant.getDomainUrl() + targetPath;
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

	public CookieUtils getCookieUtils() {
		return cookieUtils;
	}

	public void setCookieUtils(CookieUtils cookieUtils) {
		this.cookieUtils = cookieUtils;
	}

}
