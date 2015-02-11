package net.viservice.util.fastdfs;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import net.viservice.util.FileTypeUtil;
import net.viservice.util.FileUtil;

import org.apache.commons.io.FilenameUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FastdfsUtils {

	private static final Logger logger = LoggerFactory.getLogger(FastdfsUtils.class);

	private static boolean isInit = false;

	private static void init() throws Exception {
		String classPath = new File(FastdfsUtils.class.getResource("/").getFile()).getCanonicalPath();
		String configFilePath = classPath + File.separator + "fdfs_client.conf";
		logger.info("FastdfsUtils.init加载配置文件:" + configFilePath);
		ClientGlobal.init(configFilePath);
	}

	private static TrackerClient getTrackerClient() throws Exception {
		if (!isInit) {
			init();
			isInit = true;
		}

		TrackerClient trackerClient = new TrackerClient();
		return trackerClient;
	}

	private static TrackerServer getTrackerServer() throws Exception {
		return getTrackerClient().getConnection();
	}

	private static StorageClient getStorageClient() throws Exception {
		TrackerServer trackerServer = getTrackerServer();

		// StorageServer storageServer = null;
		// StorageClient storageClient = new StorageClient(trackerServer,
		// storageServer);
		StorageClient storageClient = new StorageClient(trackerServer, null);
		return storageClient;
	}

	/**
	 * 文件方式上传
	 */
	public static Map<String, Object> uploadFile(File file) {
		String fileName = file.getName();
		byte[] fileBuff = FileUtil.getBytes(file);
		return uploadFile(fileBuff, fileName);
	}

	/**
	 * 是否是图片
	 */
	private static boolean isImage(String fileName) {
		return FileTypeUtil.isImageByExtension(fileName);
	}

	/**
	 * 字节流方式上传
	 */
	public static Map<String, Object> uploadFile(byte[] fileBuff, String fileName) {
		logger.debug("fileName:" + fileName);
		String originalFileName = FilenameUtils.getName(fileName);// 文件名
		logger.debug("originalFileName:" + originalFileName);
		String baseName = FilenameUtils.getBaseName(fileName);// 不含后缀名
		logger.debug("baseName:" + baseName);
		String fileExtName = FilenameUtils.getExtension(originalFileName);// 文件后缀名
		logger.debug("fileExtName:" + fileExtName);

		long length = fileBuff.length;// 字节
		logger.debug("length:" + length);

		logger.debug("fileBuff.length:" + fileBuff.length);

		boolean isImage = isImage(originalFileName);
		logger.debug("isImage:" + isImage);
		String mimeType = FileUtil.getMimeType(fileName);
		logger.debug("mimeType:" + mimeType);

		int width = 0;
		int height = 0;
		if (isImage) {
			int[] imageInfo = getImageInfo(fileBuff);
			if (imageInfo != null) {
				width = imageInfo[0];
				height = imageInfo[1];
			}
		}

		String fileType = FileTypeUtil.getFileTypeByStream(fileBuff);
		logger.debug("fileType:" + fileType);

		NameValuePair[] metaList = new NameValuePair[] { new NameValuePair("fileName", fileName), new NameValuePair("isImage", isImage + ""), new NameValuePair("mimeType", mimeType), new NameValuePair("width", width + ""), new NameValuePair("height", height + ""), new NameValuePair("author", "FastdfsUtils") };

		boolean status = false;
		String message = "文件上传失败！";
		String[] responseData = storeFile(fileBuff, fileExtName, metaList);
		Map<String, Object> uploadResult = new HashMap<String, Object>();
		if (responseData != null) {
			status = true;
			message = "文件上传成功！";

			uploadResult.put("isImage", isImage);
			if (isImage) {
				uploadResult.put("width", width);
				uploadResult.put("height", height);
			}
			
			uploadResult.put("groupName", responseData[0]);
			uploadResult.put("storageFileName", responseData[1]);
			uploadResult.put("link", responseData[0] + "/" + responseData[1]);// 文件访问链接
		}
		
		uploadResult.put("status", status);
		uploadResult.put("message", message);
		
		uploadResult.put("fileName", fileName);
		uploadResult.put("mimeType", mimeType);
		uploadResult.put("length", length);
		
		return uploadResult;
	}

	private static int[] getImageInfo(byte[] fileBuff) {
		try {
			// File转为BufferedImage
			// BufferedImage buff = ImageIO.read(new
			// FileImageInputStream(file));
			// BufferedImage buff = ImageIO.read(file);

			// byte[]转为BufferedImage
			ByteArrayInputStream in = new ByteArrayInputStream(fileBuff);// 将byte[]作为输入流；
			BufferedImage image = ImageIO.read(in);// 将in作为输入流，读取图片存入image中，而这里in可以为ByteArrayInputStream();
			int width = image.getWidth();
			int height = image.getHeight();
			return new int[] { width, height };
		} catch (Exception e) {
			logger.error("FastdfsUtils.getImageInfo时发生异常:", e);
		}
		return new int[] { 0, 0 };
	}
	
	private static String[] storeFile(byte[] fileBuff, String fileExtName, NameValuePair[] metaList) {
		String[] responseData = null;
		try {
			StorageClient storageClient = getStorageClient();
			responseData = storageClient.upload_file(fileBuff, fileExtName.toLowerCase(), metaList);
		} catch (Exception e) {
			logger.error("FastdfsUtils.storeFile时发生异常:", e);
		}
		return responseData;
	}
	
}
