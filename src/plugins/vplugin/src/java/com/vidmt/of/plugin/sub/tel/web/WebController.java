package com.vidmt.of.plugin.sub.tel.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.roster.RosterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xmpp.packet.JID;
import org.xmpp.packet.Presence;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vidmt.of.plugin.sub.extdb.Acc;
import com.vidmt.of.plugin.sub.extdb.Acc.AccType;
import com.vidmt.of.plugin.sub.extdb.VUser;
import com.vidmt.of.plugin.sub.tel.entity.Lvl;
import com.vidmt.of.plugin.sub.tel.entity.User;
import com.vidmt.of.plugin.sub.tel.old.service.LvlService;
import com.vidmt.of.plugin.sub.tel.old.service.UserService;
import com.vidmt.of.plugin.utils.CommUtil;
import com.vidmt.of.plugin.utils.MoneyStatUtil;
import com.vidmt.of.plugin.utils.VUtil;
import com.vidmt.of.plugin.utils.VerStatUtil;

@Controller
@RequestMapping("/vplugin/api/web")
public class WebController {
	private static final Logger log = LoggerFactory.getLogger(WebController.class);
	@Autowired
	private UserService userService;
	@Autowired
	private LvlService lvlService;

	@ResponseBody
	@RequestMapping("/sys/verinfo.*")
	public JSONObject verinfo() {
		JSONArray jarr = new JSONArray();
		Map<String, Date> map = VerStatUtil.get();
		for (Entry<String, Date> entry : map.entrySet()) {
			JSONObject unit = new JSONObject();
			unit.put("client", entry.getKey());
			unit.put("lasttime", entry.getValue());
			jarr.add(unit);
		}
		
		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", jarr);
		return json;
	}

	@ResponseBody
	@RequestMapping("/sys/moneyinfo.*")
	public JSONObject moneyinfo() {
//		MoneyStatUtil.main(null);
		JSONObject json = new JSONObject();
		json.put("c", 0);
		json.put("d", MoneyStatUtil.get());
		return json;
	}

	@ResponseBody
	@RequestMapping("/user/get.*")
	public JSONObject getuser(Long uid, String phone, Long page, Integer pageSize, HttpServletRequest req) {
		List<User> list = new ArrayList<>();
		Acc acc = null;
		if (uid != null) {
			acc = new Acc(AccType.uid, uid.toString());
		} else if (phone != null) {
			acc = new Acc(AccType.phone, phone);
		}
		if (acc != null) {
			User user = userService.findByAcc(acc);
			if (user != null) {
				list.add(user);
			}
		} else {
			if (page != null && pageSize != null) {
				long offset = (page - 1) * pageSize;
				list = userService.fuzzySearch(null, null, offset, pageSize);
			}
		}

		JSONArray jarr = new JSONArray(list.size());

		PresenceManager presencemgr = XMPPServer.getInstance().getPresenceManager();
		for (User user : list) {
			JSONObject jobj = new JSONObject();
			String avatar = user.getAvatarUri();
			if (avatar != null) {
				if (!avatar.startsWith("/static")) {
					avatar = "/static" + avatar;
				}
				jobj.put("avatar", avatar);
			}

			jobj.put("uid", user.getId());
			jobj.put("nick", user.getNick());
			jobj.put("phone", user.getPhone());
			VUser ofuser = new VUser(user);
			if (presencemgr.isAvailable(ofuser)) {
				Presence.Show show = presencemgr.getPresence(ofuser).getShow();
				if (show != null) {
					jobj.put("presence", presencemgr.getPresence(ofuser).getShow());
				} else {
					jobj.put("presence", "available");
				}
			}
			jobj.put("lvl", user.getLvl());
			if (user.getLvlEnd() != null) {
				jobj.put("endTime", CommUtil.fmtDate(user.getLvlEnd()));
			}
			jarr.add(jobj);
		}

		JSONObject json = new JSONObject();
		json.put("c", "0");
		json.put("d", jarr);
		return json;
	}

	@ResponseBody
	@RequestMapping("/user/delete.*")
	public JSONObject deleteuser(Long uid, String phone) {
		RosterManager rostermgr = XMPPServer.getInstance().getRosterManager();
		if (uid != null) {
			rostermgr.deleteRoster(new JID(uid + "@" + VUtil.getDomain()));
			userService.deleteByUid(uid);
			VUtil.deleteRes(uid);
		} else if (phone != null) {
			Acc acc = new Acc("phone:" + phone);
			User user = userService.findByAcc(acc);
			if (user != null) {
				uid = user.getId();
				rostermgr.deleteRoster(new JID(uid + "@" + VUtil.getDomain()));
				userService.deleteByUid(uid);
				VUtil.deleteRes(uid);
			}
		}
		return JSON.parseObject("{'c':0}");
	}

	@ResponseBody
	@RequestMapping("/user/update.*")
	public JSONObject updateuser(User user) {
		Long uid = user.getId();
		if (uid == null) {
			return JSON.parseObject("{'c':1,'m':'uid不能为空'}");
		}
		if (user.getLvl() != null) {
			Lvl lvl = lvlService.getLvlByType(user.getLvl());
			userService.updateUserPayed(uid, lvl);
		} else {
			userService.updateUserExpired(user.getId());
		}
		return JSON.parseObject("{'c':0}");
	}
}
