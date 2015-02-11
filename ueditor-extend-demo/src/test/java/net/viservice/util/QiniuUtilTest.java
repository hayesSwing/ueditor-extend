package net.viservice.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qiniu.api.io.PutRet;

import junit.framework.TestCase;

public class QiniuUtilTest extends TestCase {

	private static final Logger logger = LoggerFactory.getLogger(QiniuUtilTest.class);

	@Override
	protected void setUp() throws Exception {
	}

	@Override
	protected void tearDown() throws Exception {
	}
	
	public void testGetUploadToken() {
		String uptoken = QiniuUtil.getUploadToken();
		logger.debug("uptoken:" + uptoken);
	}
	
	public void testUploadFileByInputStream() throws IOException {
		String localFilePath = "C:/Users/Administrator/Desktop/001/index/20141119002.jpg";
		File localFile = new File(localFilePath);
		net.viservice.editor.MultipartFile multipartFile = new net.viservice.editor.StandardMultipartFile("filedName", new FileInputStream(localFile), localFile.getName(), localFile.length());
		
		PutRet putRet = QiniuUtil.uploadFileByInputStream(multipartFile);
		
		logger.debug("putRet:" + putRet);
	}
	
}
