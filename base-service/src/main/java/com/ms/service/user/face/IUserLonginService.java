package com.ms.service.user.face;

import com.ms.domain.user.bo.UserLoginBO;
import com.ms.domain.user.dao.UserLoginDAO;

/**
 * 用户登录服务接口定义
 * @author zhoushanjie
 *
 */
public interface IUserLonginService {
	
	/**
	 * 查询注册用户的全部信息
	 * @param userName
	 * @param status
	 * @return
	 */
	UserLoginBO queryUserRegistryInfo(String userName, Integer status);
	
	/**
	 * 根据用户名和密码，校验用户是否可以登录
	 * @param userName
	 * @param pwd
	 * @return
	 */
	 String validateUserPassRePin(String userName, String pwd);
	
	/**
	 * 用户更新密码
	 * @param userName
	 * @param oldPwd
	 * @param pwd
	 * @return
	 */
	boolean updatePassWord(String userName, String oldPwd, String pwdNew);
	
	/**
	 * 添加用户信息
	 * @param userBO
	 * @return
	 */
	boolean addNewUser(UserLoginBO userBO);
	
	/**
	 * 根据用户关键字凭据(邮箱、手机号、昵称)获取用户pin
	 * @param keyWords
	 * @return
	 */
	String getPinByKeyWord(String keyWord);
	
	/**
	 * 校验用户注册的pin信息是否已经存在
	 * @param recordKey
	 * @return
	 */
	boolean hasPinRecord(String pin);
	
	/**
	 *	校验用户注册的email信息是否已经存在
	 * @param email
	 * @return
	 */
	boolean hasEmailRecord(String email);
	
	/**
	 *	校验用户注册的phone信息是否已经存在
	 * @param phone
	 * @return
	 */
	boolean hasPhoneRecord(Integer phone);
	
	/**
	 *	校验用户注册的nickName信息是否已经存在
	 * @param nickName
	 * @return
	 */
	boolean hasnickNameRecord(String nickName);
	
	/**
	 * 更新用户状态
	 * @return
	 */
	boolean updateUserStatus(String userName, Integer status);
	
}
