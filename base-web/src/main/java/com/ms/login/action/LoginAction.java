package com.ms.login.action;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import base.test.base.action.BaseAction;
import base.test.base.util.JsonUtil;
import base.test.base.util.RegxUtil;

import com.ms.domain.action.base.BaseActionResult;
import com.ms.domain.action.login.LoginResult;
import com.ms.domain.user.bo.UserLoginBO;
import com.ms.login.constant.LoginParamConstant;
import com.ms.service.user.face.IUserLonginService;

public class LoginAction extends BaseAction{
	
	private static final long serialVersionUID = 1900513687097567642L;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	//用户登录名
	private String userName;
	
	 // 用户名，限制长度30位，用户唯一标识
	private String pin;
	
	 //用户昵称
	private String nickName;
	
	 //用户登录密码
	private String pwd;
	
	//第二次用户输入的密码
	private String repeatPwd;
	
	 //更新密码时的新密码
	private String pwdNew;
	
	 //更新密码时的重复输入的新密码
	private String repeatePwdNew;
	
	 // 用户注册、认证手机号
	private Long phone;
	
	 //用户邮件地址
	private String email;
	
	//目标链接
	private String aimUrl;
	
	//用户登录服务
	private IUserLonginService userLonginService;
	
	
	public String goLogin(){
		try {
			if(StringUtils.isBlank(aimUrl)){
				aimUrl=LoginParamConstant.getIndexPageUrl();
			}
			//只能在/login的路径上下文中跳转，无法跳到别的路径下
//			request.getRequestDispatcher(aimUrl).forward(request, response);
//			response.sendRedirect(aimUrl);
			return "loginHtml";
		} catch (Exception e) {
			logger.error("LoginAction.goLogin用户登录异常！！！无参数", e);
			return EXCEPTION;
		} 
	}
	/**
	 * 登录方法
	 * @return
	 */
	public String login(){
		try {
			LoginResult loginResult =  new LoginResult();
			if(StringUtils.isBlank(userName) || StringUtils.isBlank(pwd)){
				loginResult.setMsg("用户名或者密码为空！！！");
				loginResult.setSuccess(false);
				print(JsonUtil.toJson(loginResult));
				return "loginReturn";
			}
//			String pin = userLonginService.validateUserPassRePin(userName,pwd);
//			if(StringUtils.isNotBlank(pin)){
//				setCookie(LoginParamConstant.getCookieName(),pin);
//			}
			if(StringUtils.isBlank(aimUrl)){
				aimUrl=LoginParamConstant.getIndexPageUrl();
			}
			loginResult.setAimUrl(aimUrl);
			loginResult.setMsg("处理成功");
			loginResult.setSuccess(true);
			print(JsonUtil.toJson(loginResult));
			return "loginReturn";
		} catch (Exception e) {
			logger.error("LoginAction.login用户登录异常！！！上下文参数={userName="+userName+"}", e);
			return EXCEPTION;
		} 
	}
	
	/**
	 * 注销登录
	 * @return
	 */
	public String logout(){
		try {
			String cookiePin = getCookieValue(LoginParamConstant.getCookieName());
			if(StringUtils.isNotBlank(cookiePin)){
				clearAllCookie();
			}else{
				throw new RuntimeException("用户注销时发生异常，未获取用户的登录状态。上下文参数={userName="+userName+"，    cookiePin="+cookiePin+";}");
			}
			redrictLogin();
			return null;
		} catch (IOException e) {
			logger.error("LoginAction.logout用户注销异常！！！上下文参数={userName="+userName+"}", e);
			return EXCEPTION;
		}
	}
	
	/**
	 * 修改用户密码
	 * @return
	 */
	public String changePass(){
		try{
			String errorInfo = validChangePwdParam(getPin());
			if(StringUtils.isNotBlank(errorInfo)){
				print(errorInfo);
				return "changePwdParaError";
			}
			
			boolean hasOk = userLonginService.updatePassWord(userName,pwd,pwdNew);
			if(hasOk){
				return "changePwdSucess";
			}
			
		}catch(Exception e){
			logger.error("LoginAction.registry用户注册异常！！！上下文参数={pin="+pin+"；   email="+email+"；   nickName="+nickName+"；   phone="+phone+"}", e);
			return EXCEPTION;
		}
		return "changePwdFail";
	}
	
	/**
	 * 用户注册
	 * @return
	 */
	public String registry(){
		BaseActionResult baseResult =  new BaseActionResult();
		try{
			String errorInfo =validateRegistryUserInfo();
			if(StringUtils.isNotBlank(errorInfo)){
				baseResult.setSuccess(false);
				baseResult.setMsg(errorInfo);
				print(JsonUtil.toJson(baseResult));
				return "registry";
			}
			
			UserLoginBO userLoginBO = new UserLoginBO();
			userLoginBO.setEmail(email);
			userLoginBO.setNickName(nickName);
			userLoginBO.setPhone(phone);
			userLoginBO.setPin(pin);
			userLoginBO.setPwd(pwd);
			boolean hasOk = userLonginService.addNewUser(userLoginBO);
			if(hasOk){
				baseResult.setSuccess(true);
				print(JsonUtil.toJson(baseResult));
				return "registry";
			}
		}catch(Exception e){
			logger.error("LoginAction.registry用户注册异常！！！上下文参数={pin="+pin+"；   email="+email+"；   nickName="+nickName+"；   phone="+phone+"}", e);
			return EXCEPTION;
		}
		baseResult.setSuccess(false);
		baseResult.setMsg("网络异常，请稍后再试！！！");
		print(JsonUtil.toJson(baseResult));
		return "registry";
	}
	
