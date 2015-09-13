package com.ms.domain.action.login;

import com.ms.domain.action.base.BaseActionResult;

public class LoginResult extends BaseActionResult {

	private static final long serialVersionUID = 500950927581607012L;
	
	//返回需要跳转的链接
	private String aimUrl;

	public String getAimUrl() {
		return aimUrl;
	}

	public void setAimUrl(String aimUrl) {
		this.aimUrl = aimUrl;
	}
}
