package base.test.http.util.constant;

public class HttpClientParam {
	
	// socket缓冲区大小(单位KB)
	private int socketBufferSize;
	
	// 连接超时时间(单位毫秒)
	private int connectionTimeout;
	
	// 读数据超时时间(单位毫秒)
	private int soTimeout;
	
	// 连接管理者超时时间(单位毫秒)
	private long connectionManagerTimeout;
	
	// 是否禁用重连
	private boolean requestSentRetryEnabled;
	
	// 重连次数
	private int retryCount;
	
	/**
	 * 默认禁用重连
	 */
	public HttpClientParam(){
		socketBufferSize = HttpClientPropertiesHolder.getSocketBufferSize();
		connectionTimeout = HttpClientPropertiesHolder.getConnectionTimeout();
		soTimeout = HttpClientPropertiesHolder.getSoTimeout();
		connectionManagerTimeout = HttpClientPropertiesHolder.getConnectionManagerTimeout();
		requestSentRetryEnabled = HttpClientPropertiesHolder.isEnableConnectionRepeat();
		retryCount = HttpClientPropertiesHolder.getConnectionRepeatCount();
	}

	/**
	 * 
	 * @param requestSentRetryEnabled 是否禁用重连
	 * @param retryCount 重连次数
	 */
	public HttpClientParam(boolean requestSentRetryEnabled, int retryCount) {
		this();
		this.requestSentRetryEnabled = requestSentRetryEnabled;
		this.retryCount = retryCount;
	}

	public boolean isRequestSentRetryEnabled() {
		return requestSentRetryEnabled;
	}

	public void setRequestSentRetryEnabled(boolean requestSentRetryEnabled) {
		this.requestSentRetryEnabled = requestSentRetryEnabled;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public int getSocketBufferSize() {
		return socketBufferSize;
	}

	public void setSocketBufferSize(int socketBufferSize) {
		this.socketBufferSize = socketBufferSize;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getSoTimeout() {
		return soTimeout;
	}

	public void setSoTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
	}

	public long getConnectionManagerTimeout() {
		return connectionManagerTimeout;
	}

	public void setConnectionManagerTimeout(long connectionManagerTimeout) {
		this.connectionManagerTimeout = connectionManagerTimeout;
	}

}
