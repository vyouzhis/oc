package com.lib.manager.datasource;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.db.MGDB;
import org.ppl.etc.UrlClassList;

import com.alibaba.fastjson.JSON;

public class mongo_db extends Permission implements BasePerminterface {
	private List<String> rmc;

	private String db_collection;
	private int fetch_query = 0;	
	private String where_query;
	private String field_query;
	private String sort_query;
	private String project_name;
	private MGDB mgdb;

	public mongo_db() {
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

		db_collection = porg.getKey("db_collection");
		String fq = porg.getKey("fetch_query");
		if (fq!= null && fq.matches("[0-9]+")) {
			fetch_query = Integer.valueOf(fq);
		}
		where_query = porg.getKey("where_query");
		field_query = porg.getKey("field_query");
		sort_query = porg.getKey("sort_query");
		project_name = porg.getKey("project_name");
		
		where_query = Myreplace(where_query);

		field_query = Myreplace(field_query);
		sort_query = Myreplace(sort_query);
		
		mgdb = new MGDB();
		rmc = porg.getRmc();
		if (rmc.size() != 2) {
			Msg(_CLang("error_role"));
			return;
		}
		CollectionList();
		switch (rmc.get(1).toString()) {
		case "read":
			read(null);
			break;
		case "search":
			search(null);
			break;
		case "create":
			create(null);
			return;
		default:
			Msg(_CLang("error_role"));
			return;
		}

		setRoot("fetch_query", fetch_query);
		super.View();
	}

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		UrlClassList ucl = UrlClassList.getInstance();

		setRoot("action_url", ucl.search(SliceName(stdClass)));

		TipList(null);

	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		
		int now = time();
		
		String format = "INSERT INTO hor_rule(name, collention, qaction, query, field, sort, ctime, stime, etime)" +
				"VALUES ('%s','%s','%s','%s','%s','%s',%d, %d, %d)";
		String sql = String.format(format, project_name,db_collection,fetch_query, where_query, field_query,sort_query, now, 0,0);
		echo(sql);
		
		UrlClassList ucl = UrlClassList.getInstance();
		String url = ucl.read(SliceName(stdClass));
		try {
			update(sql);
			TipMessage(url, _CLang("ok_save"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

		

		echo("where:" + where_query);
		if (where_query.length() == 0) {
			setRoot("where_query", "{}");
		} else {
			setRoot("where_query", where_query);
		}

		if (field_query.length() == 0) {
			setRoot("field_query", "{}");
		} else {
			setRoot("field_query", field_query);
		}

		if (fetch_query == 0) {
			SortQuery();
			Find();
		} else if (fetch_query == 2) {
			Distinct();
		} else if (fetch_query == 3) {
			FetchCount();
		}

		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("action_url", ucl.search(SliceName(stdClass)));
		setRoot("create_url", ucl.create(SliceName(stdClass)));
		setRoot("save_project", "1");
	}

	private void CollectionList() {

		Set<String> clist = mgdb.CollectionList();
		String clists = "";
		String def_col = "";
		for (String s : clist) {
			if (s.equals("system.indexes"))
				continue;
			def_col = s;
			clists += "\"" + s + "\",";
		}
		clists.substring(0, clists.length() - 1);

		if (db_collection == null) {

			setRoot("def_collention", def_col);
		} else {
			mgdb.SetCollection(db_collection);
			setRoot("def_collention", db_collection);
		}

		setRoot("CollectionList", clists);

		mgdb.DBEnd();
	}

	private void SortQuery() {

		

		if (sort_query.length() > 2) {
			mgdb.JsonSort(sort_query);
			setRoot("sort_query", sort_query);
		} else {
			setRoot("sort_query", "{}");
		}
	}

	private void FetchCount() {
		if (where_query.length() > 2 && where_query.substring(0, 1).equals("{")) {
			mgdb.JsonWhere(where_query);
		}
		if (field_query.length() > 2) {
			mgdb.JsonColumn(field_query);
		}

		int count = mgdb.FetchCont();
		setRoot("error", "Total:" + count);
	}

	private void Distinct() {
		where_query = where_query.replace("\"", "");

		if (field_query.length() > 2) {

			mgdb.JsonColumn(field_query);
		}

		List<Object> res = mgdb.FetchDistinct(where_query);

		if (res != null) {

			String list_json = JSON.toJSONString(res);

			setRoot("data_result", list_json);
		} else {
			setRoot("error", "error:" + mgdb.getErrorMsg());
		}
		TipList(null);

		mgdb.DBEnd();
	}

	private void Find() {

		mgdb.setLimit(10);

		if (where_query.length() > 2 && where_query.substring(0, 1).equals("{")) {
			mgdb.JsonWhere(where_query);
		}
		if (field_query.length() > 2) {
			mgdb.JsonColumn(field_query);
		}
		boolean isOk = mgdb.FetchList();
		List<String> field = null;
		if (isOk == true) {
			List<String> list = mgdb.GetJsonValue();
			String data_json = "";
			if (list != null) {
				for (String json : list) {
					data_json += json + ",";
				}
				data_json = data_json.substring(0, data_json.length() - 1);
				setRoot("data_result", "[" + data_json + "]");

				field = mgdb.Fields();
			} else {
				setRoot("error", mgdb.getErrorMsg());
			}

		} else {
			setRoot("error", mgdb.getErrorMsg());
		}
		TipList(field);
		mgdb.DBEnd();
	}

	private void TipList(List<String> new_field) {

		List<String> field_all = mgdb.UpdateTipList(new_field);
		if (field_all != null) {
			String json_field = JSON.toJSON(field_all).toString();

			setRoot("field_all", json_field);
		}
	}

	private String Myreplace(String old) {
		if(old == null)return null;
		
		String news = old.replace("&nbsp;", "");
		news = news.replace("&quot;", "\"");

		return news;
	}

}
