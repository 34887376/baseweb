package base.test.http.util.constant;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import base.test.http.util.httpclient.IHttpClient;
import base.test.http.util.httpclient.IHttpsHandler;


/**
 * https动态代理的处理类
 * @author Administrator
 *
 */
public class HttpsSSLInvocationHandler implements InvocationHandler{

	private IHttpClient client;
	
	private IHttpsHandler handler;
	
	public HttpsSSLInvocationHandler(IHttpClient client,IHttpsHandler handler){
		this.client = client;
		this.handler = handler;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		// 发送https请求前处理
		handler.beforeSendHttps();
		Object result = method.invoke(client, args);
		// 发送https请求后处理
		handler.afterSendHttps();
		return result;
	}
	
}
