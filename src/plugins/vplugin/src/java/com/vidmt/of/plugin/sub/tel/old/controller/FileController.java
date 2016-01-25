package com.vidmt.of.plugin.sub.tel.old.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.exceptoins.CodeException;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;
import com.vidmt.of.plugin.utils.VUtil;

@Controller
public class FileController {
	private static final Logger log = LoggerFactory.getLogger(FileController.class);
	@Autowired
	private UserService userService;

	@ResponseBody
	@RequestMapping(value = "/vplugin/api/1/user/upload.*", method = RequestMethod.POST)
	public JSONObject oldupload(String type, MultipartHttpServletRequest req, HttpServletResponse resp) {
		ResType restype = null;
		if ("AVATAR".equals(type)) {
			restype = ResType.AVATAR;
		} else if ("ALBUM".equals(type)) {
			restype = ResType.PHOTO;
		}
		JSONObject json = upload(restype, req, resp);
		if (json.getIntValue("c") > 0) {
			return json;
		}
		String data = json.getString("d");
		JSONObject newJson = new JSONObject();
		newJson.put("c", 0);
		newJson.put("m", data.replace("/static", ""));
		return newJson;
	}

	@ResponseBody
	@RequestMapping(value = "/vplugin/api/2/file/upload.*", method = RequestMethod.POST)
	public JSONObject upload(ResType type, MultipartHttpServletRequest req, HttpServletResponse resp) {
		Long uid = (Long) req.getAttribute("uid");
		StringBuilder resUri = new StringBuilder();
		Map<String, MultipartFile> files = req.getFileMap();
		List<File> successedFile = new ArrayList<>();
		for (String key : files.keySet()) {
			try {
				String uri = VUtil.saveRes(type, uid, files.get(key));
				successedFile.add(new File(VUtil.getStaticDir(), uri));
				resUri.append(uri).append("|");
			} catch (IOException e) {
				log.error("保存上传的文件错误", e);
				for (File f : successedFile) {
					f.delete();
				}
				if (type == ResType.AVATAR) {
					JSONObject json = new JSONObject();
					json.put("c", CodeException.ERR_UNKOWN);
					json.put("m", e.getMessage());
					return json;
				}
			}
		}
		if (resUri.length() > 0) {
			resUri.deleteCharAt(resUri.length() - 1);
		}
		if (type == ResType.AVATAR) {
			userService.updateAvatar(uid, resUri.toString());
		} else if (type == ResType.PHOTO) {
			userService.updatePhoto(uid, resUri.toString());
		}
		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", resUri.toString());
		return json;
	}

	public static enum ResType {
		AVATAR, PHOTO
	}

}
