package base.test.base.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

/**
 * Json转换的工具类 2013-03-26
 */
@SuppressWarnings({ "unchecked", "deprecation" })
public class JsonUtil {

    private final static Log LOG = LogFactory.getLog(JsonUtil.class);

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // 新版 simleFactory 二进制，高性能

    /**
     * 空参数构造器
     */
    private JsonUtil() {
    }

    /**
     * 静态块初始化
     */
    static {
        // 设置输出包含的属性
        OBJECT_MAPPER.getSerializationConfig().setSerializationInclusion(Inclusion.NON_NULL);
        // 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
        OBJECT_MAPPER.getDeserializationConfig().set(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        OBJECT_MAPPER.getSerializationConfig().setDateFormat(df);
        OBJECT_MAPPER.getDeserializationConfig().setDateFormat(df);
    }

    /**
     * 如果JSON字符串为Null或"null"字符串,返回Null. 如果JSON字符串为"[]",返回空集合.
     * 
     * @param jsonString
     * @param clazz
     */
    public static <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return (T) OBJECT_MAPPER.readValue(jsonString, clazz);
        } catch (IOException e) {
            LOG.error("parse json string error:" + jsonString, e);
        }
        return null;
    }

    /**
     * 读取集合如List/Map,且不是List<String>时.
     * 
     * @param jsonString
     * @param typeReference
     * @return
     */
    public static <T> T fromJson(String jsonString, TypeReference<T> typeReference) {
        if (StringUtils.isBlank(jsonString)) {
            return null;
        }
        try {
            return (T) OBJECT_MAPPER.readValue(jsonString, typeReference);
        } catch (IOException e) {
            LOG.error("parse json string error:" + jsonString, e);
        }
        return null;
    }

    /**
     * 获取泛型的Collection Type,当存入类型是集合，且集合内存的对象不是基本类型（是自定义的对象，防止泛型擦除。如List<MyObject>）。
     * 
     * @param jsonStr
     *            json字符串
     * @param collectionClass
     *            泛型的Collection
     * @param elementClasses
     *            元素类型
     */
    public static <T> T readJson(String jsonStr, Class<?> collectionClass, Class<?>... elementClasses) throws Exception {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        return OBJECT_MAPPER.readValue(jsonStr, javaType);
    }

    /**
     * 如果对象为Null,返回"null". 如果集合为空集合,返回"[]".
     */
    public static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            LOG.warn("write to json string error:" + object, e);
        }
        return null;
    }

    /**
     * 取出Mapper做进一步的设置或使用其他序列化API.
     */
    public static ObjectMapper getMapper() {
        return OBJECT_MAPPER;
    }

    public static void main(String[] args) {
        Date tim = new Date();
        System.out.println(tim);
        String timStr = tim.toString();
        System.out.println(timStr);

        Date dat = fromJson(timStr, Date.class);
        System.out.println(dat);

        System.out.println(toJson(tim));
        JsonUtil j = new JsonUtil();
        timTest t = j.new timTest();
        t.setName("123");
        t.setTime(new Date());
        System.out.println(toJson(t));

        String js = "{\"name\":\"123\",\"time\":\"2015-07-20 18:06:16\"}";
        String jso = "{\"name\":\"123\",\"time\":\"Mon Jul 20 18:06:16 GMT+08:00 2015\"}";
        timTest rigth = fromJson(js, timTest.class);
        timTest wrong = fromJson(jso, timTest.class);
        System.out.println(rigth.getTime());
        System.out.println(wrong.getTime());

    }

    class timTest {

        private String name;

        private Date time;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

    }
}
