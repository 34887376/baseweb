package com.ms.dao.user.face;

import com.ms.domain.user.dao.UserLoginDAO;

public interface IUserLoginDAO {
	
	/**
	 * 查询注册用户的全部信息
	 * @param userLoginDAO
	 * @return
	 * @throws Exception
	 */
	UserLoginDAO queryUserRegistryInfo(UserLoginDAO userLoginDAO) throws Exception;
	
	/**
	 * 根据用户名和密码，校验用户是否可以登录
	 * @param userName
	 * @param pwd
	 * @return
	 */
	 String validateUserPassRePin(UserLoginDAO userLoginDAO) throws Exception;
	
	/**
	 * 用户更新密码
	 * @param userName
	 * @param oldPwd
	 * @param pwd
	 * @return
	 */
	boolean updatePassWord(UserLoginDAO userLoginDAO) throws Exception;
	
	/**
	 * 添加用户信息
	 * @param userBO
	 * @return
	 */
	boolean addNewUser(UserLoginDAO userLoginDAO) throws Exception;
	
	/**
	 * 根据用户关键字凭据获取用户pin
	 * @param keyWords
	 * @return
	 */
	String getPinByKeyWord(UserLoginDAO userLoginDAO) throws Exception;
	
	/**
	 * 校验用户注册的pin信息是否已经存在
	 * @param recordKey
	 * @return
	 */
	boolean hasPinRecord(String pin) throws Exception;
	
	/**
	 *	校验用户注册的email信息是否已经存在
	 * @param email
	 * @return
	 */
	boolean hasEmailRecord(String email) throws Exception;
	
	/**
	 *	校验用户注册的phone信息是否已经存在
	 * @param phone
	 * @return
	 */
	boolean hasPhoneRecord(Integer phone) throws Exception;
	
	/**
	 *	校验用户注册的nickName信息是否已经存在
	 * @param nickName
	 * @return
	 */
	boolean hasnickNameRecord(String nickName) throws Exception;
	
	/**
	 * 更新用户状态
	 * @param userLoginDAO
	 * @return
	 * @throws Exception
	 */
	boolean updateUserStatus(UserLoginDAO userLoginDAO) throws Exception;

}
