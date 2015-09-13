package com.ms.login.constant;

/**
 * 登录方法的一些常量/配置参数设置
 * @author zhoushanjie
 *
 */
public class LoginParamConstant {
	
    //主站域名url
    private static String domainUrl;
	
	//登录记录的cookie
	private static String cookieName;

	//md5加密的key
	private static String md5Seed;
	
	//首页链接地址
	private static String indexPageUrl;
	
	//请求上下文中的pin的key
	private static String pinContextKey;
	
	//登录页面链接
	private static String loginUrl;
	
	public static String getCookieName() {
		return cookieName;
	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	public static String getMd5Seed() {
		return md5Seed;
	}

	public void setMd5Seed(String md5Seed) {
		this.md5Seed = md5Seed;
	}

	public static String getIndexPageUrl() {
		return indexPageUrl;
	}

	public void setIndexPageUrl(String indexPageUrl) {
		this.indexPageUrl = indexPageUrl;
	}

	public static String getPinContextKey() {
		return pinContextKey;
	}

	public void setPinContextKey(String pinContextKey) {
		this.pinContextKey = pinContextKey;
	}

	public static String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public static String getDomainUrl() {
		return domainUrl;
	}

	public void setDomainUrl(String domainUrl) {
		this.domainUrl = domainUrl;
	}
}
