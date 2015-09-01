package base.test.http.util.constant;

import java.lang.reflect.Proxy;

import base.test.http.util.httpclient.IHttpClient;
import base.test.http.util.httpclient.IHttpsHandler;
import base.test.http.util.httpclient._3.x.HttpClient_3_x;
import base.test.http.util.httpclient._3.x.HttpsHandler_3_x;
import base.test.http.util.httpclient._4.x.HttpClient_4_x;
import base.test.http.util.httpclient._4.x.HttpsHandler_4_x;

public class HttpClientManager {

	// 当前使用版本
	private static HttpClientVersion version = HttpClientVersion.HTTPCLIENT_3_x;

	// 默认的http客户端
	private static IHttpClient httpClient;

	// 使用新建的http客户端(线程范围,同一线程内仍是同一个客户端)
	private static ThreadLocal<IHttpClient> tHttpClient = new ThreadLocal<IHttpClient>();

	// 默认的https客户端
	private static IHttpClient httpsClient;

	// 使用新建的https客户端(线程范围,同一线程内仍是同一个客户端)
	private static ThreadLocal<IHttpClient> tHttpsClient = new ThreadLocal<IHttpClient>();

	private HttpClientManager(){
		
	}
	
	/**
	 * 根据版本号初始化http客户端
	 */
	private static void initHttp() {
		switch (version) {
		case HTTPCLIENT_3_x:
			httpClient = new HttpClient_3_x(false);
			break;
		case HTTPCLIENT_4_x:
			httpClient = new HttpClient_4_x(false);
			break;
		default:
			throw new RuntimeException("没有找到httpClient对应的处理类");
		}
	}

	/**
	 * 根据版本号初始化线程范围内的http客户端
	 */
	private static void initThreadLocalHttp() {
		switch (version) {
		case HTTPCLIENT_3_x:
			tHttpClient.set(new HttpClient_3_x(true));
			break;
		case HTTPCLIENT_4_x:
			tHttpClient.set(new HttpClient_4_x(true));
			break;
		default:
			throw new RuntimeException("没有找到httpClient对应的处理类");
		}
	}

	/**
	 * 根据版本号初始化https客户端
	 */
	private static void initHttps() {
		switch (version) {
		case HTTPCLIENT_3_x:
			httpsClient = getHttpsByProxy(new HttpClient_3_x(false),
					new HttpsHandler_3_x());
			break;
		case HTTPCLIENT_4_x:
			httpsClient = getHttpsByProxy(new HttpClient_4_x(false),
					new HttpsHandler_4_x());
			break;
		default:
			throw new RuntimeException("没有找到httpsClient对应的处理类");
		}
	}

	/**
	 *根据版本号初始化线程范围内的https客户端
	 */
	private static void initThreadLocalHttps() {
		switch (version) {
		case HTTPCLIENT_3_x:
			tHttpsClient.set(getHttpsByProxy(new HttpClient_3_x(true),
					new HttpsHandler_3_x()));
			break;
		case HTTPCLIENT_4_x:
			tHttpsClient.set(getHttpsByProxy(new HttpClient_4_x(true),
					new HttpsHandler_4_x()));
			break;
		default:
			throw new RuntimeException("没有找到httpsClient对应的处理类");
		}
	}

	/**
	 * 创造http客户端
	 * 
	 * @param useNewClient
	 *           是否使用新建的客户端(线程范围,同一线程内仍是同一个客户端)
	 * @return
	 */
	public static synchronized IHttpClient createHttpClient(boolean useNewClient) {
		if (useNewClient) {
			if (tHttpClient.get() == null) {
				initThreadLocalHttp();
			}
			return tHttpClient.get();
		}
		if (httpClient == null) {
			initHttp();
		}
		return httpClient;
	}

	/**
	 * 创造https客户端
	 * 
	 * @param useNewClient
	 *            是否使用新建的客户端(线程范围,同一线程内仍是同一个客户端)
	 * @return
	 */
	public static synchronized IHttpClient createHttpsClient(boolean useNewClient) {
		if (useNewClient) {
			if (tHttpsClient.get() == null) {
				initThreadLocalHttps();
			}
			return tHttpsClient.get();
		}
		if (httpsClient == null) {
			initHttps();
		}
		return httpsClient;
	}

	/**
	 * 灵活创建client，可自己定义超时时间等参数
	 * @return
	 */
	public static synchronized IHttpClient createClient(boolean isSafe) {
		switch (version) {
		case HTTPCLIENT_3_x:
			return isSafe ? getHttpsByProxy(new HttpClient_3_x(true),new HttpsHandler_3_x()) : new HttpClient_3_x(true);
		case HTTPCLIENT_4_x:
			return isSafe ? getHttpsByProxy(new HttpClient_4_x(true),new HttpsHandler_4_x()) : new HttpClient_4_x(true);
		default:
			throw new RuntimeException("没有找到httpClient对应的处理类");
		}
	}

	/**
	 * 通过jdk动态代理创造https客户端
	 * 
	 * @param client
	 *            https客户端
	 * @param handler
	 *            注册https的处理类
	 * @return
	 */
	private static IHttpClient getHttpsByProxy(IHttpClient client,
			IHttpsHandler handler) {
		HttpsSSLInvocationHandler invocationHandler = new HttpsSSLInvocationHandler(
				client, handler);
		return (IHttpClient) Proxy.newProxyInstance(client.getClass()
				.getClassLoader(), client.getClass().getInterfaces(),
				invocationHandler);
	}

}
