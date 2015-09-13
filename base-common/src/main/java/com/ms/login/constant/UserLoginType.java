package com.ms.login.constant;

/**
 * 用户账号登录的类型
 * @author zhoushanjie
 *
 */
public enum UserLoginType {

	邮箱(1),手机号(2),用户名(3);
	
	private int loginType;
	
	private UserLoginType(int loginType) {
		this.loginType = loginType;
	}

	public int getLoginType() {
		return loginType;
	}

}
