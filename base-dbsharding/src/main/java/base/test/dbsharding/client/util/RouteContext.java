package base.test.dbsharding.client.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 路由的上下文信息
 *
 */
public class RouteContext {


    protected final static ThreadLocal<Map<Object, Object>> threadContext = new MapThreadLocal();

    private RouteContext() {}

    public static void put(Object key, Object value) {
        getContextMap().put(key, value);
    }

    public static Object remove(Object key) {
        return getContextMap().remove(key);
    }

    public static Object get(Object key) {
        return getContextMap().get(key);
    }

    public static boolean containsKey(Object key) {
        return getContextMap().containsKey(key);
    }

    private static class MapThreadLocal extends ThreadLocal<Map<Object, Object>> {
        protected Map<Object, Object> initialValue() {
            return new HashMap<Object, Object>() {

                private static final long serialVersionUID = 3637958959138295593L;

                public Object put(Object key, Object value) {
                    return super.put(key, value);
                }
            };
        }
    }

    protected static Map<Object, Object> getContextMap() {
        return threadContext.get();
    }

    public static void reset() {
        getContextMap().clear();
    }

}
