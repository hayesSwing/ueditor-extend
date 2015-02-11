package net.viservice.editor.ueditor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.baidu.ueditor.ActionEnter;
import com.baidu.ueditor.ConfigManager;
import com.baidu.ueditor.define.ActionMap;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;

public class UeditorActionEnter extends ActionEnter {

	private HttpServletRequest request = null;

	private String rootPath = null;
	private String contextPath = null;

	private String actionType = null;

	private ConfigManager configManager = null;

	private UeditorService ueditorService = null;

	public UeditorActionEnter(HttpServletRequest request, String rootPath, UeditorService ueditorService) {
		super(request, rootPath);

		this.request = request;
		this.rootPath = rootPath;
		this.actionType = request.getParameter("action");
		this.contextPath = request.getContextPath();
		this.configManager = ConfigManager.getInstance(this.rootPath, this.contextPath, request.getRequestURI());

		this.ueditorService = ueditorService;

	}
	
	@Override
	public String exec() {
		return super.exec();
	}

	@Override
	public String invoke() {

		if (actionType == null || !ActionMap.mapping.containsKey(actionType)) {
			return new BaseState(false, AppInfo.INVALID_ACTION).toJSONString();
		}

		if (this.configManager == null || !this.configManager.valid()) {
			return new BaseState(false, AppInfo.CONFIG_ERROR).toJSONString();
		}

		State state = null;

		int actionCode = ActionMap.getType(this.actionType);

		Map<String, Object> conf = null;

		switch (actionCode) {

			case ActionMap.CONFIG:
				return this.configManager.getAllConfig().toString();
	
			case ActionMap.UPLOAD_IMAGE:
			case ActionMap.UPLOAD_SCRAWL:
			case ActionMap.UPLOAD_VIDEO:
			case ActionMap.UPLOAD_FILE:
				conf = this.configManager.getConfig(actionCode);
				state = new UeditorUploader(request, conf, this.ueditorService).doExec();
				break;
	
			case ActionMap.CATCH_IMAGE:
				conf = configManager.getConfig(actionCode);
				String[] list = this.request.getParameterValues((String) conf.get("fieldName"));
				state = new UeditorImageHunter(conf, this.ueditorService).capture(list);
				break;
	
			case ActionMap.LIST_IMAGE:
			case ActionMap.LIST_FILE:
				conf = configManager.getConfig(actionCode);
				int start = this.getStartIndex();
				state = new UeditorFileManager(conf, this.ueditorService).listFile(start);
				break;

		}

		return state.toJSONString();

	}
	
}
