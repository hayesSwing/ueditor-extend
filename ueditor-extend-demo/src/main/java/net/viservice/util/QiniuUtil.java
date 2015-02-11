package net.viservice.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;

import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.config.Config;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.rs.PutPolicy;

/**
 * 七牛云存储工具类
 */
public class QiniuUtil {

	private static final String accessKey = "accessKey";
	private static final String secretKey = "secretKey";

	private static final String bucketName = "bucketName";
	private static final String baseUrl = "baseUrl";

	/**
	 * 生成上传授权uptoken
	 */
	public static String getUploadToken() {
		Config.ACCESS_KEY = accessKey;
		Config.SECRET_KEY = secretKey;
		Mac mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
		PutPolicy putPolicy = new PutPolicy(bucketName);
		String uptoken = null;
		try {
			uptoken = putPolicy.token(mac);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (AuthException e) {
			e.printStackTrace();
		}
		return uptoken;
	}

	/**
	 * 获取访问域名
	 */
	public static String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * 上传文件(InputStream)
	 */
	public static PutRet uploadFileByInputStream(net.viservice.editor.MultipartFile multipartFile) throws IOException {
		String uptoken = getUploadToken();
		PutExtra extra = new PutExtra();
		String key = multipartFile.getOriginalFilename();
		PutRet ret = IoApi.Put(uptoken, key, multipartFile.getInputStream(), extra);
		return ret;
	}

	/**
	 * 上传文件(ByteArray)
	 */
	public static PutRet uploadFileByByteArray(byte[] data) throws IOException {
		String uptoken = getUploadToken();
		PutExtra extra = new PutExtra();
		String key = null;
		InputStream is = new ByteArrayInputStream(data);
		PutRet ret = IoApi.Put(uptoken, key, is, extra);
		return ret;
	}

}
