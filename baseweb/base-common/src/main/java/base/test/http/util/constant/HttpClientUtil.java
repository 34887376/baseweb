package base.test.http.util.constant;
/**
 * httpclient工具类
 * @author Administrator
 *
 */
public class HttpClientUtil {
	
	/**
	 * 获取Content-type消息头中的编码方式
	 * @param contentType
	 * @return
	 */
	public static String getEncodeFromContentType(String contentType){
		if(contentType == null || contentType.equals("")){
			return HttpClientConstants.BODY_DECODE_DEFAULT;
		}
		String[] arrayTmp = contentType.split(";");
		if(arrayTmp.length > 1){
			String[] arrayStr = arrayTmp[1].split("=");
			if(arrayStr.length > 1){
				return arrayStr[1].trim();
			}
		}
		return HttpClientConstants.BODY_DECODE_DEFAULT;
	}
	
	/**
	 * 获取contentEncoding消息头中的编码方式
	 * @param contentEncoding
	 * @return
	 */
	public static String getEncodingFromContentEncoding(String contentEncoding){
		if(contentEncoding == null || contentEncoding.equals("")){
			return "";
		}
		return contentEncoding.toLowerCase();
	}
	
	/**
	 * 获取响应消息体是否压缩格式
	 * @param contentEncoding
	 * @return
	 */
	public static boolean isGzipBody(String contentEncoding){
		if(getEncodingFromContentEncoding(contentEncoding).indexOf(HttpClientConstants.BODY_GZIP) > -1){
			return true;
		}
		return false;
	}
	
}
