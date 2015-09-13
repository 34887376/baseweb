package com.ms.domain.user.bo;

import java.io.Serializable;

public class UserLoginBO implements Serializable {

	private static final long serialVersionUID = 5528544355879959669L;

	/**
	 * 主键id
	 * 
	 */
	private Long id;
	
	/**
	 * 给用户分配的唯一编码
	 */
	private String uuid;
	
	/**
	 * 用户名，限制长度30位，用户唯一标识
	 */
	private String pin;
	
	/**
	 * 用户昵称
	 */
	private String nickName;
	
	/**
	 * 用户登录密码
	 */
	private String pwd;
	
	/**
	 * 更新密码时的新密码
	 */
	private String pwdNew;
	
	/**
	 * 用户注册、认证手机号
	 */
	private Integer phone;
	
	/**
	 * 用户邮件地址
	 */
	private String email;
	
	/**
	 * 用户状态 1 正常，2 封号 3 恶意账号
	 */
	private Integer status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Integer getPhone() {
		return phone;
	}

	public void setPhone(Integer phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPwdNew() {
		return pwdNew;
	}

	public void setPwdNew(String pwdNew) {
		this.pwdNew = pwdNew;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
