package com.lib.manager.user;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;

public class user_list extends Permission implements BasePerminterface {
	private List<String> rmc;

	public user_list() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		setRoot("name", _MLang("name"));

		setRoot("fun", this);
	}

	public void Show() {
		if (super.Init() == -1)
			return;
		Default();
		rmc = porg.getRmc();
		if (rmc.size() != 2) {
			Msg(_CLang("error_role"));
			return;
		}
		switch (rmc.get(1).toString()) {
		case "read":
			read(null);
			break;
		default:
			Msg(_CLang("error_role"));
			return;
		}

		super.View();

	};

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		
		String sql = "SELECT uid,name,nickname,email,"
				+ "ctime , ltime FROM "
				+ "`" + DB_PRE
				+ "user_info` where cid=" + aclGetUid();

		List<Map<String, Object>> res;
		try {
			res = FetchAll(sql);
			setRoot("list_user", res);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("edit_url", ucl.read("user_profile"));			
	}

	private void Default() {
		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("create_url", ucl.read("user_profile"));
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
