package base.test.http.util.httpclient._3.x;

import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import base.test.http.util.constant.HttpClientPropertiesHolder;



public class MyConnectionManager {
	
	static HttpConnectionManager multiManager;
	
	private MyConnectionManager(){
		
	}
	
	public static void initMultiManager(){
		multiManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = new HttpConnectionManagerParams();
		params.setStaleCheckingEnabled(true);
		params.setDefaultMaxConnectionsPerHost(HttpClientPropertiesHolder.getMultiThreadConnectionPoolMaxHost());
		params.setMaxTotalConnections(HttpClientPropertiesHolder.getMultiThreadConnectionPoolMaxTotal());
		params.setTcpNoDelay(false);
		multiManager.setParams(params);
	}
	
	public static synchronized HttpConnectionManager getConnectionManager(){
		if(multiManager == null){
			initMultiManager();
		}
		return multiManager;
	}
	
}
