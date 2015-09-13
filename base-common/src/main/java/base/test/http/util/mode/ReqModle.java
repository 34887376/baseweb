package base.test.http.util.mode;

import java.util.HashMap;
import java.util.Map;

import base.test.http.util.constant.HttpClientConstants;


/**
 * http请求封装类
 * @author Administrator
 *
 */
public class ReqModle extends HttpModle{
	
	// 请求路径
	private String url;
	
	// url编码
	private String urlEncode = HttpClientConstants.URL_ENCODE_DEFAULT;
	
	// 消息体编码
	private String bodyEncode = HttpClientConstants.BODY_ENCODE_DEFAULT;
	
	// 消息体解码
	private String bodyDecode;
	
	// post方式请求参数
	private Map<String,String> param = new HashMap<String,String>();
	
	/**
	 * 没有参数的构造函数
	 */
	public ReqModle(){
		
	}
	
	/**
	 * 构造函数
	 * @param url 请求路径
	 */
	public ReqModle(String url){
		this(url,null,null);
	}
	
	/**
	 * 构造函数
	 * @param url 请求路径
	 * @param header 消息头
	 */
	public ReqModle(String url,Map<String,String> header){
		this(url,header,null);
	}
	
	/**
	 * 构造函数
	 * @param param post方式提交参数
	 * @param url 请求路径
	 */
	public ReqModle(Map<String,String> param,String url){
		this(url,null,param);
	}
	
	/**
	 * 构造函数
	 * @param url 请求路径
	 * @param header 消息头
	 * @param param post方式提交参数
	 */
	public ReqModle(String url,Map<String,String> header,Map<String,String> param){
		super(header);
		this.url = url;
		this.param = param;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getParam() {
		return param;
	}

	public void setParam(Map<String, String> param) {
		this.param = param;
	}

	public String getUrlEncode() {
		return urlEncode;
	}

	public void setUrlEncode(String urlEncode) {
		this.urlEncode = urlEncode;
	}

	public String getBodyEncode() {
		return bodyEncode;
	}

	public void setBodyEncode(String bodyEncode) {
		this.bodyEncode = bodyEncode;
	}

	public String getBodyDecode() {
		return bodyDecode;
	}

	public void setBodyDecode(String bodyDecode) {
		this.bodyDecode = bodyDecode;
	}
	
}
