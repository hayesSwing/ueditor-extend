package net.viservice.editor.ueditor.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import net.viservice.editor.ueditor.UeditorService;
import net.viservice.util.QiniuUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.MultiState;
import com.baidu.ueditor.define.State;
import com.qiniu.api.io.PutRet;

/**
 * UeditorService实现 - qiniu
 */
@Component("UeditorServiceQiniuImpl")
public class UeditorServiceQiniuImpl implements UeditorService {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
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

			PutRet putRet = QiniuUtil.uploadFileByInputStream(multipartFile);
			
			if (putRet.ok()) {
				state = new BaseState(true);
				state.putInfo("size", multipartFile.getSize());
				state.putInfo("title", multipartFile.getOriginalFilename());
				state.putInfo("url", QiniuUtil.getBaseUrl() + "/" + putRet.getKey());

				// 把上传的文件信息记入数据库
				// ---自行处理---
				return state;
			} else {
				logger.error("文件上传失败，请检查配置参数是否正确！");
			}
		} catch (IOException e) {

		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	@Override
	public State saveBinaryFile(byte[] data, String fileName) {
		State state = null;
		try {
			PutRet putRet = QiniuUtil.uploadFileByByteArray(data);
			if (putRet.ok()) {
				state = new BaseState(true);
				state.putInfo("size", data.length);
				state.putInfo("title", fileName);
				state.putInfo("url", QiniuUtil.getBaseUrl() + "/" + putRet.getKey());

				// 把上传的文件信息记入数据库
				// ---自行处理---
				return state;
			} else {
				logger.error("文件上传失败，请检查配置参数是否正确！");
			}
		} catch (IOException e) {

		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	@Override
	public State listFile(String[] allowFiles, int start, int pageSize) {
		// 把计入数据库中的文件信息读取出来，返回即可

		// 下面的代码，仅作示例
		State state = new MultiState(true);
		state.putInfo("start", start);
		state.putInfo("total", 0);
		return state;
	}

}
