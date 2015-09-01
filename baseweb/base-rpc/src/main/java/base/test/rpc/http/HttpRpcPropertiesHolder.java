package base.test.rpc.http;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import base.test.base.util.PropertiesUtil;
import base.test.http.util.constant.HttpClientPropertiesHolder;


public class HttpRpcPropertiesHolder {

    private static final Log logger = LogFactory.getLog(HttpRpcPropertiesHolder.class);

    private static final String PROPERTIES_NAME = "httprpc.properties";

    private static final Properties properties;

    public static final String PREFIX_REALTIMEPRICE = "realtimeprice.";

    static {
        properties = PropertiesUtil.getInstanceByFileName(PROPERTIES_NAME);
    }

    /**
     * 实时价格url
     *
     * @return
     */
    public static String getRealtimePriceUrl() {
        String url = "http://pm.3.local/prices/mgets";
        try {
            url = properties.getProperty(PREFIX_REALTIMEPRICE + "pm3cn.url");
        } catch (Exception e) {
            logger.error(e);
        }
        return url;
    }

    public static int getSocketBufferSize(String prefix) {
        int size = HttpClientPropertiesHolder.getSocketBufferSize();
        try {
            size = Integer.parseInt(properties.getProperty(prefix + "socket.buffer.size")) * 1024;
        } catch (Exception e) {
            logger.error(e);
        }
        return size;
    }

    public static int getConnectionTimeout(String prefix) {
        int time = HttpClientPropertiesHolder.getConnectionTimeout();
        try {
            time = Integer.parseInt(properties.getProperty(prefix + "connection.timeout"));
        } catch (Exception e) {
            logger.error(e);
        }
        return time;
    }

    public static int getSoTimeout(String prefix) {
        int time = HttpClientPropertiesHolder.getSoTimeout();
        try {
            time = Integer.parseInt(properties.getProperty(prefix + "so.timeout"));
        } catch (Exception e) {
            logger.error(e);
        }
        return time;
    }

    public static long getConnectionManagerTimeout(String prefix) {
        long time = HttpClientPropertiesHolder.getConnectionManagerTimeout();
        try {
            time = Long.parseLong(properties.getProperty(prefix + "connection.manager.timeout"));
        } catch (Exception e) {
            logger.error(e);
        }
        return time;
    }

    public static boolean isEnableConnectionRepeat(String prefix) {
        boolean enable = HttpClientPropertiesHolder.isEnableConnectionRepeat();
        try {
            enable = Boolean.parseBoolean(properties.getProperty(prefix + "connection.repeat.enable"));
        } catch (Exception e) {
            logger.error(e);
        }
        return enable;
    }

    public static int getConnectionRepeatCount(String prefix) {
        int count = HttpClientPropertiesHolder.getConnectionRepeatCount();
        try {
            count = Integer.parseInt(properties.getProperty(prefix + "connection.repeat.count"));
        } catch (Exception e) {
            logger.error(e);
        }
        return count;
    }
}
