package com.lib.manager.datasource;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.db.MGDB;
import org.ppl.etc.UrlClassList;

import com.alibaba.fastjson.JSON;

public class mongo_db extends Permission implements BasePerminterface {
	private List<String> rmc;

	private int fetch_query = 0;
	private int ctype_query = 0;
	private String where_query;
	private String field_query;
	private String sort_query;
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
		String fq = porg.getKey("fetch_query");
		if (fq.matches("[0-9]+")) {
			fetch_query = Integer.valueOf(fq);
		}

		field_query = porg.getKey("field_query");
		where_query = porg.getKey("where_query");

		where_query = Myreplace(where_query);

		field_query = Myreplace(field_query);

		echo("where:"+where_query);
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

		String select_collection = porg.getKey("db_collection");
		if (select_collection == null) {

			setRoot("def_collention", def_col);
		} else {
			mgdb.SetCollection(select_collection);
			setRoot("def_collention", select_collection);
		}

		setRoot("CollectionList", clists);

		mgdb.DBEnd();

		String cq = porg.getKey("ctype_query");
		if (cq != null && cq.matches("[0-9]+")) {
			ctype_query = Integer.valueOf(cq);
		}
		setRoot("ctype_query", ctype_query);
	}

	private void SortQuery() {

		sort_query = porg.getKey("sort_query");

		sort_query = Myreplace(sort_query);

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
			setRoot("error", "error:"+mgdb.getErrorMsg());
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
		String news = old.replace("&nbsp;", "");
		news = news.replace("&quot;", "\"");

		return news;
	}

}
