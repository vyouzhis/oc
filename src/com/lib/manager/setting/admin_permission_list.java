package com.lib.manager.setting;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class admin_permission_list extends Permission implements
		BasePerminterface {
	private List<String> rmc;

	public admin_permission_list() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		setRoot("name", _MLang("name"));

		setRoot("fun", this);
	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		if (super.Init() == -1)
			return;
		rmc = porg.getRmc();
		if (rmc.size() != 2) {
			Msg(_CLang("error_role"));
			return;
		}
		switch (rmc.get(1).toString()) {
		case "read":
			read(null);
			break;
		case "edit":
			edit(null);
			return;
		default:
			Msg(_CLang("error_role"));
			return;
		}

		super.View();
	}

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		
		String format = "SELECT id,gname,mainrole FROM `"
				+ DB_PRE
				+ "group` where uid=%d and status=1;";
		String sql = String.format(format, aclGetUid());
		List<Map<String, Object>> res = null;
		Map<String, Map<String, String>> PackClassList = new HashMap<>();
		Map<String, String> Gid = new HashMap<>();
		try {
			res = FetchAll(sql);
			for (Map<String, Object> map : res) {
				PackClassList.put(map.get("gname").toString(), JsonToMap(map
						.get("mainrole").toString()));
				Gid.put(map.get("gname").toString(), map.get("id").toString());
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setRoot("Pack_Class_List", PackClassList);
		setRoot("Gid", Gid);
	}

	private Map<String, String> JsonToMap(String json) {
		JSONObject subJson = JSON.parseObject(json);
		Map<String, String> subMap = new HashMap<String, String>();
		for (String key : subJson.keySet()) {
			String dbJson = subJson.getString(key);
			JSONObject dbJ = JSON.parseObject(dbJson);
			for (String k : dbJ.keySet()) {
				subMap.put(k, dbJ.getString(k));
			}

		}

		return subMap;
	}

	public boolean RoleCheck(String role, int action) {
		if (role.length() == 0)
			return false;
		return role.matches("(.*)" + action + "(.*)");
	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edit(Object arg) {
		// TODO Auto-generated method stub

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
