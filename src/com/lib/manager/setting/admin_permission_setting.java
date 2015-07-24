package com.lib.manager.setting;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.common.ShowMessage;
import org.ppl.etc.UrlClassList;
import org.ppl.etc.globale_config;
import org.ppl.io.TimeClass;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class admin_permission_setting extends Permission implements
		BasePerminterface {
	private List<String> argList;
	private JSONObject MainRoleJson;
	private List<String> SubRoleJson;
	private Map<String, String> role_pg = null;

	public admin_permission_setting() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		role_pg = porg.getAllpg();
		super.setAction(1);
		setRoot("name", _MLang("name"));
		setRoot("fun", this);
	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		if (super.Init() == -1)
			return;
		argList = porg.getRmc();
		def();

		if (argList.size() != 2) {
			Msg(_CLang("error_role"));
			return;
		}
		switch (argList.get(1).toString()) {
		case "read":
			read(null);
			break;
		case "create":
			create(null);
			Msg();
			return;
		case "edit":
			edit(null);
			Msg();
			return;
		default:
			Msg(_CLang("error_role"));
			return;
		}

		super.View();
	}

	private void Msg() {
		UrlClassList ucl = UrlClassList.getInstance();
		String ok_url = ucl.read("admin_permission_list");

		ShowMessage ms = ShowMessage.getInstance();
		String res = ms.SetMsg(ok_url, _CLang("ok_save"), 3000);
		super.setHtml(res);
		super.isAutoHtml = false;
	}

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		UrlClassList ucl = UrlClassList.getInstance();
		Map<String, List<String>> PackClassList;
		PackClassList = ucl.getPackClassList();
		
		setRoot("action_url", ucl.create(SliceName(stdClass)));

		if (porg.getKey("gid") != null) {
			getGroupMainRole(porg.getKey("gid"));
			setRoot("gid", porg.getKey("gid"));
			setRoot("action_url", ucl.edit(SliceName(stdClass)));
			getGroupUser(porg.getKey("gid"));
		}
		String Json = aclfetchMyRole();

		setRoot("Pack_Class_List", PackClassList);
	}

	@SuppressWarnings("unchecked")
	private void getGroupMainRole(String gid) {
		String sql = "SELECT gname, gdesc, mainrole, subrole FROM `"
				+ DB_PRE + "group` where id=" + gid;
		Map<String, Object> res = FetchOne(sql);
		setRoot("group_name", res.get("gname"));
		setRoot("group_desc", res.get("gdesc"));
		MainRoleJson = null;
		SubRoleJson = null;
		if (res.get("mainrole") != null)
			MainRoleJson = JSON.parseObject(res.get("mainrole").toString());
		if (res.get("subrole").toString().length() > 0)
			SubRoleJson = JSON.parseObject(res.get("subrole").toString(),
					List.class);

	}

	public boolean MainRoleCheckAction(String main, String lib, int action) {
		if (MainRoleJson == null)
			return false;
		if (!MainRoleJson.containsKey(main))
			return false;
		Object rj = MainRoleJson.get(main);

		JSONObject libJson = JSON.parseObject(rj.toString());
		if (!libJson.containsKey(lib))
			return false;

		String act = libJson.get(lib).toString();

		return act.matches("(.*)" + action + "(.*)");

	}

	public boolean SubroleCheckAction(String lib) {
		if (SubRoleJson == null)
			return false;
		return SubRoleJson.contains(lib);
	}

	private void def() {
		
		String subRole = mConfig.GetValue(globale_config.SubRole);
		JSONObject subJson = JSON.parseObject(subRole);
		Map<String, String> subMap = new HashMap<String, String>();

		for (Entry<String, Object> entry : subJson.entrySet()) {
			String key = entry.getKey();
			Object val = entry.getValue();
			subMap.put(key, val.toString());
		}

		setRoot("subMap", subMap);

	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub

		String group_name = role_pg.get("group_name");
		String group_desc = role_pg.get("group_desc");

		String SubRole = TreatSubRole();

		role_pg.remove("group_name");
		role_pg.remove("group_desc");
		role_pg.remove("gid");

		String MainRole = TreatMainRole();

		TimeClass tc = TimeClass.getInstance();
		int now = (int) tc.time();
		String format = "INSERT INTO `"
				+ DB_PRE
				+ "group` "
				+ "(`gname`, `gdesc`, `mainrole`, `subrole`,`uid`,`ctime`, `etime`)"
				+ " VALUES ('%s', '%s',  '%s', '%s', %d, %d, %d);";
		String sql = String.format(format, group_name, group_desc, MainRole,
				SubRole, aclGetUid(), now, now);
		
		try {
			update(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void edit(Object arg) {
		// TODO Auto-generated method stub

		String SubRole = TreatSubRole();
		String group_name = role_pg.get("group_name");
		String group_desc = role_pg.get("group_desc");
		String gid = role_pg.get("gid");

		role_pg.remove("group_name");
		role_pg.remove("group_desc");
		role_pg.remove("gid");

		String MainRole = TreatMainRole();
		
		TimeClass tc = TimeClass.getInstance();
		int now = (int) tc.time();
		String format = "UPDATE `"
				+ DB_PRE
				+ "group` SET "
				+ "`gname` = '%s', `gdesc` = '%s', `mainrole` = '%s', `subrole` = '%s', `etime` = '%d' "
				+ "WHERE `role_group`.`id` = %s;";

		String sql = String.format(format, group_name, group_desc, MainRole,
				SubRole, now, gid);

		try {
			update(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String TreatSubRole() {
		
		String subRole = mConfig.GetValue(globale_config.SubRole);
		JSONObject subJson = JSON.parseObject(subRole);
		List<String> subList = new ArrayList<String>();

		for (Entry<String, Object> entry : subJson.entrySet()) {
			String key = entry.getKey();
			if (role_pg.containsKey(key)) {
				subList.add(key);
				role_pg.remove(key);
			}
		}
		String subRoles = "";
		if (subList.size() > 0) {
			subRoles = JSON.toJSONString(subList);
		}

		return subRoles;
	}

	private String TreatMainRole() {

		Map<String, Map<String, String>> role = new HashMap<String, Map<String, String>>();
		Map<String, String> sub;
		String sub_name, sub_action;
		for (Entry<String, String> entry : role_pg.entrySet()) {
			String key = entry.getKey();
			// String value = entry.getValue();
			String[] role_split = key.split("_", 2);

			sub_name = role_split[1].substring(0, role_split[1].length() - 2);
			sub_action = role_split[1].substring(role_split[1].length() - 1);

			if (!role.containsKey(role_split[0])) {
				sub = new HashMap<String, String>();
			} else {
				sub = role.get(role_split[0]);
				if (sub.containsKey(sub_name)) {
					sub_action += "_" + sub.get(sub_name);
				}
			}
			sub.put(sub_name, sub_action);
			role.put(role_split[0], sub);
		}

		return JSON.toJSONString(role);
	}

	private void getGroupUser(String gid) {
		
		String format = "SELECT nickname,email FROM `"
				+ DB_PRE
				+ "user_info` where gid=%s and status=1 and isdelete=0;";
		String sql = String.format(format, gid);
		List<Map<String, Object>> res = null;
		try {
			res = FetchAll(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setRoot("group_user", res);
	}

	@Override
	public void remove(Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void search(Object arg) {
		// TODO Auto-generated method stub

	}

}
