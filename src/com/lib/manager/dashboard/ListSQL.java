package com.lib.manager.dashboard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;
import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;

import com.alibaba.fastjson.JSON;

public class ListSQL extends Permission implements BasePerminterface {
	private List<String> rmc;
	private Map<String, Map<String, Object>> TreeObject;
	private int lid;
	private String pname = "";
	private String RootSQL = "";

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
		pname = porg.getKey("pname");

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
		String json = ListMainClassify();

		super.setHtml(json);
	}

	private String ListMainClassify() {

		RootSQL = "select id,name from hor_classify where pid=%d and "
				+ UserPermi() + " order by id";
		TreeObject = new HashMap<>();

		if (lid == 0) {
			String json = tmpSQL();
			return json;
		} else if (lid > 0) {
			subTreeRoot();
		} else {
			DefaultRoot();
		}

		String treeObjectJson = JSON.toJSONString(TreeObject);
		return treeObjectJson;

	}

	@SuppressWarnings("unchecked")
	private void subTreeRoot() {
		Map<String, Object> SubTree, FloderTree;
		Map<String, Object> RootTree;
		String sql = String.format(RootSQL, lid);
		List<Map<String, Object>> RRes;
		FloderTree = new HashMap<String, Object>();
		
		List<Map<String, Object>> RootRes;
		RootRes = ItemTree();
		FloderTree = SetTree(RootRes, lid, pname);
		RootTree = (Map<String, Object>) FloderTree.get("children");
		try {
			RRes = FetchAll(sql);
			for (Map<String, Object> map : RRes) {
				SubTree = new HashMap<>();
				SubTree.put("name", map.get("name"));
				SubTree.put("type", "folder");
				SubTree.put("id", map.get("id"));

				RootTree.put(map.get("name").toString(), SubTree);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TreeObject.put("additionalParameters", FloderTree);
		
	}
	
	private List<Map<String, Object>> ItemTree() {
		List<Map<String, Object>> RootRes = null, tRes;
		
		List<String> formats = new ArrayList<>();
		formats.add("SELECT id,name,cid,qaction FROM "
				+ DB_HOR_PRE
				+ "mongodbrule where qaction in (2,3) and snap=0 and cid="+lid+" and "
				+ UserPermi() + " order by id desc;");
		formats.add("select id,sql,name,sql_type,sqltmp,cid from " + DB_HOR_PRE
				+ "usersql where sql_type=0 and input_data=0 and "
				+ UserPermi() + " and cid="+lid);
		formats.add("select s.id,s.name,u.cid from " + DB_HOR_PRE
				+ "sqltmp s, " + DB_HOR_PRE
				+ "usersql u where u.id=s.sid  and (u.uid = " + aclGetUid()
				+ " or u.isshare=1) and u.cid= "+lid+" order by s.id desc");
		
		for (String tsql : formats) {
			
			try {
				//echo(tsql);
				tRes = FetchAll(tsql);
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
		return RootRes;
				
	}
	

	private void DefaultRoot() {
		Map<String, Object> SubTree;

		String sql = String.format(RootSQL, 1);
		List<Map<String, Object>> RRes;
		try {
			RRes = FetchAll(sql);
			for (Map<String, Object> map : RRes) {
				SubTree = new HashMap<>();
				SubTree.put("name", map.get("name"));
				SubTree.put("type", "folder");
				SubTree.put("id", map.get("id"));
				
				List<Map<String, Object>> tRest = new ArrayList<>();
				SubTree.put("additionalParameters",SetTree(tRest, toInt(map.get("id")), map.get("name").toString()));

				TreeObject.put(map.get("name").toString(), SubTree);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SubTree = new HashMap<>();
		SubTree.put("name", _MLang("tmp"));
		SubTree.put("type", "folder");

		List<Map<String, Object>> tRest = new ArrayList<>();
		SubTree.put("additionalParameters", SetTree(tRest, 0, _MLang("tmp")));

		TreeObject.put(_MLang("tmp"), SubTree);
	}

	private String tmpSQL() {
		String RootSql;
		List<Map<String, Object>> tRes;
		Map<String, Object> SubTree;
		RootSql = "select id,name,sqltmp,'6' as qaction from " + DB_HOR_PRE
				+ "usersql where sql_type=1 and " + UserPermi()
				+ " order by id desc;";
		try {
			tRes = FetchAll(RootSql);
			if (tRes != null) {

				SubTree = new HashMap<>();
				SubTree.put("name", _MLang("tmp"));
				SubTree.put("type", "folder");
				SubTree.put("additionalParameters",
						SetTree(tRes, 0, _MLang("tmp")));

				return JSON.toJSONString(SubTree);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Map<String, Object> SetTree(List<Map<String, Object>> res, int id,
			String name) {
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
		Mongo.put("name", name);
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
