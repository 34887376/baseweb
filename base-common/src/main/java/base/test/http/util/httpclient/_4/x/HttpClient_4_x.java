package base.test.http.util.httpclient._4.x;

import java.util.Map;

import base.test.http.util.constant.HttpClientParam;
import base.test.http.util.httpclient.IHttpClient;
import base.test.http.util.mode.ReqModle;
import base.test.http.util.mode.ResModle;


public class HttpClient_4_x implements IHttpClient{
	
	private HttpClientParam param;
	
	public HttpClient_4_x(boolean useNewClient){
		if(!useNewClient){
			initClient();
		}
	}
	
	/**
	 * 初始化httpClient
	 */
	private void initClient(){
		
	}
	
	public void setHttpClientParam(HttpClientParam param) {
		this.param = param;
	}

	public ResModle sendRequest(ReqModle request,
			boolean followRedirect, boolean useCookie) {
		
		return null;
	}

	public ResModle sendGet(ReqModle request, boolean followRedirect,
			boolean useCookie) {
		
		return null;
	}

	public ResModle sendGet(ReqModle request, boolean useCookie) {
		
		return null;
	}

	public ResModle sendGet(ReqModle request) {
		
		return null;
	}

	public ResModle sendGet(String url, boolean followRedirect,
			boolean useCookie) {
		
		return null;
	}

	public ResModle sendGet(String url, boolean useCookie) {
		
		return null;
	}

	public ResModle sendGet(String url) {
		
		return null;
	}

	public ResModle sendPost(ReqModle request, boolean useCookie) {
		
		return null;
	}

	public ResModle sendPost(ReqModle request) {
		
		return null;
	}

	public ResModle sendPost(String url, Map<String, String> param,
			boolean useCookie) {
		
		return null;
	}

	public ResModle sendPost(String url, Map<String, String> param) {
		
		return null;
	}

	public ResModle sendPost(String url, boolean useCookie) {
		
		return null;
	}

	public ResModle sendPost(String url) {
		
		return null;
	}
	
}
