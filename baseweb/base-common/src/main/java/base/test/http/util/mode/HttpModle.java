package base.test.http.util.mode;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 请求和响应的父类
 * @author Administrator
 *
 */
public class HttpModle {
	
	// 消息头
	private Map<String,String> msgHeader = new HashMap<String,String>();
	
	// 消息体
	private String body;
	
	/**
	 * 没有参数的构造函数
	 */
	public HttpModle(){
		
	}

	/**
	 * 构造函数
	 * @param msgHeader 消息头
	 */
	public HttpModle(Map<String,String> msgHeader){
		this.msgHeader = msgHeader;
	}
	
	/**
	 * 构造函数
	 * @param msgHeader 消息头
	 * @param body 消息体
	 */
	public HttpModle(Map<String, String> msgHeader, String body) {
		if(msgHeader == null){
			msgHeader = new HashMap<String,String>();
		}
		this.msgHeader = msgHeader;
		this.body = body;
	}

	public Map<String, String> getMsgHeader() {
		return msgHeader;
	}

	public void setMsgHeader(Map<String, String> msgHeader) {
		this.msgHeader = msgHeader;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * 控制台打印消息头信息(调试使用)
	 */
	public void printMsgHeader(){
		System.out.println("打印消息头开始------------------------");
		for(Entry<String,String> entry : msgHeader.entrySet()){
			System.out.println("key:" + entry.getKey() + "	value:" + entry.getValue());
		}
		System.out.println("打印消息头结束------------------------");
	}
	
	/**
	 * 控制台打印消息体信息(调试使用)
	 */
	public void printBody(){
		System.out.println("打印消息体开始------------------------");
		System.out.println(body);
		System.out.println("打印消息体结束------------------------");
	}
	
}
