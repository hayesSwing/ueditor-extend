package net.viservice.editor.ueditor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.MIMEType;
import com.baidu.ueditor.define.MultiState;
import com.baidu.ueditor.define.State;

/**
 * 图片抓取器
 */
public class UeditorImageHunter {

	private static final int BUFFER_SIZE = 4096;

	private List<String> allowTypes = null;
	private long maxSize = -1;
	
	private List<String> filters = null;

	private UeditorService ueditorService = null;

	public UeditorImageHunter(Map<String, Object> conf, UeditorService ueditorService) {

		this.maxSize = (Long) conf.get("maxSize");
		this.allowTypes = Arrays.asList((String[]) conf.get("allowFiles"));
		this.filters = Arrays.asList((String[]) conf.get("filter"));

		this.ueditorService = ueditorService;

	}

	//如果要抓取的图片数量太多，此处可以调整为多线程处理。
	public State capture(String[] list) {

		MultiState state = new MultiState(true);

		for (String source : list) {
			state.addState(captureRemoteData(source));
		}

		return state;

	}

	public State captureRemoteData(String urlStr) {

		HttpURLConnection connection = null;
		URL url = null;
		String suffix = null;

		try {
			url = new URL(urlStr);
			
			if (!validHost(url.getHost())) {
				return new BaseState(false, AppInfo.PREVENT_HOST);
			}

			connection = (HttpURLConnection) url.openConnection();

			connection.setInstanceFollowRedirects(true);
			connection.setUseCaches(true);

			if (!validContentState(connection.getResponseCode())) {
				return new BaseState(false, AppInfo.CONNECTION_ERROR);
			}

			suffix = MIMEType.getSuffix(connection.getContentType());

			if (!validFileType(suffix)) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			if (!validFileSize(connection.getContentLength())) {
				return new BaseState(false, AppInfo.MAX_SIZE);
			}
			
			if (urlStr.indexOf("?") > 0) {
				urlStr = urlStr.substring(0, urlStr.indexOf("?"));
			}
			String fileName = urlStr.substring(urlStr.lastIndexOf("/") + 1);
			
			State state = this.ueditorService.saveBinaryFile(getBytes(connection.getInputStream()), fileName);
			
			if (state.isSuccess()) {
				JSONObject jsonObj = new JSONObject(state.toJSONString());
				state.putInfo("url", jsonObj.getString("url"));
				state.putInfo("source", urlStr);
			}

			return state;

		} catch (Exception e) {
			e.printStackTrace();
			return new BaseState(false, AppInfo.REMOTE_FAIL);
		}

	}

	private byte[] getBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
		try {
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
			}
			try {
				out.close();
			} catch (IOException ex) {
			}
		}
		return out.toByteArray();
	}

	private boolean validHost(String hostname) {
		return !filters.contains(hostname);
	}

	private boolean validContentState(int code) {

		return HttpURLConnection.HTTP_OK == code;

	}

	private boolean validFileType(String type) {

		return this.allowTypes.contains(type);

	}

	private boolean validFileSize(int size) {
		return size < this.maxSize;
	}

}
