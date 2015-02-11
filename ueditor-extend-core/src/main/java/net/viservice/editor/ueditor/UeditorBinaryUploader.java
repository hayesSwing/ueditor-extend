package net.viservice.editor.ueditor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.viservice.editor.MultipartFile;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;

public class UeditorBinaryUploader {
	
	public static final State save(HttpServletRequest request, Map<String, Object> conf, UeditorService ueditorService) {
		boolean isAjaxUpload = request.getHeader("X_Requested_With") != null;
		if (!ServletFileUpload.isMultipartContent(request)) {
			return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
		}

		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

		if (isAjaxUpload) {
			upload.setHeaderEncoding("UTF-8");
		}

		try {
			String filedName = (String) conf.get("fieldName");
			
			MultipartFile multipartFile = ueditorService.getMultipartFile(filedName, request);
			if (multipartFile == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}
			
			String originFileName = multipartFile.getOriginalFilename();
			String suffix = FileType.getSuffixByFilename(originFileName);
			
			originFileName = originFileName.substring(0, originFileName.length() - suffix.length());
			
			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}
			
			InputStream is = multipartFile.getInputStream();
			State storageState = ueditorService.saveFileByInputStream(multipartFile, maxSize);
			is.close();
			
			if (storageState.isSuccess()) {
				JSONObject jsonObj = new JSONObject(storageState.toJSONString());
				storageState.putInfo("url", jsonObj.getString("url"));
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);
			}

			return storageState;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	private static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);
		return list.contains(type);
	}

}
