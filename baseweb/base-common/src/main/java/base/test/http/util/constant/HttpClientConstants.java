package base.test.http.util.constant;

/**
 * httpclient常量类
 * @author Administrator
 *
 */
public class HttpClientConstants {
	
	/**
	 * http请求url默认编码
	 */
	public static final String URL_ENCODE_DEFAULT = "UTF-8";

	/**
	 * http请求消息体默认编码
	 */
	public static final String BODY_ENCODE_DEFAULT = "application/x-www-form-urlencoded;charset=UTF-8";
	
	/**
	 * http响应消息体默认解码
	 */
	public static final String BODY_DECODE_DEFAULT = "UTF-8";
	
	/**
	 * http响应消息体压缩
	 */
	public static final String BODY_GZIP = "gzip";
	
	/**
	 * https协议名称
	 */
	public static final String PROTOCOL_HTTPS_NAME = "https";
	
	/**
	 * https协议端口号
	 */
	public static final int PROTOCOL_HTTPS_PORT = 443;
	
}
