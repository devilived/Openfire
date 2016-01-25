package com.vidmt.of.plugin.sub.token;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.util.ParamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vidmt.of.plugin.VPlugin;
import com.vidmt.of.plugin.utils.CommUtil;

//@Controller
@RequestMapping("/vplugin")
public class TokenController {
	private static final Logger log = LoggerFactory.getLogger(TokenController.class);

	@RequestMapping("/token.json")
	protected void token(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String token = ParamUtils.getParameter(req, "token");
		Map<String, Object> map = new HashMap<>();
		try {
			XMPPServer xmppServer = XMPPServer.getInstance();
			VPlugin vPlugin = (VPlugin) xmppServer.getPluginManager().getPlugin(VPlugin.NAME);
			TokenPlugin tokenPlg = vPlugin.getSubPlugin(TokenPlugin.class);
			String uid = tokenPlg.getUidByToken(token);
			if (uid != null) {
				map.put("uid", uid);
			} else {
				map.put("c", -1);
				map.put("m", "token not exist:" + token);
			}
		} catch (Throwable e) {
			String eStr = CommUtil.fmtException(e);
			log.error("valid TOKEN error:" + eStr);
			map.put("c", -1);
			map.put("m", "alid TOKEN error:" + eStr);
		}
	}
}
