package com.ms.dao.user.face.impl;

import java.util.HashMap;
import java.util.Map;

import com.ms.dao.base.dao.BaseMysqlDAO;
import com.ms.dao.user.face.IUserLoginDAO;
import com.ms.domain.user.dao.UserLoginDAO;

public class UserLoginDAOImpl extends BaseMysqlDAO implements IUserLoginDAO {

	
	public UserLoginDAO queryUserRegistryInfo(UserLoginDAO userLoginDAO)
			throws Exception {
		UserLoginDAO allInfo = (UserLoginDAO)this.queryForObject("UserLogin.queryUserRegistryInfo", userLoginDAO);
		return allInfo;
	}
	
	public String validateUserPassRePin(UserLoginDAO userLoginDAO) throws Exception {
		String pin = (String)this.queryForObject("UserLogin.validateUserPassRePin", userLoginDAO);
		return pin;
	}

	public boolean updatePassWord(UserLoginDAO userLoginDAO) throws Exception{
		Integer affectRowNum = (Integer)this.update("UserLogin.updateUserPass", userLoginDAO);
		if(affectRowNum!=null && affectRowNum>0){
			return true;
		}
		return false;
	}

	public boolean addNewUser(UserLoginDAO userLoginDAO) throws Exception{
		Integer affectRowNum = (Integer)this.insert("UserLogin.insertUserRegistryInfo", userLoginDAO);
		if(affectRowNum!=null && affectRowNum>0){
			return true;
		}
		return false;
	}

	public String getPinByKeyWord(UserLoginDAO userLoginDAO) throws Exception{
		String pin = (String)this.queryForObject("UserLogin.queryPinByKeyWord", userLoginDAO);
		return pin;
	}

	public boolean hasPinRecord(String pin) throws Exception {
		Integer recordNum = (Integer)this.queryForObject("UserLogin.hasPinRecord", pin);
		if(recordNum!=null && recordNum>0){
			return true;
		}
		return false;
	}

	public boolean hasEmailRecord(String email) throws Exception {
		Integer recordNum = (Integer)this.queryForObject("UserLogin.hasEmailRecord", email);
		if(recordNum!=null && recordNum>0){
			return true;
		}
		return false;
	}

	public boolean hasPhoneRecord(Integer phone) throws Exception {
		Integer recordNum = (Integer)this.queryForObject("UserLogin.hasPhoneRecord", phone);
		if(recordNum!=null && recordNum>0){
			return true;
		}
		return false;
	}

	public boolean hasnickNameRecord(String nickName) throws Exception {
		Integer recordNum = (Integer)this.queryForObject("UserLogin.hasNicknameRecord", nickName);
		if(recordNum!=null && recordNum>0){
			return true;
		}
		return false;
	}

	public boolean updateUserStatus(UserLoginDAO userLoginDAO) throws Exception {
		Integer affectRowNum = (Integer)this.update("UserLogin.updateUserStatus", userLoginDAO);
		if(affectRowNum!=null && affectRowNum>0){
			return true;
		}
		return false;
	}

}
