package com.lib.manager.datasource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.common.Page;
import org.ppl.etc.UrlClassList;

public class mongo_purge_list extends Permission implements BasePerminterface {
	private List<String> rmc;
	private int Limit = 10;
	private int page = 0;

	public mongo_purge_list() {
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
	
		page = toInt(porg.getKey("p"));
	
		switch (rmc.get(1).toString()) {
		case "read":
			read(null);
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
		int offset=0;
		
		if(page!=0){
			offset = (page-1)*Limit;
		}
		
		String format = "select * from "+DB_HOR_PRE+"mongodbrule order by id desc  limit %d offset %d";
		String sql = String.format(format, Limit, offset);
		
		List<Map<String, Object>> res, tmp;

		try {
			res = new ArrayList<>();
			while (true) {
				tmp = FetchAll(sql);				
				res.addAll(tmp);				
				if(isFetchFinal())break;
			}
			
			setRoot("purge_list", res);
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
		setRoot("edit_url", ucl.edit("mongo_db"));
		setRoot("remove_url", ucl.remove("mongo_db_edit_action"));
		setRoot("new_purge_url", ucl.read("mongo_db"));
	}
	
	private int Tol() {
		String sql ="select count(*) as count from "+DB_HOR_PRE+"mongodbrule limit 1";
		Map<String, Object> res;
		res = FetchOne(sql);
		//echo(sql);
		if(res!=null)return toInt( res.get("count"));
		return 0;
	}

	public String QueryRule(int i) {
		switch (i) {
		case 0:
			return "find";
		case 2:
			return "distinct";
		case 3:
			return "count";
		default:
			return "";
		}
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
