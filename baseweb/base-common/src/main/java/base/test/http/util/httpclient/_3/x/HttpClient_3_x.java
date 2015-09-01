package base.test.http.util.httpclient._3.x;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.DefaultHttpParams;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.params.CoreConnectionPNames;

import base.test.http.util.constant.HttpClientParam;
import base.test.http.util.constant.HttpClientPropertiesHolder;
import base.test.http.util.constant.HttpClientUtil;
import base.test.http.util.httpclient.IHttpClient;
import base.test.http.util.mode.ReqModle;
import base.test.http.util.mode.ResModle;


public class HttpClient_3_x implements IHttpClient{

	private HttpClient client;
	
	private HttpClientParam param;
	
	private ThreadLocal<HttpMethod> tMethod = new ThreadLocal<HttpMethod>();
	
	public HttpClient_3_x(boolean useNewClient){
		if(!useNewClient){
			initClient();
		}
	}
	
	public void setHttpClientParam(HttpClientParam param) {
		this.param = param;
		initClient();
	}



	/**
	 * 初始化httpClient
	 */
	private void initClient(){
		if(client == null){
			client = new HttpClient();
			if(param == null){
				param = new HttpClientParam();
			}
			
			// 设置连接池
			if(HttpClientPropertiesHolder.isUseMultiThreadConnectionPool()){
				client.setHttpConnectionManager(MyConnectionManager.getConnectionManager());
			}
			
			// 设置socket缓冲区大小
			client.getParams().setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, param.getSocketBufferSize());
			
			// 设置协议版本号
			client.getHostConfiguration().getParams().setParameter(HttpMethodParams.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			
			// 设置重连处理handler
			client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(param.getRetryCount(),param.isRequestSentRetryEnabled()));
			
			// 设置连接管理者超时时间
			client.getParams().setParameter(HttpClientParams.CONNECTION_MANAGER_TIMEOUT, param.getConnectionManagerTimeout());
			
			// 设置连接超时时间
			client.getHttpConnectionManager().getParams().setConnectionTimeout(param.getConnectionTimeout());
			
			// 设置读数据超时时间
			client.getHttpConnectionManager().getParams().setSoTimeout(param.getSoTimeout());
			
			// 使用与浏览器一样的cookie策略
			DefaultHttpParams.getDefaultParams().setParameter("http.protocol.cookie-policy", CookiePolicy.BROWSER_COMPATIBILITY);
		}
	}
	
	/**
	 * 创建get方式的method实例
	 * @return
	 */
	private HttpMethod CreateGetMethod(){
		HttpMethod get = new GetMethod();
		return get;
	}
	
	/**
	 * 创建post方式的method实例
	 * @return
	 */
	private HttpMethod CreatePostMethod(){
		HttpMethod post = new PostMethod();
		return post;
	}
	
	/**
	 * 根据RequestType创建method实例
	 * @param type 请求方式
	 */
	private void CreateHttpMethod(RequestType type){
		if(tMethod.get() == null){
			switch (type) {
			case METHOD_GET:
				tMethod.set(CreateGetMethod());
				break;
			case METHOD_POST:
				tMethod.set(CreatePostMethod());
				break;
			default:
				System.out.println("没有找到httpMethod对应的方法类型,默认使用get方式");
				tMethod.set(CreateGetMethod());
			}
		}
	}
	
	/**
	 * 从当前线程中移除method实例
	 */
	private void distoryHttpMethod(){
		if(tMethod.get() != null){
			tMethod.remove();
		}
	}
	
	/**
	 * 获得httpClient实例(线程单例的)
	 * @return
	 */
	private HttpClient getHttpClient(){
		if(client == null){
			initClient();
		}
		return client;
	}
	
	/**
	 * 获得httpMethod实例(线程单例的)
	 * @return
	 */
	private HttpMethod getHttpMethod(){
		if(tMethod.get() == null){
			System.out.println("没有初始化httpMethod，默认使用get方式");
			CreateHttpMethod(RequestType.METHOD_GET);
		}
		return tMethod.get();
	}
	
	public ResModle sendRequest(ReqModle request,
			boolean followRedirect, boolean useCookie) throws Exception{
		
		// 获得method
		HttpMethod method = getHttpMethod();
		
		// 根据请求信息初始化method
		ReqHelper.initRequestMethod(method,request);
		
		// 如果不允许自动重定向
		if (!followRedirect) {
			method.setFollowRedirects(false);
		}

		// 设置cookie
		if (useCookie) {
			method.addRequestHeader("Cookie", getCookies());
		}
		
		ResModle response = new ResModle();
		try {
			getHttpClient().executeMethod(method);
			ResHelper.initResponse(method, request, response);
		} catch (HttpException e) {
			doWhenHttpException(e);
		} catch (IOException e) {
			doWhenHttpException(e);
		}finally{
			method.releaseConnection();
		}
		
		// 移除当前线程中的method
		distoryHttpMethod();
		
		return response;
	}

	private void doWhenHttpException(Exception e) throws Exception{
		throw e;
	}
	
	public ResModle sendGet(ReqModle request, boolean followRedirect,
			boolean useCookie) throws Exception{
		CreateHttpMethod(RequestType.METHOD_GET);
		return sendRequest(request,followRedirect,useCookie);
	}

	public ResModle sendGet(ReqModle request, boolean useCookie) throws Exception{
		CreateHttpMethod(RequestType.METHOD_GET);
		return sendRequest(request,true,useCookie);
	}

	
	public ResModle sendGet(ReqModle request) throws Exception{
		CreateHttpMethod(RequestType.METHOD_GET);
		return sendRequest(request,true,false);
	}

	
	public ResModle sendGet(String url, boolean followRedirect,
			boolean useCookie) throws Exception{
		ReqModle request = new ReqModle(url);
		CreateHttpMethod(RequestType.METHOD_GET);
		return sendRequest(request,followRedirect,useCookie);
	}

	
	public ResModle sendGet(String url, boolean useCookie) throws Exception{
		ReqModle request = new ReqModle(url);
		CreateHttpMethod(RequestType.METHOD_GET);
		return sendRequest(request,true,useCookie);
	}

	
	public ResModle sendGet(String url) throws Exception{
		ReqModle request = new ReqModle(url);
		CreateHttpMethod(RequestType.METHOD_GET);
		return sendRequest(request,true,false);
	}

	
	public ResModle sendPost(ReqModle request, boolean useCookie) throws Exception{
		CreateHttpMethod(RequestType.METHOD_POST);
		return sendRequest(request,true,useCookie);
	}

	
	public ResModle sendPost(ReqModle request) throws Exception{
		CreateHttpMethod(RequestType.METHOD_POST);
		return sendRequest(request,true,false);
	}

	
	public ResModle sendPost(String url, Map<String, String> param,
			boolean useCookie) throws Exception{
		ReqModle request = new ReqModle(param,url);
		CreateHttpMethod(RequestType.METHOD_POST);
		return sendRequest(request,true,useCookie);
	}

	
	public ResModle sendPost(String url, Map<String, String> param) throws Exception{
		ReqModle request = new ReqModle(param,url);
		CreateHttpMethod(RequestType.METHOD_POST);
		return sendRequest(request,true,false);
	}

	
	public ResModle sendPost(String url, boolean useCookie) throws Exception{
		ReqModle request = new ReqModle(url);
		CreateHttpMethod(RequestType.METHOD_POST);
		return sendRequest(request,true,useCookie);
	}

	public ResModle sendPost(String url) throws Exception{
		ReqModle request = new ReqModle(url);
		CreateHttpMethod(RequestType.METHOD_POST);
		return sendRequest(request,true,false);
	}

	/**
	 * 获得cookie内容
	 * @return
	 */
	private String getCookies(){
		StringBuffer strCookie = new StringBuffer("");
		Cookie[] cookies = getHttpClient().getState().getCookies();
		if(cookies == null || cookies.length < 1){
			return "";
		}
		for (Cookie c : cookies) {
			if(c == null) continue;
			strCookie.append(c).append(";");
		}
		return strCookie.toString();
	}
	
	/**
	 * 请求工具类
	 * @author Administrator
	 *
	 */
	private static class ReqHelper{

		/**
		 * 初始化请求信息
		 * @param method
		 * @param request
		 */
		private static void initRequestMethod(HttpMethod method,ReqModle request){
			setRequestURI(method,request);
			setRequestHeader(method,request);
			setRequestBodyEncode(method,request);
			setRequestParam(method,request);
		}
		
		/**
		 * 设置请求URI
		 * @param method
		 * @param request
		 */
		private static void setRequestURI(HttpMethod method,ReqModle request){
			try {
				// 设置url
				method.setURI(new URI(request.getUrl(),false,request.getUrlEncode()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * 设置请求头
		 * @param method
		 * @param request
		 */
		private static void setRequestHeader(HttpMethod method,ReqModle request){
			if(request.getMsgHeader() != null && (!request.getMsgHeader().isEmpty())){
				for(String key : request.getMsgHeader().keySet()){
					method.addRequestHeader(key, request.getMsgHeader().get(key));
				}
			}
		}
		
		/**
		 * 设置请求体编码方式
		 * @param method
		 * @param request
		 */
		private static void setRequestBodyEncode(HttpMethod method,ReqModle request){
			method.addRequestHeader("Content-Type",request.getBodyEncode());
		}
		
		/**
		 * 设置post请求参数
		 * @param method
		 * @param request
		 */
		private static void setRequestParam(HttpMethod method,ReqModle request){
			if(request.getParam() != null && (!request.getParam().isEmpty())){
				NameValuePair pairs[] = new NameValuePair[request.getParam().size()];
				int index = 0;
				for(String key : request.getParam().keySet()){
					NameValuePair pair = new NameValuePair();
					pair.setName(key);
					pair.setValue(request.getParam().get(key));
					pairs[index] = pair;
					index ++;
				}
				if(method instanceof PostMethod){
					method = (PostMethod)method;
					((PostMethod) method).setRequestBody(pairs);
				}
			}
		}
	}
	
	/**
	 * 响应工具类
	 * @author Administrator
	 *
	 */
	private static class ResHelper{
		
		/**
		 * 设置响应信息
		 * @param method
		 * @param request
		 * @param response
		 */
		private static void initResponse(HttpMethod method,ReqModle request,ResModle response){
			response.setStatus(method.getStatusCode(), method.getStatusText());
			Map<String,String> responseHeader = setResponseHeader(method,response);
			setResponseBody(method,request,response,responseHeader.get("Content-Type"));
		}
		
		/**
		 * 设置响应信息消息头
		 * @param method
		 * @param response
		 */
		private static Map<String,String> setResponseHeader(HttpMethod method,ResModle response){
			Map<String,String> responseHeader = new HashMap<String,String>();
			Header[] headers = method.getResponseHeaders();
			if(headers != null){
				for(Header header : headers){
					if(header == null) continue;
					responseHeader.put(header.getName(), header.getValue());
				}
			}
			response.setMsgHeader(responseHeader);
			return responseHeader;
		}
		
		/**
		 * 设置相应消息体
		 * @param method
		 * @param response
		 */
		private static void setResponseBody(HttpMethod method,ReqModle request,ResModle response,String contentType){
			String bodyDecode = HttpClientUtil.getEncodeFromContentType(contentType);
			if(request.getBodyDecode() != null && !request.getBodyDecode().equals("")){
				bodyDecode = request.getBodyDecode();
			}
			try {
				response.setBody(getBody(method,bodyDecode));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * 获得响应的消息体内容
		 * @param request 请求方法对象
		 * @param encoding 响应消息体解码方式
		 * @return
		 * @throws IOException
		 */
		private static String getBody(HttpMethod request, String encoding) throws IOException {
			String responseBody = null;
			InputStream responseStream = null;
			BufferedReader breader = null;
			try{
				responseStream = request.getResponseBodyAsStream();
				Header encodHeader = request.getResponseHeader("Content-Encoding");
				if(encodHeader != null && HttpClientUtil.isGzipBody(encodHeader.getValue())){
					breader = new BufferedReader(new InputStreamReader(new GZIPInputStream(responseStream), encoding));
				}else{
					breader = new BufferedReader(new InputStreamReader(responseStream, encoding));
				}
				String tempLine = breader.readLine();
				StringBuffer tempStr = new StringBuffer();
				String crlf = System.getProperty("line.separator");
				while (tempLine != null) {
					tempStr.append(tempLine);
					tempStr.append(crlf);
					tempLine = breader.readLine();
				}
				responseBody = tempStr.toString();
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(breader != null){
					breader.close();
				}
				if(responseStream != null){
					responseStream.close();
				}
			}
			
			return responseBody;
		}
		
	}
	
}
