package com.ms.dao.user.face.impl;

import org.apache.log4j.Logger;

import com.ms.dao.base.dao.BaseMysqlDAO;
import com.ms.dao.user.face.IUserLoginDAO;
import com.ms.domain.user.dao.UserLoginDAO;

public class UserLoginDAOImpl extends BaseMysqlDAO implements IUserLoginDAO {

	private Logger logger = Logger.getLogger(this.getClass());
	
	public UserLoginDAO queryUserRegistryInfo(UserLoginDAO userLoginDAO)
			throws Exception {
		if(userLoginDAO==null){
			return new UserLoginDAO();
		}
		UserLoginDAO allInfo = (UserLoginDAO)this.queryForObject("UserLogin.queryUserRegistryInfo", userLoginDAO);
		return allInfo;
	}
	
	public String validateUserPassRePin(UserLoginDAO userLoginDAO) throws Exception {
		if(userLoginDAO==null){
			return null;
		}
		String pin = (String)this.queryForObject("UserLogin.validateUserPassRePin", userLoginDAO);
		return pin;
	}

	public boolean updatePassWord(UserLoginDAO userLoginDAO) throws Exception{
		if(userLoginDAO==null){
			return false;
		}
		int affectRowNum = this.update("UserLogin.updateUserPass", userLoginDAO);
		if(affectRowNum>0){
			return true;
		}
		return false;
	}

	public boolean addNewUser(UserLoginDAO userLoginDAO) throws Exception{
		Object affectRowNum = this.insert("UserLogin.insertUserRegistryInfo", userLoginDAO);
		if(affectRowNum==null){
			return false;
		}
		if((Long)affectRowNum>0){
			return true;
		}
		return false;
	}

	public String getPinByKeyWord(UserLoginDAO userLoginDAO) throws Exception{
		String pin = (String)this.queryForObject("UserLogin.queryPinByKeyWord", userLoginDAO);
		return pin;
	}

	public boolean hasPinRecord(String pin) throws Exception {
		if(pin==null){
			return false;
		}
		Long recordNum = (Long)this.queryForObject("UserLogin.hasPinRecord", pin);
		if(recordNum!=null && recordNum>0){
			return true;
		}
		return false;
	}

	public boolean hasEmailRecord(String email) throws Exception {
		if(email==null){
			return false;
		}
		Long recordNum = (Long)this.queryForObject("UserLogin.hasEmailRecord", email);
		if(recordNum!=null && recordNum>0){
			return true;
		}
		return false;
	}

	public boolean hasPhoneRecord(Long phone) throws Exception {
		if(phone==null){
			return false;
		}
		Long recordNum = (Long)this.queryForObject("UserLogin.hasPhoneRecord", phone);
		if(recordNum!=null && recordNum>0){
			return true;
		}
		return false;
	}

	public boolean hasnickNameRecord(String nickName) throws Exception {
		if(nickName==null){
			return false;
		}
		Long recordNum = (Long)this.queryForObject("UserLogin.hasNicknameRecord", nickName);
		if(recordNum!=null && recordNum>0){
			return true;
		}
		return false;
	}

	public boolean updateUserStatus(UserLoginDAO userLoginDAO) throws Exception {
		if(userLoginDAO==null){
			return false;
		}
		int affectRowNum = this.update("UserLogin.updateUserStatus", userLoginDAO);
		if(affectRowNum>0){
			return true;
		}
		return false;
	}

}
