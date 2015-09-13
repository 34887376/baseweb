package base.test.http.util.constant;

import java.util.Properties;

import base.test.base.util.PropertiesUtil;


public class HttpClientPropertiesHolder {
	
	private static final String PROPERTIES_NAME = "httpclient.properties";
	
	private static Properties properties;
	
	static {
		properties = PropertiesUtil.getInstanceByFileName(PROPERTIES_NAME);
	}
	
	/**
	 * socket缓冲区
	 * @return
	 */
	public static int getSocketBufferSize(){
		int size = 512 * 1024;
			
		try{
			size = Integer.parseInt(properties.getProperty("socket.buffer.size")) * 1024;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return size;
	}
	
	/**
	 * 连接超时时间(单位毫秒)
	 * @return
	 */
	public static int getConnectionTimeout(){
		int time = 5000;
			
		try{
			time = Integer.parseInt(properties.getProperty("connection.timeout"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return time;
	}
	
	/**
	 * 读数据超时时间(单位毫秒)
	 * @return
	 */
	public static int getSoTimeout(){
		int time = 5000;
			
		try{
			time = Integer.parseInt(properties.getProperty("so.timeout"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return time;
	}
	
	/**
	 * 连接管理者超时时间(单位毫秒)
	 * @return
	 */
	public static long getConnectionManagerTimeout(){
		long time = 5000;
			
		try{
			time = Long.parseLong(properties.getProperty("connection.manager.timeout"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return time;
	}
	
	/**
	 * 是否禁用重连,默认禁用
	 * @return
	 */
	public static boolean isEnableConnectionRepeat(){
		boolean enable = true;
			
		try{
			enable = Boolean.parseBoolean(properties.getProperty("connection.repeat.enable"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return enable;
	}
	
	/**
	 * 重连次数 ,默认设置为0(要考虑重试写是否会有幂等性问题，重试读是否会增加服务提供方的压力)
	 * @return
	 */
	public static int getConnectionRepeatCount(){
		int count = 0;
			
		try{
			count = Integer.parseInt(properties.getProperty("connection.repeat.count"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return count;
	}
	
	/**
	 * 是否使用线程安全的连接池,在并发情况下，如果不使用会出现问题，所以一般都是使用，慎重修改
	 * @return
	 */
	public static boolean isUseMultiThreadConnectionPool(){
		boolean use = true;
			
		try{
			use = Boolean.parseBoolean(properties.getProperty("connection.pool.multithread.use"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return use;
	}
	
	/**
	 * 安全连接池中每个host的最大连接数
	 * @return
	 */
	public static int getMultiThreadConnectionPoolMaxHost(){
		int size = 20;
			
		try{
			size = Integer.parseInt(properties.getProperty("connection.pool.multithread.max.host"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return size;
	}
	
	/**
	 * 安全连接池中总共的最大连接数
	 * @return
	 */
	public static int getMultiThreadConnectionPoolMaxTotal(){
		int size = 40;
			
		try{
			size = Integer.parseInt(properties.getProperty("connection.pool.multithread.max.total"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return size;
	}
	
}
