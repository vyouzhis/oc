package com.lib.manager.datasource;

import java.sql.SQLException;
import java.util.List;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;

public class mongo_db_edit_action extends Permission implements BasePerminterface{
	private List<String> rmc;
	
	private String db_collection;
	private int fetch_query = 0;	
	private String where_query;
	private String field_query;
	private String sort_query;
	private String project_name;
	
	public mongo_db_edit_action() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		InAction();
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
		
		case "create":
			create(null);
			return;		
		case "remove":
			remove(null);
			return;
		default:
			Msg(_CLang("error_role"));
			return;
		}
	}
	
	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		db_collection = porg.getKey("db_collection");
		String fq = porg.getKey("fetch_query");
		if (fq!= null && fq.matches("[0-9]+")) {
			fetch_query = Integer.valueOf(fq);
		}
		where_query = porg.getKey("where_query");
		field_query = porg.getKey("field_query");
		sort_query = porg.getKey("sort_query");
		project_name = porg.getKey("project_name");
		
		if(project_name!=null){
			setRoot("project_name", project_name);
		}
		where_query = escapeHtml(where_query);

		field_query = escapeHtml(field_query);
		sort_query = escapeHtml(sort_query);
		
		int snap_id = Integer.valueOf(porg.getKey("snap_id"));
		
		String eid = porg.getKey("eid");
		int id = 0;
		if(eid!=null && eid.matches("[0-9]+")){
			id = Integer.valueOf(eid);
		}
		
		String format = "UPDATE "+DB_HOR_PRE+"mongodbrule SET " +
				" name='%s', collention='%s', qaction='%s', query='%s', field='%s', sort='%s', snap=%d" +
				" WHERE id=%d;";
		String sql = String.format(format, project_name, db_collection, fetch_query, where_query, field_query, sort_query,snap_id , id);
		
		UrlClassList ucl = UrlClassList.getInstance();
		try {
			update(sql);
			TipMessage(ucl.read("mongo_purge_list"), _CLang("ok_remove"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			echo(sql);
			TipMessage(ucl.read("mongo_purge_list"), _CLang("error_save"));
		}
		
	}

	@Override
	public void edit(Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(Object arg) {
		// TODO Auto-generated method stub
		String eid = porg.getKey("id");
		int id = 0;
		if(eid!=null && eid.matches("[0-9]+")){
			id = Integer.valueOf(eid);
		}
		
		String format = "UPDATE "+DB_HOR_PRE+"mongodbrule SET " +
				" istop = 1" +
				" WHERE id=%d;";
		String sql = String.format(format,  id);
		UrlClassList ucl = UrlClassList.getInstance();
		try {
			update(sql);
			TipMessage(ucl.read("mongo_purge_list"), _CLang("ok_save"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			echo(sql);
			TipMessage(ucl.read("mongo_purge_list"), _CLang("error_save"));
		}
	}

	@Override
	public void search(Object arg) {
		// TODO Auto-generated method stub
		
	}
	
}
