package base.test.base.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {
	
	private static Map<String, Properties> propMap = new HashMap<String, Properties>();

	private static Properties getProperties(String propName) {
		Properties p = new Properties();
		InputStream in = getClassLoader().getResourceAsStream(propName);
		try {
			p.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * 根据properties文件名得到properties
	 * 
	 * @param fileName
	 * @return
	 */
	public static Properties getInstanceByFileName(String fileName) {
		if (!propMap.containsKey(fileName)) {
			propMap.put(fileName, getProperties(fileName));
		}
		return propMap.get(fileName);
	}

	/**
	 * 根据properties文件名和属性名得到属性值
	 * 
	 * @param fileName
	 * @param propKey
	 * @return
	 */
	public static String getPropertyByFileName(String fileName, String propKey) {
		if (!propMap.containsKey(fileName)) {
			propMap.put(fileName, getProperties(fileName));
		}
		Properties properties = propMap.get(fileName);
		return properties.getProperty(propKey);
	}

	private static ClassLoader getClassLoader() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

		if (classLoader == null) {
			classLoader = PropertiesUtil.class.getClassLoader();
		}
		return classLoader;
	}
	
}
