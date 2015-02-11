package net.viservice.editor.ueditor;

import java.util.Map;

import com.baidu.ueditor.define.State;

public class UeditorFileManager {
	
	private String[] allowFiles = null;
	private int count = 0;

	private UeditorService ueditorService = null;
	
	public UeditorFileManager(Map<String, Object> conf, UeditorService ueditorService) {
		this.allowFiles = this.getAllowFiles(conf.get("allowFiles"));
		this.count = (Integer) conf.get("count");
		
		this.ueditorService = ueditorService;
	}
	
	public State listFile(int index) {
		return ueditorService.listFile(this.allowFiles,index, this.count);
	}
	
	private String[] getAllowFiles(Object fileExt) {
		
		String[] exts = null;
		String ext = null;

		if (fileExt == null) {
			return new String[0];
		}

		exts = (String[]) fileExt;

		for (int i = 0, len = exts.length; i < len; i++) {

			ext = exts[i];
			exts[i] = ext.replace(".", "");

		}

		return exts;

	}

}
