package base.test.rpc.http;

import base.test.http.util.constant.HttpClientManager;
import base.test.http.util.constant.HttpClientParam;
import base.test.http.util.httpclient.IHttpClient;

public class HttpRpcFactory {

	// 实时价格httpclient
	private static IHttpClient realtimePriceHttpClient;
	// 实时价格httpsclient
	private static IHttpClient realtimePriceHttpsClient;  
	
	private HttpRpcFactory(){
		
	}
	
	/**
	 * 创建realtimePrice使用的httpclient
	 * @param isSafe (true:使用https,false:使用http)
	 * @return
	 */
	public static synchronized IHttpClient createRealtimePriceClient(boolean isSafe){
		if(isSafe){
			initRealtimePriceHttpsClient();
			return realtimePriceHttpsClient;
		}else{
			initRealtimePriceHttpClient();
			return realtimePriceHttpClient;
		}
	}
	
	private static void initRealtimePriceHttpClient(){
		if(realtimePriceHttpClient == null){
			realtimePriceHttpClient = HttpClientManager.createClient(false);
			initClientFromParam(realtimePriceHttpClient,HttpRpcPropertiesHolder.PREFIX_REALTIMEPRICE);
		}
	}
	
	private static void initRealtimePriceHttpsClient(){
		if(realtimePriceHttpsClient == null){
			realtimePriceHttpsClient = HttpClientManager.createClient(true);
			initClientFromParam(realtimePriceHttpsClient,HttpRpcPropertiesHolder.PREFIX_REALTIMEPRICE);
		}
	}
	/**
	 * 根据前缀不同，为不同的client初始化不同的配置
	 * @param client
	 * @param prefix
	 */
	private static void initClientFromParam(IHttpClient client,String prefix){
		HttpClientParam param = new HttpClientParam();
		param.setSocketBufferSize(HttpRpcPropertiesHolder.getSocketBufferSize(prefix));
		param.setConnectionTimeout(HttpRpcPropertiesHolder.getConnectionTimeout(prefix));
		param.setSoTimeout(HttpRpcPropertiesHolder.getSoTimeout(prefix));
		param.setConnectionManagerTimeout(HttpRpcPropertiesHolder.getConnectionManagerTimeout(prefix));
		param.setRequestSentRetryEnabled(HttpRpcPropertiesHolder.isEnableConnectionRepeat(prefix));
		param.setRetryCount(HttpRpcPropertiesHolder.getConnectionRepeatCount(prefix));
		client.setHttpClientParam(param);
	}
	
}
