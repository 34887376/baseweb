package com.ms.service.user.face.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import base.test.base.util.GenerateUUID;
import base.test.base.util.JsonUtil;

import com.ms.dao.user.face.IUserLoginDAO;
import com.ms.domain.user.bo.UserLoginBO;
import com.ms.domain.user.dao.UserLoginDAO;
import com.ms.encrypt.util.EnAndDeEncryptUtils;
import com.ms.login.constant.LoginParamConstant;
import com.ms.login.constant.UserLoginType;
import com.ms.login.constant.UserStatusType;
import com.ms.service.user.face.IUserLonginService;

public class UserLonginServiceImpl implements IUserLonginService {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private IUserLoginDAO iUserLoginDAO;
	
	public UserLoginBO queryUserRegistryInfo(String userName, Integer status) {
		UserLoginBO userLoginBO = null;
		try {
			int userType = witchLogin(userName);
			//设置用户登录信息
			UserLoginDAO userLoginDAO = new UserLoginDAO();
			//获取用户名是邮箱还是手机号还是pin；这三者互斥，只能存在其中一个
			if(userType == UserLoginType.邮箱.getLoginType()){
				userLoginDAO.setEmail(userName);
			}else if(userType == UserLoginType.手机号.getLoginType()){
				userLoginDAO.setPhone(Long.parseLong(userName));
			}else if(userType == UserLoginType.用户名.getLoginType()){
				userLoginDAO.setPin(userName);
			}
			userLoginDAO.setStatus(status);
			UserLoginDAO reUserLoginDAO = iUserLoginDAO.queryUserRegistryInfo(userLoginDAO);
			if(reUserLoginDAO!=null){
				userLoginBO = new UserLoginBO();
				userLoginBO.setEmail(reUserLoginDAO.getEmail());
				userLoginBO.setId(reUserLoginDAO.getId());
				userLoginBO.setNickName(reUserLoginDAO.getNickName());
				userLoginBO.setPhone(reUserLoginDAO.getPhone());
				userLoginBO.setPin(reUserLoginDAO.getPin());
				userLoginBO.setStatus(reUserLoginDAO.getStatus());
				userLoginBO.setUuid(reUserLoginDAO.getUuid());
			}
		} catch (Exception e) {
			logger.error("UserLonginServiceImpl.validateUserPassRePin方法根据用户名、密码校验用户合法性发生异常！！！入参={userName="+userName+";  status="+status+"}", e);
			return null;
		}
		return userLoginBO;
	}

	public String validateUserPassRePin(String userName, String pwd) {
		try {
			int userType = witchLogin(userName);
			//设置用户登录信息
			UserLoginDAO userLoginDAO = new UserLoginDAO();
			userLoginDAO.setPwd(EnAndDeEncryptUtils.encryptMD5WithKey(pwd, LoginParamConstant.getMd5Seed()));
			//获取用户名是邮箱还是手机号还是pin；这三者互斥，只能存在其中一个
			if(userType == UserLoginType.邮箱.getLoginType()){
				userLoginDAO.setEmail(userName);
			}else if(userType == UserLoginType.手机号.getLoginType()){
				userLoginDAO.setPhone(Long.parseLong(userName));
			}else if(userType == UserLoginType.用户名.getLoginType()){
				userLoginDAO.setPin(userName);
			}
			String pin = iUserLoginDAO.validateUserPassRePin(userLoginDAO);
			if(StringUtils.isNotBlank(pin)){
				return pin;
			}
		} catch (Exception e) {
			logger.error("UserLonginServiceImpl.validateUserPassRePin方法根据用户名、密码校验用户合法性发生异常！！！入参={userName="+userName+";  pwd="+pwd+"}", e);
			return null;
		}
		return null;
	}

	public boolean updatePassWord(String userName, String oldPwd, String pwdNew) {
		try {
			
			UserLoginDAO userLoginDAO = new UserLoginDAO();
			userLoginDAO.setPwd(EnAndDeEncryptUtils.encryptMD5WithKey(oldPwd, LoginParamConstant.getMd5Seed()));
			//获取用户名是邮箱还是手机号还是pin；这三者互斥，只能存在其中一个
			int userType = witchLogin(userName);
			if(userType == UserLoginType.邮箱.getLoginType()){
				userLoginDAO.setEmail(userName);
			}else if(userType == UserLoginType.手机号.getLoginType()){
				userLoginDAO.setPhone(Long.parseLong(userName));
			}else if(userType == UserLoginType.用户名.getLoginType()){
				userLoginDAO.setPin(userName);
			}
			userLoginDAO.setPwdNew(EnAndDeEncryptUtils.encryptMD5WithKey(pwdNew, LoginParamConstant.getMd5Seed()));
			boolean hasOk = iUserLoginDAO.updatePassWord(userLoginDAO);
			if(hasOk){
				return true;
			}
		} catch (Exception e) {
			logger.error("UserLonginServiceImpl.updatePassWord方法根据用户名、密码校验更新用户密码时发生异常！！！入参={userName="+userName+";  " +
					"oldPwd="+EnAndDeEncryptUtils.encryptMD5WithKey(oldPwd, LoginParamConstant.getMd5Seed())+";  pwdNew="+EnAndDeEncryptUtils.encryptMD5WithKey(pwdNew, LoginParamConstant.getMd5Seed())+" }", e);
			return false;
		}
		return false;
	}

