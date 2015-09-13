package com.ms.domain.action.base;

import java.io.Serializable;

public class BaseActionResult implements Serializable {

	private static final long serialVersionUID = -1812092007248094665L;

	//处理是否成功
	private boolean success;
	
	//返回的信息
	private String msg;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
