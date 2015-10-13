package com.lib.manager.dashboard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;

import com.alibaba.fastjson.JSON;

public class ListSQL extends Permission implements BasePerminterface {
	private List<String> rmc;
	private Map<String, Map<String, Object>> TreeObject;
	private int lid;
	// private Map<String, Map<String, Map<String, String>>> Mongo;

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
		lid = toInt(porg.getKey("id"));
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
		int n = time();
		String json = ListMainClassify();
		
		super.setHtml(json);
	}
	
	@SuppressWarnings("unchecked")
	private String ListMainClassify() {
		String sql = "select id,name,pid,(select name from " + DB_HOR_PRE
				+ "classify h where h.id=c.pid ) as pname from " + DB_HOR_PRE
				+ "classify c where displays=0 and "+UserPermi()+" order by pid,id";

		List<Map<String, Object>> res;

		TreeObject = new HashMap<>();
		
		Map<String, Object> SubTree;
		List<Map<String, Object>> RootRes = null, tRes;
		String RootSql = null;
		List<String> formats = new ArrayList<>();
		formats.add("SELECT id,name,cid,qaction FROM "
				+ DB_HOR_PRE
				+ "mongodbrule where qaction in (2,3) and snap=0 and cid=%s and "+UserPermi()+" order by id desc;");
		formats.add("select id,sql,name,sql_type,sqltmp,cid from " + DB_HOR_PRE
				+ "usersql where sql_type=0 and input_data=0 and "+UserPermi()+" and cid=%s");
		formats.add("select s.id,s.name,u.cid from "
				+ DB_HOR_PRE
				+ "sqltmp s, "
				+ DB_HOR_PRE
				+ "usersql u where u.id=s.sid  and (u.uid = "+aclGetUid() +" or u.isshare=1) and u.cid= %s order by s.id desc");
		/*
		try {			
			res = FetchAll(sql);
			if (res != null) {
				for (Map<String, Object> map : res) {

					for (String tsql : formats) {
						RootSql = String.format(tsql, map.get("id").toString());
						try {
							tRes = FetchAll(RootSql);
							if (RootRes != null) {
								if (tRes != null) {
									RootRes.addAll(tRes);
								}
							} else {
								RootRes = tRes;
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (toInt(map.get("pid")) == 1) {
						SubTree = new HashMap<>();
						SubTree.put("additionalParameters", SetTree(RootRes, toInt(map.get("id"))));
						SubTree.put("name", map.get("name").toString());
						SubTree.put("type", "folder");
						TreeObject.put(map.get("name").toString(), SubTree);
						
					} else {
						Map<String, Object> Item = new HashMap<>();
						Item.put("type", "folder");
						Item.put("name", map.get("name"));
						Item.put("additionalParameters", SetTree(RootRes, toInt(map.get("id"))));
						
						Map<String, Map<String, Object>> file = new HashMap<>();
												
						file = (Map<String, Map<String, Object>>) TreeObject.get(map.get("pname").toString()).get("additionalParameters");
						file.get("children").put(map.get("name").toString(), Item);
						
					}
					RootRes = null;

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(lid==0){
			tmpSQL();
		}else {
			SubTree = new HashMap<>();
			SubTree.put("name", _MLang("tmp"));
			SubTree.put("type", "folder");
			
			List<Map<String, Object>>  tRest = new ArrayList<>();
			SubTree.put("additionalParameters", SetTree(tRest, 0));

			TreeObject.put(_MLang("tmp"), SubTree);
		}
		*/
		
		if(lid== 0){
			String json=tmpSQL();
			return json;
		}
		else if (lid >0) {
				
		}
		else{
			defaultRoot();			
		}
		
		String treeObjectJson = JSON.toJSONString(TreeObject);
		return treeObjectJson;

	}
	
	private void subTree() {
		
	}
	
	private void defaultRoot() {
		Map<String, Object> SubTree;
		
		String sql = "select id,name from hor_classify where pid=1 and "+UserPermi()+" order by id";
		List<Map<String, Object>>  RRes;
		try {
			RRes = FetchAll(sql);
			for (Map<String, Object> map:RRes) {
				SubTree = new HashMap<>();
				SubTree.put("name", map.get("name"));
				SubTree.put("type", "folder");
				
				List<Map<String, Object>>  tRest = new ArrayList<>();
				SubTree.put("additionalParameters", SetTree(tRest, toInt(map.get("id"))));

				TreeObject.put(map.get("name").toString(), SubTree);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SubTree = new HashMap<>();
		SubTree.put("name", _MLang("tmp"));
		SubTree.put("type", "folder");
		
		List<Map<String, Object>>  tRest = new ArrayList<>();
		SubTree.put("additionalParameters", SetTree(tRest, 0));

		TreeObject.put(_MLang("tmp"), SubTree);
	}
	
	private String tmpSQL() {
		String RootSql;
		List<Map<String, Object>>  tRes;
		Map<String, Object> SubTree;
		RootSql = "select id,name,sqltmp,'6' as qaction from " + DB_HOR_PRE
				+ "usersql where sql_type=1 and "+UserPermi()+" order by id desc;";
		try {
			tRes = FetchAll(RootSql);
			if (tRes != null) {

				SubTree = new HashMap<>();
				SubTree.put("name", _MLang("tmp"));
				SubTree.put("type", "folder");
				SubTree.put("additionalParameters", SetTree(tRes, 0));

				return  JSON.toJSONString(SubTree);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, Object> SetTree(
			List<Map<String, Object>> res, int id) {
		Map<String, Map<String, String>> file = new HashMap<>();

		for (Map<String, Object> map : res) {
			Map<String, String> Item = new HashMap<>();
			Item.put("type", "item");

			Item.put("id", map.get("id").toString());
			Item.put("name", map.get("name").toString());
			if (map.containsKey("qaction")) {
				Item.put("qaction", map.get("qaction").toString());
			} else if (map.containsKey("sql_type")) {
				Item.put("qaction", "4");
			} else {
				Item.put("qaction", "5");
			}

			if (map.containsKey("sql_type")) {
				Item.put("sql_type", map.get("sql_type").toString());
			}
			if (map.containsKey("sqltmp")) {
				Item.put("sqltmp", map.get("sqltmp").toString());
			}

			file.put(map.get("id").toString(), Item);
		}

		Map<String, Object> Mongo;
		Mongo = new HashMap<String, Object>();

		Mongo.put("children", file);
		Mongo.put("id", id);
		return Mongo;
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
