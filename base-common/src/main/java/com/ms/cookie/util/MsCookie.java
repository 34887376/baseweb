package com.ms.cookie.util;

import org.apache.commons.lang.StringUtils;

import com.ms.encrypt.util.EnAndDeEncryptUtils;

import javax.servlet.http.Cookie;

public class MsCookie {

	/**
	 * cookie名称
	 */
	private String name;
	
	/**
	 * cookie的domain
	 */
	private String domain;
	
	/**
	 * cookie写入的路径
	 */
	private String path;
	
	/**
	 * cookie的过期时间
	 */
	private int expiry;
	
	/**
	 * cookie值加密时的key
	 * 
	 */
	private String key;
	
	/**
	 * 是否需要加密
	 * 
	 */
	private boolean encrypt;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getExpiry() {
		return expiry;
	}

	public void setExpiry(int expiry) {
		this.expiry = expiry;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isEncrypt() {
		return encrypt;
	}

	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}

	/**
	 * 创建一个新的cookie
	 * @param value
	 * @return
	 */
	public Cookie newCookie(String value) {
		String newValue;
		Cookie cookie = null;
		try {
			if (!StringUtils.isEmpty(value)) {
				newValue = isEncrypt() ? EnAndDeEncryptUtils.encryptDESWithKey(
						value, getKey()) : value;
			} else {
				newValue = value;
			}
			cookie = new Cookie(name, newValue);
			if (!StringUtils.isBlank(domain)) {
				cookie.setDomain(domain);
			}
			if (!StringUtils.isBlank(path)) {
				cookie.setPath(path);
			}
			if (expiry > 0) {
				cookie.setMaxAge(expiry);
			}
		} catch (Exception e) {
			throw new RuntimeException("加密cookie过程中发生异常！！！", e);
		}
		return cookie;
	}

	/**
	 * 获取cookie的值
	 * @param value
	 * @return
	 */
	public String getValue(String value) {
		try {
			if (!StringUtils.isEmpty(value)) {
				return isEncrypt() ? EnAndDeEncryptUtils.decryptDESWithKey(
						value, getKey()) : value;
			} else {
				return value;
			}
		} catch (Exception e) {
			throw new RuntimeException("解密cookie过程中发生异常！！！", e);
		}
	}

}
