package com.lib.manager.datasource;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.common.Page;
import org.ppl.etc.UrlClassList;

public class api_secret_list extends Permission implements BasePerminterface {
	private List<String> rmc;
	private int Limit = 10;
	private int page = 0;
	private UrlClassList ucl = UrlClassList.getInstance();
	
	public api_secret_list() {
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
		switch (rmc.get(1).toString()) {
		case "read":
			read(null);
			break;
		case "search":
			search(null);
			break;

		default:
			Msg(_CLang("error_role"));
			return;
		}

		super.View();
	}

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub

		setRoot("new_api_url", ucl.read("api_secret_action"));
		
		int offset = 0;

		if (page != 0) {
			offset = (page - 1) * Limit;
		}

		String format = "select * from " + DB_HOR_PRE
				+ "apisecret order by id desc  limit %d offset %d";
		String sql = String.format(format, Limit, offset);

		List<Map<String, Object>> res;

		try {
			res = FetchAll(sql);
			setRoot("api_list", res);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SetPage();
	}

	private void SetPage() {
		Page p = new Page();
		UrlClassList ucl = UrlClassList.getInstance();

		int t = Tol();

		String page_html = p.s_page(ucl.read(SliceName(stdClass)), t, page,
				Limit, "");

		setRoot("Page", page_html);
		setRoot("edit_url", ucl.read("api_secret_action"));
		// setRoot("remove_url", ucl.remove("mongo_db_edit_action"));
		setRoot("new_api_url", ucl.read("api_secret_action"));
	}

	private int Tol() {
		String sql = "select count(*) as count from " + DB_HOR_PRE
				+ "apisecret limit 1";
		Map<String, Object> res;
		res = FetchOne(sql);
		if (res != null)
			return Integer.valueOf(res.get("count").toString());
		return 0;
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
