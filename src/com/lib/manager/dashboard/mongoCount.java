package com.lib.manager.dashboard;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;

import com.alibaba.fastjson.JSON;

public class mongoCount extends Permission implements BasePerminterface {
	private List<String> rmc;
	private Map<String, Map<String, Map<String, String>>> Mongo;
	public mongoCount() {
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
		getMongoDBList();
		
		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("json_url", ucl.read("mongojson"));
	}
	
	private void getMongoDBList() {
		String sql = "SELECT id,name FROM "+DB_HOR_PRE+"mongodbrule order by id desc;";
		Mongo = new HashMap<String, Map<String,Map<String,String>>>();
		
		List<Map<String, Object>> res;
		
		try {
			res = FetchAll(sql);
			Map<String, Map<String,String>> file = new HashMap<>();
			for (Map<String, Object> map : res) {
				Map<String, String> Item = new HashMap<>();
				Item.put("name", map.get("name").toString());
				Item.put("type", "item");
				Item.put("id",  map.get("id").toString());	
				file.put(map.get("id").toString(), Item);				
			}
			Mongo.put("children", file);
			String JsonTree = JSON.toJSONString(Mongo);
			setRoot("MongoTree", JsonTree);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
