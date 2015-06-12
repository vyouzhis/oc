package com.lib.manager.dashboard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;

import com.alibaba.fastjson.JSON;
import com.google.gson.annotations.JsonAdapter;

public class DashboardView extends Permission implements BasePerminterface {
	private List<String> rmc;
	
	public DashboardView() {
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
		
		
//		getMongoDBList(3, "webSite");
//		getMongoDBList(2, "webDistinct");
//		UserSQl();
//		TmpSQl();
//		
		ListMainClassify();
		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("json_url", ucl.read("EchartsJson"));
		setRoot("listSQL_url", ucl.read("ListSQL"));
		cardioid();
	}
	
	private void ListMainClassify() {
		String sql = "select id,name from hor_classify where pid=0 order by id";
		
		List<Map<String, Object>> res;
		
		
//		'webSite' : {
//			name : '网站浏览',
//			type : 'folder',
//			'additionalParameters' : ${webSite!""}
//		},
		Map<String, Map<String, Object>> TreeObject = new HashMap<>();
		Map<String, Object> SubTree;
		List<Map<String, Object>> RootRes=null, tRes;
		String RootSql = null;
		try {
			res = FetchAll(sql);
			if(res!=null){
				for (Map<String, Object> map : res) {
					SubTree = new HashMap<>();
					SubTree.put("name", map.get("name").toString());
					SubTree.put("type", "folder");
					
					
					//TreeObject.put(map.get("name").toString(),)
					
					RootSql = "SELECT id,name,cid,qaction FROM "+DB_HOR_PRE+"mongodbrule where qaction in (2,3) and snap=0 and cid="+map.get("id")+" order by id desc;";
					echo(RootSql);
					try {
						RootRes = FetchAll(RootSql);
												
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					RootSql = "select id,sql,name,sql_type,sqltmp,cid from "+DB_HOR_PRE+"usersql where sql_type=0 and input_data=0 and cid="+map.get("id");
					echo(RootSql);
					try {
						tRes = FetchAll(RootSql);
						if(RootRes != null){
							if(tRes!=null){
								RootRes.addAll(tRes);
							}
						}else {
							RootRes = tRes;
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					RootSql = "select s.id,s.name,u.cid from "+DB_HOR_PRE+"sqltmp s, "+DB_HOR_PRE+"usersql u where u.id=s.sid  and u.cid= "+ map.get("id")+" order by s.id desc";
					echo(RootSql);
					try {
						tRes = FetchAll(RootSql);
						if(RootRes != null){
							if(tRes!=null){
								RootRes.addAll(tRes);
							}
						}else {
							RootRes = tRes;
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					SubTree.put("additionalParameters", SetTree(RootRes));
					TreeObject.put(map.get("name").toString(), SubTree);
				}
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String treeObjectJson = JSON.toJSONString(TreeObject);
		setRoot("treeObjectJson", treeObjectJson);
		
	}
	
	
//	private void getMongoDBList(int qaction, String RootName) {
//		String sql = "SELECT id,name,cid,qaction FROM "+DB_HOR_PRE+"mongodbrule where qaction="+qaction+" and snap=0 order by id desc;";
//		
//		
//		List<Map<String, Object>> res;
//		
//		try {
//			res = FetchAll(sql);
//			
//			SetTree(res, RootName, qaction);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}		
//		
//	}
//	
//	private void UserSQl() {
//		String sql = "select id,sql,name,sql_type,sqltmp,cid from "+DB_HOR_PRE+"usersql where sql_type=0 and input_data=0";
//		List<Map<String, Object>> res;
//		
//		try {
//			res = FetchAll(sql);
//			if(res!=null){
//				SetTree(res, "UserSQl", 4);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	private void TmpSQl() {				
//		String sql = "select s.id,s.name,u.cid from "+DB_HOR_PRE+"sqltmp s, "+DB_HOR_PRE+"usersql u where u.id=s.sid order by s.id desc";
//		
//		List<Map<String, Object>> res;
//		
//		try {
//			res = FetchAll(sql);
//			if(res!=null){
//				SetTree(res, "TmpSQl", 5);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	private Map<String, Map<String, Map<String, String>>> SetTree(List<Map<String, Object>> res) {
		Map<String, Map<String,String>> file = new HashMap<>();
		
		for (Map<String, Object> map : res) {
			Map<String, String> Item = new HashMap<>();
			Item.put("type", "item");
			
			Item.put("id",  map.get("id").toString());
			Item.put("name", map.get("name").toString());
			if(map.containsKey("qaction")){
				Item.put("qaction",  map.get("qaction").toString());
			}else if(map.containsKey("sql_type")){
				Item.put("qaction",  "4");
			}else {
				Item.put("qaction",  "5");
			}
				
			if(map.containsKey("sql_type")){
				Item.put("sql_type", map.get("sql_type").toString());
			}
			if(map.containsKey("sqltmp")){
				Item.put("sqltmp", map.get("sqltmp").toString());
			}
			
			file.put(map.get("id").toString(), Item);				
		}
		
		Map<String, Map<String, Map<String, String>>> Mongo;
		Mongo = new HashMap<String, Map<String,Map<String,String>>>();
		
		Mongo.put("children", file);
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
	
	
	private void cardioid2() {
		int t = 0;
		double a = Math.sin(0.01745 * t) * 0.5 + 16;
		
		double r;
		double px, py;
		int ox= 160;
		int oy = 32;
		t = (t + 12) % 3;
		double rotation = t / 24;
		List<Double> xAxis = new ArrayList<>();
		List<Double> yAxis = new ArrayList<>();
		for (int j = 0; j < 360; j += 4) {
			for (int i = 0; i < 360; i += 4) {
				// Cardioid
				double theta = Math.PI / 180 * (i + rotation);
				r = a * (1 - Math.sin(theta));
				px = r * Math.cos(theta);
				py = r * Math.sin(theta);

				// deco
				double phi = Math.PI / 180 * j;
				px *= phi * Math.sin(phi) * 1.1;
				py *= phi;
				double xx = px + ox;
				double yy = py + oy;
				xAxis.add(xx);
				yAxis.add(yy);
				//echo(xx+" , " + yy);
			}
		}
		
		
	}
	
	private void cardioid() {
		 double r[] = new double[20];

	        double theta[] = new double[r.length];

	        for (int k = 0;  k < r.length;  k++) {

	            theta[k] = Math.PI*k/(r.length-1);

	            r[k] = 0.5 + Math.cos(theta[k]);

	        }
//	        echo("---");
//	        for (int i = 0; i < theta.length; i++) {
//				echo(theta[i]);
//			}
//	        echo("---");
//	        for (int i = 0; i < r.length; i++) {
//				echo(r[i]);
//			}
	        
	        setRoot("xAxis", JSON.toJSONString(r));
			setRoot("yAxis", JSON.toJSONString(theta));
	}

}
