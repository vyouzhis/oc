package com.lib.manager.dashboard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;

import com.alibaba.fastjson.JSON;

public class ListR extends Permission implements BasePerminterface {
	private List<String> rmc;
	private int lid;
	private String pname = "";
	private final String RootSQL = "select id,name from " + DB_HOR_PRE
			+ "classify where pid=%d and " + UserPermi() + " order by id";

	public ListR() {
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

		if (lid > 0) {
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
		Map<String, Object> RootTree = new HashMap<>();
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
		List<Map<String, Object>> RootRes = null;

		String tsql = "SELECT id,title as name, cid from " + DB_HOR_PRE
				+ "rlanguage WHERE ishow=1 and cid=" + lid + " and "
				+ UserPermi() + " order by id desc;";		
		try {
			RootRes = FetchAll(tsql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
				SubTree.put(
						"additionalParameters",
						SetTree(tRest, toInt(map.get("id")), map.get("name")
								.toString()));

				TreeObject.put(map.get("name").toString(), SubTree);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return TreeObject;
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
