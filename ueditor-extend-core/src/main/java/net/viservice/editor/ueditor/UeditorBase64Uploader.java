package net.viservice.editor.ueditor;

import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;

public class UeditorBase64Uploader {

	public static State save(String content, Map<String, Object> conf, UeditorService ueditorService) {

		byte[] data = decode(content);

		long maxSize = ((Long) conf.get("maxSize")).longValue();

		if (!validSize(data, maxSize)) {
			return new BaseState(false, AppInfo.MAX_SIZE);
		}
		
		String suffix = FileType.getSuffix("JPG");
		State storageState = ueditorService.saveBinaryFile(data, conf.get("filename")+ suffix);
		
		if (storageState.isSuccess()) {
			JSONObject jsonObj = new JSONObject(storageState.toJSONString());
			storageState.putInfo("url", jsonObj.getString("url"));
			storageState.putInfo("type", suffix);
			storageState.putInfo("original", "");
		}

		return storageState;
	}

	private static byte[] decode(String content) {
		return Base64.decodeBase64(content);
	}

	private static boolean validSize(byte[] data, long length) {
		return data.length <= length;
	}

}
