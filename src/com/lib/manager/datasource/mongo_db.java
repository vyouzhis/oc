package com.lib.manager.datasource;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.db.MGDB;
import org.ppl.etc.UrlClassList;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoException;
import com.sun.mail.auth.MD4;

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
		InAction();
		setRoot("fun", this);
	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		if (super.Init() == -1)
			return;

		db_collection = porg.getKey("db_collection");
		String fq = porg.getKey("fetch_query");
		if (fq != null && fq.matches("[0-9]+")) {
			fetch_query = Integer.valueOf(fq);
		}
		where_query = porg.getKey("where_query");
		field_query = porg.getKey("field_query");
		sort_query = porg.getKey("sort_query");
		project_name = porg.getKey("project_name");

		if (project_name != null) {
			setRoot("project_name", project_name);
		}
		where_query = escapeHtml(where_query);

		field_query = escapeHtml(field_query);
		sort_query = escapeHtml(sort_query);

		mgdb = new MGDB();
		rmc = porg.getRmc();
		if (rmc.size() != 2) {
			Msg(_CLang("error_role"));
			return;
		}
		setRoot("snap_id", 0);
		setRoot("cid", 0);
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
		case "edit":
			edit(null);
			break;
		default:
			Msg(_CLang("error_role"));
			return;
		}
		mgdb.Close();
		setRoot("fetch_query", fetch_query);
		
		listPid();
		
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
		int snap = 0;
		UrlClassList ucl = UrlClassList.getInstance();
		String url = ucl.read(SliceName(stdClass));
		int cid = toInt(porg.getKey("cid_list"));
		
		if(porg.getKey("snap_id").equals("1")){
			snap = 1;
		}
		
		int now = time();
		int stime = getLastTime(db_collection);
		if(stime==0){
			TipMessage(url, _CLang("error_null"));
			return;
		}
		String format = "INSERT INTO "
				+ DB_HOR_PRE
				+ "mongodbrule(name, collention, qaction, query, field, sort, ctime, stime, etime, snap,cid)"
				+ "VALUES ('%s','%s','%s','%s','%s','%s',%d, %d, %d, %d, %d)";
		String sql = String.format(format, project_name, db_collection,
				fetch_query, where_query, field_query, sort_query, now, stime,
				stime, snap, cid);

		
		//echo(sql);
		try {
			insert(sql);
			// echo("id:"+id);
			TipMessage(url, _CLang("ok_save"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//TipMessage(url, e.getMessage());
		}
	}

	private int getLastTime(String col) {
		mgdb.DBEnd();
		int ltime = 0;
		String field = "{\"ctime\":1}";
		mgdb.SetCollection(col);
		mgdb.JsonColumn(field);
		mgdb.JsonSort(field);
		mgdb.setLimit(1);
		boolean s = mgdb.FetchList();
		if (s) {
			List<Map<String, Object>> res = mgdb.GetValue();
			if(res!=null){
				ltime = (int) res.get(0).get("ctime");
			}

		}

		return ltime;
	}

	@Override
	public void edit(Object arg) {
		// TODO Auto-generated method stub
		String id = porg.getKey("id");
		int eid = 0;
		if (id != null && id.matches("[0-9]+")) {
			eid = Integer.valueOf(id);
		}
		//echo("where:" + where_query);

		if (db_collection == null || db_collection.length() == 0) {
			String format = "select * from " + DB_HOR_PRE
					+ "mongodbrule  where id=%d limit 1";
			String sql = String.format(format, eid);
			Map<String, Object> res;

			res = FetchOne(sql);
			if (res != null) {

				db_collection = res.get("collention").toString();
				mgdb.SetCollection(db_collection);
				setRoot("def_collention", db_collection);
				String fq = res.get("qaction").toString();
				if (fq != null && fq.matches("[0-9]+")) {
					fetch_query = Integer.valueOf(fq);
				}
				where_query = res.get("query").toString();
				field_query = res.get("field").toString();
				sort_query = res.get("sort").toString();
				project_name = res.get("name").toString();
				
				setRoot("snap_id", res.get("snap").toString());
				setRoot("cid", res.get("cid").toString());
			}
		}

		
		setRoot("project_name", project_name);

		if (where_query.length() < 3) {
			setRoot("where_query", "{}");
		} else {
			setRoot("where_query", where_query);
		}

		if (field_query.length() < 3) {
			setRoot("field_query", "{}");
		} else {
			setRoot("field_query", field_query);
		}

		FetchRule();

		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("action_url", ucl.edit(SliceName(stdClass)) + "?id=" + eid
				+ "&project_name=" + project_name);
		setRoot("create_url", ucl.create("mongo_db_edit_action"));
		setRoot("eid", eid);
	}

	@Override
	public void remove(Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void search(Object arg) {
		// TODO Auto-generated method stub

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

		FetchRule();

		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("action_url", ucl.search(SliceName(stdClass)));

		setRoot("create_url", ucl.create(SliceName(stdClass)));

	}

	private void FetchRule() {

		if (fetch_query == 0) {
			SortQuery();
			Find();
		} else if (fetch_query == 2) {
			Distinct();
		} else if (fetch_query == 3) {
			FetchCount();
		}
		setRoot("save_project", "1");
	}

	private void CollectionList() {
		
		Set<String> clist = null;
		try{
			clist = mgdb.CollectionList();
		}catch (MongoException e) {
			// TODO: handle exception
		}
		if(clist==null)return;
		if(clist.size()<1) return;  
		String clists = "";
		String def_col = "";
		for (String s : clist) {
			if (s.equals("system.indexes") || s.equals("system.profile"))
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
		String error = "";
		mgdb.setLimit(10);

		if (where_query.length() > 2 && where_query.substring(0, 1).equals("{")) {
			try {
				mgdb.JsonWhere(where_query);
			} catch (Exception e) {
				// TODO: handle exception
				error = e.getMessage();
			}

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
				error = mgdb.getErrorMsg();
			}

		} else {
			error = mgdb.getErrorMsg();
		}
		if (error.length() > 1) {
			setRoot("error", error);
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
	
	private void listPid() {

		int cid = toInt(porg.getKey("cid"));
		setRoot("cid", cid);

		String sql = "select id,name from " + DB_HOR_PRE
				+ "classify order by id desc";
		List<Map<String, Object>> res;

		try {
			res = FetchAll(sql);
			if (res != null) {
				setRoot("pid_list", res);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
