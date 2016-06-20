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
	private int lid;
	private String pname = "";
	private final String RootSQL = "select id,name from "+DB_HOR_PRE+"classify where pid=%d and "
			+ UserPermi() + " order by id";

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

		
		 Map<String, Map<String, Object>> TreeObject = null;
		
		lid = toInt(porg.getKey("id"));
		if (lid == 0 || porg.getKey("id").toString().substring(0,1).equals("t")) {
			lid = toInt(porg.getKey("id").toString().substring(1));
			TreeObject = tmpSQL();			
		} else if (lid > 0) {
			TreeObject = subTreeRoot();
		} else {
			TreeObject = DefaultRoot();
		}

		String treeObjectJson = JSON.toJSONString(TreeObject);
		return treeObjectJson;

	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Object>> subTreeRoot() {
		Map<String, Map<String, Object>> TreeObject = new HashMap<>();
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
		return TreeObject;
	}
	
	private List<Map<String, Object>> ItemTree() {
		List<Map<String, Object>> RootRes = null, tRes;
		
		List<String> formats = new ArrayList<>();
		formats.add("SELECT id,name,cid,qaction FROM "
				+ DB_HOR_PRE
				+ "mongodbrule where qaction in (2,3) and snap=0 and cid="+lid+" and "
				+ UserPermi() + " order by id desc;");
		formats.add("select id,usql,name,sql_type,sqltmp,cid from " + DB_HOR_PRE
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
	

	private Map<String, Map<String, Object>> DefaultRoot() {
		Map<String, Map<String, Object>> TreeObject = new HashMap<>();
		
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
		return TreeObject;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Object>> tmpSQL() {
		Map<String, Map<String, Object>> TreeObject = new HashMap<>();
		int cid=lid;
		if(lid==0) cid=1;
		Map<String, Object> SubTree, FloderTree;
		Map<String, Object> RootTree;
		String sql = String.format(RootSQL, cid);
		List<Map<String, Object>> RRes;
		FloderTree = new HashMap<String, Object>();
		
		List<Map<String, Object>> RootRes;
		RootRes = TmpItemTree();
		FloderTree = SetTree(RootRes, lid, pname);
		RootTree = (Map<String, Object>) FloderTree.get("children");
		try {
			RRes = FetchAll(sql);
			for (Map<String, Object> map : RRes) {
				SubTree = new HashMap<>();
				SubTree.put("name", map.get("name"));
				SubTree.put("type", "folder");
				SubTree.put("id", "t"+map.get("id"));

				RootTree.put(map.get("name").toString(), SubTree);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TreeObject.put("additionalParameters", FloderTree);
		return TreeObject;
	}
	
	private List<Map<String, Object>> TmpItemTree() {
		String RootSql;
		List<Map<String, Object>> tRes;
		//Map<String, Object> SubTree;
		RootSql = "select id,name,sqltmp,'6' as qaction from " + DB_HOR_PRE
				+ "usersql where sql_type=1 and cid="+lid+" and " + UserPermi()
				+ " order by id desc;";
		try {
			tRes = FetchAll(RootSql);
			return tRes;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