	/**
	 * 校验用户pin是否可以注册
	 * @return
	 */
	public String checkHasPin(){
		
		String pinErrorInof = validatePin(pin);
		if(StringUtils.isNotBlank(pinErrorInof)){
			print(pinErrorInof);
			return "checkFail";
		}
		return "checkSuccess";
	}
	
	/**
	 * 校验用户电话是否可以注册
	 * @return
	 */
	public String checkHasPhone(){
		
		String phoneErrorInof = validatePhone(phone);
		if(StringUtils.isNotBlank(phoneErrorInof)){
			print(phoneErrorInof);
			return "checkFail";
		}
		return "checkSuccess";
	}
	
	/**
	 * 校验用户邮箱是否可以注册
	 * @return
	 */
	public String checkHasEmail(){
		
		String emailErrorInof = validateEmail(email);
		if(StringUtils.isNotBlank(emailErrorInof)){
			print(emailErrorInof);
			return "checkFail";
		}
		return "checkSuccess";
	}
	
	
	/**
	 * 校验用户注册的账号信息
	 * @return
	 */
	private String validateRegistryUserInfo(){
		
		String pinErrorInof = validatePin(pin);
		if(StringUtils.isNotBlank(pinErrorInof)){
			return pinErrorInof;
		}
		
		String emailErrorInof = validateEmail(email);
		if(StringUtils.isNotBlank(emailErrorInof)){
			return emailErrorInof;
		}
		
		String phoneErrorInof = validatePhone(phone);
		if(StringUtils.isNotBlank(phoneErrorInof)){
			return phoneErrorInof;
		}
		
		String pwdErrorInof = validatePwd(pwd,repeatPwd);
		if(StringUtils.isNotBlank(pwdErrorInof)){
			return pwdErrorInof;
		}

		return null;
	}
	
	/**
	 * 用户修改密码参数校验
	 * @param pin
	 * @return
	 */
	private String validChangePwdParam(String pin){
		
		if(StringUtils.isBlank(pwd)){
			print("旧密码为空！！！");
			return "paramError";
		}
		
		if(StringUtils.isBlank(pwdNew)){
			return "新密码不能为空！！！";
		}
		
		String repetPin = userLonginService.validateUserPassRePin(pin,pwd);
		if(StringUtils.isBlank(repetPin)){
			return "旧密码输入错误！！！";
		}
		
		String pwdErrorInof = validatePwd(pwdNew,repeatePwdNew);
		if(StringUtils.isNotBlank(pwdErrorInof)){
			return pwdErrorInof;
		}
		
		return null;
	}
	
	private String validatePin(String pin){
		if(StringUtils.isBlank(pin)){
			return "用户名不能为空！！！";
		}else{
			if(!RegxUtil.isValidPin(pin)){
				return "用户名地址格式错误！！！";
			}
			if(userLonginService.hasPinRecord(pin)){
				return "该用户名已经被使用，请更换！！！";
			}
		}
		return null;
	}
	
	private String validateEmail(String email){
		if(StringUtils.isBlank(email)){
			return "注册邮箱地址不能为空！！！";
		}else{
			if(!RegxUtil.isEmail(email)){
				return "注册邮箱地址格式错误！！！";
			}
			if(userLonginService.hasEmailRecord(email)){
				return "该邮箱已经被注册过，请更换！！！";
			}
		}
		return null;
	}
	
	private String validatePhone(Long phone){
		if(phone==null){
			return "注册手机号码不能为空！！！";
		}else{
			if(!RegxUtil.isPhone(String.valueOf(phone))){
				return "注册手机号格式错误！！！";
			}
			if(userLonginService.hasPhoneRecord(phone)){
				return "该手机号已经被注册过，请更换！！！";
			}
		}
		return null;
	}
	
	private String validatePwd(String pwd, String repetePwd){
		if(StringUtils.isBlank(pwd)){
			return "密码不能为空！！！";
		}
		if(StringUtils.isBlank(repeatPwd)){
			return "确认密码不能为空！！！";
		}
		if(!pwd.equals(repeatPwd)){
			return "两次输入的密码不一致！！！";
		}
		return null;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getRepeatPwd() {
		return repeatPwd;
	}

	public void setRepeatPwd(String repeatPwd) {
		this.repeatPwd = repeatPwd;
	}

	public String getPwdNew() {
		return pwdNew;
	}

	public void setPwdNew(String pwdNew) {
		this.pwdNew = pwdNew;
	}

	public String getRepeatePwdNew() {
		return repeatePwdNew;
	}

	public void setRepeatePwdNew(String repeatePwdNew) {
		this.repeatePwdNew = repeatePwdNew;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAimUrl() {
		return aimUrl;
	}

	public void setAimUrl(String aimUrl) {
		this.aimUrl = aimUrl;
	}

	public void setUserLonginService(IUserLonginService userLonginService) {
		this.userLonginService = userLonginService;
	}
	public Long getPhone() {
		return phone;
	}
	public void setPhone(Long phone) {
		this.phone = phone;
	}

}
