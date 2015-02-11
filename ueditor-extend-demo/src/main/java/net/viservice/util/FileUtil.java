package net.viservice.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	/**
	 * Get the Mime Type from a File
	 * <p>
	 * 参考：http://www.rgagnon.com/javadetails/java-0487.html，http://dada89007.iteye.com/blog/1392606
	 * </p>
	 * @param fileName
	 * @return
	 */
	public static String getMimeType(String fileName) {
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String type = fileNameMap.getContentTypeFor(fileName);
		logger.debug("type:" + type);
		
		return type;
	}
	
	/**
	 * 获得指定文件的byte数组
	 */
	public static byte[] getBytes(String filePath) {
		byte[] buffer = null;
		File file = new File(filePath);
		buffer = getBytes(file);
		return buffer;
	}
	
	/**
	 * 获得指定文件的byte数组
	 */
	public static byte[] getBytes(File file) {
		byte[] buffer = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			buffer = new byte[fis.available()];
			long length = fis.read(buffer);
			fis.close();
			logger.debug("length:" + length);
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException异常", e);
		} catch (IOException e) {
			logger.error("IOException异常", e);
		}
		return buffer;
	}
	
}
