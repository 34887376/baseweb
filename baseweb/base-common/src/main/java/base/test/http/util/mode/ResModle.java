package base.test.http.util.mode;

import java.util.Map;

/**
 * http响应封装类
 * @author Administrator
 *
 */
public class ResModle extends HttpModle{

	// 响应状态
	private Status status = new Status();
	
	/**
	 * 没有参数的构造函数
	 */
	public ResModle(){
		
	}
	
	/**
	 * 构造函数
	 * @param header 消息头
	 */
	public ResModle(Map<String,String> header){
		this(header,null);
	}
	
	/**
	 * 构造函数
	 * @param body 响应消息体
	 */
	public ResModle(String body){
		this(null,body);
	}
	
	/**
	 * 构造函数
	 * @param header 消息头
	 * @param body 响应消息体
	 */
	public ResModle(Map<String,String> header,String body){
		super(header,body);
	}
	
	/**
	 * 设置状态
	 * @param statusCode 响应状态码
	 * @param statusText 响应状态信息
	 */
	public void setStatus(int statusCode,String statusText){
		status.setStatusCode(statusCode);
		status.setStatusText(statusText);
	}
	
	/**
	 * 获得响应状态码
	 * @return
	 */
	public int getStatusCode(){
		return status.getStatusCode();
	}
	
	/**
	 * 获得响应状态信息
	 * @return
	 */
	public String getStatusText(){
		return status.getStatusText();
	}
	
	/**
	 * 响应状态内部类
	 * @author Administrator
	 *
	 */
	private class Status{
		// 状态码
		private int statusCode;
		// 状态信息
		private String statusText;
		
		public int getStatusCode() {
			return statusCode;
		}
		
		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}
		
		public String getStatusText() {
			return statusText;
		}
		
		public void setStatusText(String statusText) {
			this.statusText = statusText;
		}
		
	}
	
}
