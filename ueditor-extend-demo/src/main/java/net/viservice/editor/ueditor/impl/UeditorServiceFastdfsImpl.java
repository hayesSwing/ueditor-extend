package net.viservice.editor.ueditor.impl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.MultiState;
import com.baidu.ueditor.define.State;

import net.viservice.editor.ueditor.UeditorService;
import net.viservice.util.PropertiesUtil;
import net.viservice.util.fastdfs.FastdfsUtils;

/**
 * UeditorService实现 - Fastdfs
 */
@Component("UeditorServiceFastdfsImpl")
public class UeditorServiceFastdfsImpl implements UeditorService{

	@Override
	public net.viservice.editor.MultipartFile getMultipartFile(String filedName, HttpServletRequest request) {
		net.viservice.editor.MultipartFile resultFile = null;
		try {
			MultipartHttpServletRequest multipartHttpservletRequest = (MultipartHttpServletRequest) request;
			MultipartFile multipartFile = multipartHttpservletRequest.getFile(filedName);
			if (!multipartFile.isEmpty()) {
				resultFile = new net.viservice.editor.StandardMultipartFile(filedName, multipartFile.getInputStream(), multipartFile.getOriginalFilename(), multipartFile.getSize());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultFile;
	}
	
	@Override
	public State saveFileByInputStream(net.viservice.editor.MultipartFile multipartFile, long maxSize) {
		State state = null;
		try {
			if (multipartFile.getSize() > maxSize) {
				return new BaseState(false, AppInfo.MAX_SIZE);
			}
			
			Map<String, Object> uploadResult = null;
			String originalFileName = multipartFile.getOriginalFilename();
			uploadResult = FastdfsUtils.uploadFile(multipartFile.getBytes(), originalFileName);

			if ((Boolean) uploadResult.get("status")) {
				state = new BaseState(true);
				state.putInfo("size", uploadResult.get("length").toString());
				state.putInfo("title", uploadResult.get("fileName").toString());
				//state.putInfo("groupName", uploadResult.get("groupName").toString());
				//state.putInfo("storageFileName", uploadResult.get("storageFileName").toString());
				state.putInfo("url", PropertiesUtil.getValue("applicationContext.properties", "dfsFileAccessBasePath") + "/" + uploadResult.get("link").toString());
				
				// 把上传的文件信息记入数据库
				// ---自行处理---
				return state;
			}
		} catch (IOException e) {

		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}
	
	@Override
	public State saveBinaryFile(byte[] data, String fileName) {
		State state = null;
		
		Map<String, Object> uploadResult = FastdfsUtils.uploadFile(data, fileName);
		if ((Boolean) uploadResult.get("status")) {
			state = new BaseState(true);
			state.putInfo("size", uploadResult.get("length").toString());
			state.putInfo("title", uploadResult.get("fileName").toString());
			//state.putInfo("groupName", uploadResult.get("groupName").toString());
			//state.putInfo("storageFileName", uploadResult.get("storageFileName").toString());
			state.putInfo("url", PropertiesUtil.getValue("applicationContext.properties", "dfsFileAccessBasePath") + "/" + uploadResult.get("link").toString());
			
			// 把上传的文件信息记入数据库
			// ---自行处理---
			return state;
		}
		
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	@Override
	public State listFile(String[] allowFiles, int start, int pageSize) {
		//把计入数据库中的文件信息读取出来，返回即可
		
		//下面的代码，仅作示例
		State state = new MultiState( true );
		state.putInfo( "start", start);
		state.putInfo( "total", 0);
		return state;
	}
	
	
	//---------以下两个注释的方法，是我项目中的处理方式，可供参考-------------//
	
//	@SuppressWarnings("unchecked")
//	@Override
//	public State listFile(String[] allowFiles, int start, int pageSize) {
//		Pager pager = new Pager();
//
//		int pageNumber = start / pageSize + 1;
//		pager.setPageSize(pageSize);
//		pager.setPageNumber(pageNumber);
//
//		//System.out.println("allowFiles:"+Arrays.toString(allowFiles));
//		//allowFiles:[png, jpg, jpeg, gif, bmp]
//		
//		Criteria criteria = new Criteria();
//		criteria.add(Restrictions.order("createDate", "desc"));
//
//		State state = null;
//		try {
//			pager = dfsFileService.getPager(getTenantId(), criteria, pager);
//			state = this.getState((List<DfsFile>) pager.getList());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		state.putInfo("start", start);
//		state.putInfo("total", pager.getTotalCount());
//
//		return state;
//	}
//
//	private State getState(List<DfsFile> files) {
//		MultiState state = new MultiState(true);
//		BaseState fileState = null;
//		
//		for (DfsFile dfsFile : files) {
//			fileState = new BaseState(true);
//			fileState.putInfo("url", PropertiesUtil.getValue("applicationContext.properties", "dfsFileAccessBasePath") + "/" + dfsFile.getLink());
//			state.addState(fileState);
//		}
//		return state;
//	}
	

}
