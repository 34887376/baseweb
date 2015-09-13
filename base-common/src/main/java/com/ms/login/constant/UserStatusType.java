package com.ms.login.constant;

/**
 * 用户账号的状态
 * @author zhoushanjie
 *
 */
public enum UserStatusType {

	正常用户(1),封号用户(2),恶意用户(3);
	
	private int userStatus;

	private UserStatusType(int userStatus) {
		this.userStatus = userStatus;
	}

	public int getUserStatus() {
		return userStatus;
	}
	
}