	public boolean addNewUser(UserLoginBO userLoginBO) {
		try {
			UserLoginDAO userLoginDAO = new UserLoginDAO();
			userLoginDAO.setNickName(userLoginBO.getNickName());
			userLoginDAO.setStatus(UserStatusType.正常用户.getUserStatus());
			userLoginDAO.setUuid(GenerateUUID.getOnlyCharUUID());
			userLoginDAO.setEmail(userLoginBO.getEmail());
			userLoginDAO.setPin(userLoginBO.getPin());
			userLoginDAO.setPhone(userLoginBO.getPhone());
			userLoginDAO.setPwd(EnAndDeEncryptUtils.encryptMD5WithKey(userLoginBO.getPwd(), LoginParamConstant.getMd5Seed()));
			boolean hasOk = iUserLoginDAO.addNewUser(userLoginDAO);
			if(hasOk){
				return true;
			}
		} catch (Exception e) {
			logger.error("UserLonginServiceImpl.addNewUser方法添加用户信息时发生异常！！！入参={userLoginBO="+JsonUtil.toJson(userLoginBO)+"}", e);
			return false;
		}
		return false;
	}
	
	public String getPinByKeyWord(String keyWord) {
		try {
			UserLoginDAO userLoginDAO = new UserLoginDAO();
			int userType = witchLogin(keyWord);
			//获取用户名是邮箱还是手机号还是pin；这三者互斥，只能存在其中一个
			if(userType == UserLoginType.邮箱.getLoginType()){
				userLoginDAO.setEmail(keyWord);
			}else if(userType == UserLoginType.手机号.getLoginType()){
				userLoginDAO.setPhone(Long.parseLong(keyWord));
			}else{
				userLoginDAO.setNickName(keyWord);
			}
			String pin = iUserLoginDAO.getPinByKeyWord(userLoginDAO);
			if(StringUtils.isNotBlank(pin)){
				return pin;
			}
		} catch (Exception e) {
			logger.error("UserLonginServiceImpl.getPinByKeyWord方法根据用户名、密码校验用户合法性发生异常！！！入参={keyWord="+keyWord+"}", e);
			return null;
		}
		return null;
	}
	

	public boolean hasPinRecord(String pin) {
		try {
			boolean hasOk = iUserLoginDAO.hasPinRecord(pin);
			if(hasOk){
				return true;
			}
		} catch (Exception e) {
			logger.error("UserLonginServiceImpl.hasPinRecord方法查询用户注册名称是否存在时发生异常！！！入参={pin="+pin+"}", e);
			return false;
		}
		return false;
	}

	public boolean hasEmailRecord(String email) {
		try {
			boolean hasOk = iUserLoginDAO.hasEmailRecord(email);
			if(hasOk){
				return true;
			}
		} catch (Exception e) {
			logger.error("UserLonginServiceImpl.hasEmailRecord方法查询用户注册邮箱是否存在时发生异常！！！入参={email="+email+"}", e);
			return false;
		}
		return false;
	}

	public boolean hasPhoneRecord(Long phone) {
		try {
			boolean hasOk = iUserLoginDAO.hasPhoneRecord(phone);
			if(hasOk){
				return true;
			}
		} catch (Exception e) {
			logger.error("UserLonginServiceImpl.hasPhoneRecord方法查询用户注册手机号是否存在时发生异常！！！入参={phone="+phone+"}", e);
			return false;
		}
		return false;
	}

	public boolean hasnickNameRecord(String nickName) {
		try {
			boolean hasOk = iUserLoginDAO.hasnickNameRecord(nickName);
			if(hasOk){
				return true;
			}
		} catch (Exception e) {
			logger.error("UserLonginServiceImpl.hasnickNameRecord方法查询用户注册昵称是否存在时发生异常！！！入参={nickName="+nickName+"}", e);
			return false;
		}
		return false;
	}
	

	public boolean updateUserStatus(String userName, Integer status) {
		try {
			UserLoginDAO userLoginDAO = new UserLoginDAO();
			int userType = witchLogin(userName);
			if(userType == UserLoginType.邮箱.getLoginType()){
				userLoginDAO.setEmail(userName);
			}else if(userType == UserLoginType.手机号.getLoginType()){
				userLoginDAO.setPhone(Long.parseLong(userName));
			}else if(userType == UserLoginType.用户名.getLoginType()){
				userLoginDAO.setPin(userName);
			}
			userLoginDAO.setStatus(status);
			boolean hasOk = iUserLoginDAO.updateUserStatus(userLoginDAO);
			if(hasOk){
				return true;
			}
		} catch (Exception e) {
			logger.error("UserLonginServiceImpl.updateUserStatus方法根据用户名更新用户状态时发生异常！！！入参={userName="+userName+";  " +
					"status="+status+" }", e);
			return false;
		}
		return false;
	}
	
	/**
	 * 用户使用的格式
	 * 1 邮箱
	 * 2 手机号
	 * 3 用户名
	 * @param userName
	 * @return
	 */
	private int witchLogin(String userName){
		//邮箱格式正则表达式
		String em = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
		//手机号格式正则表达式
		String ph = "^[1][3578]\\d{9}$";
		if(userName.matches(em)){//邮箱登录
			return UserLoginType.邮箱.getLoginType();
		} else if(userName.matches(ph)){//手机号登录
			return UserLoginType.手机号.getLoginType();
		}else{//就是用户名登录
			return UserLoginType.用户名.getLoginType();
		}
	}

	public void setiUserLoginDAO(IUserLoginDAO iUserLoginDAO) {
		this.iUserLoginDAO = iUserLoginDAO;
	}

}
