package base.test.http.util.httpclient;

import java.util.Map;

import base.test.http.util.constant.HttpClientParam;
import base.test.http.util.mode.ReqModle;
import base.test.http.util.mode.ResModle;

/**
 * httpclient接口
 * @author Administrator
 *
 */
public interface IHttpClient {
	
	/**
	 * http请求方式内部类
	 * @author Administrator
	 *
	 */
	enum RequestType{
		// get请求
		METHOD_GET,
		// post请求
		METHOD_POST;
	}
	
	/**
	 * 设置httpclient初始化参数
	 * @param param
	 */
	void setHttpClientParam(HttpClientParam param);
	
	/**
	 * 发送http请求，默认get方式
	 * @param request 请求信息
	 * @param followRedirect 是否自动重定向
	 * @param useCookie 是否使用上一次的cookie,需要是在同一线程中的请求才有效
	 * @return 响应信息
	 */
	ResModle sendRequest(ReqModle request,boolean followRedirect,boolean useCookie) throws Exception;
	
	/**
	 * 发送get请求
	 * @param request 请求信息
	 * @param followRedirect 是否自动重定向
	 * @param useCookie 是否使用上一次的cookie,需要是在同一线程中的请求才有效
	 * @return 响应信息
	 */
	ResModle sendGet(ReqModle request,boolean followRedirect,boolean useCookie) throws Exception;
	
	/**
	 * 发送get请求(默认自动重定向)
	 * @param request 请求信息
	 * @param useCookie 是否使用上一次的cookie,需要是在同一线程中的请求才有效
	 * @return 响应信息
	 */
	ResModle sendGet(ReqModle request,boolean useCookie) throws Exception;
	
	/**
	 * 发送get请求(默认自动重定向，不使用上一次的cookie信息)
	 * @param request 请求信息
	 * @return 响应信息
	 */
	ResModle sendGet(ReqModle request) throws Exception;
	
	/**
	 * 发送get请求
	 * @param url 请求路径
	 * @param followRedirect 是否自动重定向
	 * @param useCookie 是否使用上一次的cookie,需要是在同一线程中的请求才有效
	 * @return 响应信息
	 */
	ResModle sendGet(String url,boolean followRedirect,boolean useCookie) throws Exception;
	
	/**
	 * 发送get请求(默认自动重定向)
	 * @param url 请求路径
	 * @param useCookie 是否使用上一次的cookie,需要是在同一线程中的请求才有效
	 * @return 响应信息
	 */
	ResModle sendGet(String url,boolean useCookie) throws Exception;
	
	/**
	 * 发送get请求(默认自动重定向，不使用上一次的cookie信息)
	 * @param url 请求路径
	 * @return 响应信息
	 */
	ResModle sendGet(String url) throws Exception;
	
	/**
	 * 发送post请求(默认自动重定向)
	 * @param request 请求信息
	 * @param useCookie 是否使用上一次的cookie,需要是在同一线程中的请求才有效
	 * @return 响应信息
	 */
	ResModle sendPost(ReqModle request,boolean useCookie) throws Exception;
	
	/**
	 * 发送post请求(默认自动重定向，不使用上一次的cookie信息)
	 * @param request 请求信息
	 * @return 响应信息
	 */
	ResModle sendPost(ReqModle request) throws Exception;
	
	/**
	 * 发送post请求(默认自动重定向)
	 * @param url 请求路径
	 * @param param 请求参数
	 * @param useCookie 是否使用上一次的cookie,需要是在同一线程中的请求才有效
	 * @return 响应信息
	 */
	ResModle sendPost(String url,Map<String,String> param,boolean useCookie) throws Exception;
	
	/**
	 * 发送post请求(默认自动重定向，不使用上一次的cookie信息)
	 * @param url 请求路径
	 * @param param 请求参数
	 * @return 响应信息
	 */
	ResModle sendPost(String url,Map<String,String> param) throws Exception;
	
	/**
	 * 发送post请求(默认自动重定向)
	 * @param url 请求路径
	 * @param useCookie 是否使用上一次的cookie,需要是在同一线程中的请求才有效
	 * @return 响应信息
	 */
	ResModle sendPost(String url,boolean useCookie) throws Exception;
	
	/**
	 * 发送post请求(默认自动重定向，不使用上一次的cookie信息)
	 * @param url 请求路径
	 * @return 响应信息
	 */
	ResModle sendPost(String url) throws Exception;
}
