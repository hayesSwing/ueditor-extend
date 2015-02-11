package net.viservice.controller;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.viservice.editor.ueditor.UeditorActionEnter;
import net.viservice.editor.ueditor.UeditorService;
import net.viservice.util.UnicodeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Ueditor后台处理入口
 */
@Controller("UeditorController")
@RequestMapping("ueditor")
public class UeditorController {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	//@Resource(name="UeditorServiceFastdfsImpl")
	@Resource(name="UeditorServiceQiniuImpl")
	private UeditorService ueditoreService;

	@RequestMapping(value = "execute")
	@ResponseBody
	public String execute(HttpServletRequest request, HttpServletResponse response, Model model) throws UnsupportedEncodingException {
		String rootPath = request.getServletContext().getRealPath("/");
		String resultMsg = new UeditorActionEnter(request, rootPath, this.ueditoreService).exec();

		logger.error("ueditor execute ... resultMsg:" + UnicodeUtil.fromUnicode(resultMsg));

		return resultMsg;
	}

}
