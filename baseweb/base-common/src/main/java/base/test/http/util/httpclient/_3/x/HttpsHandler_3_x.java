package base.test.http.util.httpclient._3.x;

import org.apache.commons.httpclient.protocol.Protocol;

import base.test.http.util.constant.HttpClientConstants;
import base.test.http.util.httpclient.IHttpsHandler;


public class HttpsHandler_3_x implements IHttpsHandler {

	public void beforeSendHttps() {
		Protocol https = new Protocol(HttpClientConstants.PROTOCOL_HTTPS_NAME,
				new HttpsSSLProtocolSocketFactory(),
				HttpClientConstants.PROTOCOL_HTTPS_PORT);
		Protocol.registerProtocol(HttpClientConstants.PROTOCOL_HTTPS_NAME,https);
	}

	public void afterSendHttps() {
		Protocol.unregisterProtocol(HttpClientConstants.PROTOCOL_HTTPS_NAME);
	}

}
