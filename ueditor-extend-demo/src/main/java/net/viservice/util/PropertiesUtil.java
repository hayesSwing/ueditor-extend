package net.viservice.util;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	private static Map<String, Properties> allProperties = new HashMap<String, Properties>();

	private static Properties getProperties(String fileName) {
		Properties properties = allProperties.get(fileName);
		if (properties == null) {
			logger.info("读取文件" + fileName + "......");
			try {
				InputStream in = PropertiesUtil.class.getResourceAsStream("/" + fileName);
				properties = new Properties();
				properties.load(in);
				in.close();
				allProperties.put(fileName, properties);
			} catch (Exception e) {
				logger.error("读取文件" + fileName + "时,发生异常", e);
			}
		}
		return properties;
	}

	// 根据key读取value
	public static String getValue(String fileName, String key) {
		try {
			return getProperties(fileName).getProperty(key);
		} catch (Exception e) {
			logger.error("从文件" + fileName + "中读取properties[" + key + "]的全部信息时,发生异常", e);
		}
		return null;
	}
	
	// 读取properties的全部信息
	@SuppressWarnings("rawtypes")
	public static void getAllProperties(String fileName) {
		try {
			Enumeration en = getProperties(fileName).propertyNames();
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String Property = getProperties(fileName).getProperty(key);
				logger.info(key + "=" + Property);
			}
		} catch (Exception e) {
			logger.error("从文件" + fileName + "中读取properties的全部信息时,发生异常", e);
		}
	}
	
}

