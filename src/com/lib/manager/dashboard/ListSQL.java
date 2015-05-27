package com.lib.manager.dashboard;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;

import com.alibaba.fastjson.JSON;

public class ListSQL extends Permission implements BasePerminterface {
	private List<String> rmc;
	private Map<String, Map<String, Map<String, String>>> Mongo;

	public ListSQL() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		setRoot("name", _MLang("name"));
		InAction(); // 设置只是动作
		setAjax(true); // 设置是 ajax
		isAutoHtml = false; // 不用加载页头和页脚
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
	}

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		getMongoDBList(3, "webSite");
		getMongoDBList(2, "webDistinct");

		String s = "{'status':'OK','data':[{'id':1,'name':'label 1','type':'folder','additionalParameters':  " +
				"   {'id':1,'children':true,'itemSelected':false}},{'id':2,'name':'label 2','type':'item','additionalParameters':     " +
				"   {'id':2,'children':false,'itemSelected':false}},{'id':3,'name':'label 3','type':'item','additionalParameters':   " +
				"   {'id':3,'children':false,'itemSelected':false}}]}";
		
		Random rand = new Random();
		s = "["+rand.nextInt(3)+"]";
		super.setHtml(s);
	}

	private void getMongoDBList(int qaction, String RootName) {
		String sql = "SELECT id,name FROM " + DB_HOR_PRE
				+ "mongodbrule where qaction=" + qaction
				+ " and snap=0 order by id desc;";
		Mongo = new HashMap<String, Map<String, Map<String, String>>>();

		List<Map<String, Object>> res;

		try {
			res = FetchAll(sql);

			SetTree(res, RootName, qaction);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void UserSQl() {
		String sql = "select id,sql,name,sql_type,sqltmp from " + DB_HOR_PRE
				+ "usersql where sql_type=0 and input_data=0";
		List<Map<String, Object>> res;

		try {
			res = FetchAll(sql);
			if (res != null) {
				SetTree(res, "UserSQl", 4);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void TmpSQl() {
		String sql = "select id,name from " + DB_HOR_PRE
				+ "sqltmp order by id desc;";
		List<Map<String, Object>> res;

		try {
			res = FetchAll(sql);
			if (res != null) {
				SetTree(res, "TmpSQl", 5);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void SetTree(List<Map<String, Object>> res, String RootName,
			int qaction) {
		Map<String, Map<String, String>> file = new HashMap<>();
		for (Map<String, Object> map : res) {
			Map<String, String> Item = new HashMap<>();
			Item.put("name", map.get("name").toString());
			Item.put("type", "item");
			Item.put("id", map.get("id").toString());
			Item.put("qaction", qaction + "");
			if (map.containsKey("sql_type")) {
				Item.put("sql_type", map.get("sql_type").toString());
			}
			if (map.containsKey("sqltmp")) {
				Item.put("sqltmp", map.get("sqltmp").toString());
			}

			file.put(map.get("id").toString(), Item);
		}
		Mongo.put("children", file);
		String JsonTree = JSON.toJSONString(Mongo);
		setRoot(RootName, JsonTree);
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
