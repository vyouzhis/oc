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
	private Map<String, Map<String, Map<String, String>>> Mongo;
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
		getMongoDBList(3, "webSite");
		getMongoDBList(2, "webDistinct");
		UserSQl();
		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("json_url", ucl.read("EchartsJson"));
		cardioid();
	}
	
	private void getMongoDBList(int qaction, String RootName) {
		String sql = "SELECT id,name FROM "+DB_HOR_PRE+"mongodbrule where qaction="+qaction+" and snap=0 order by id desc;";
		Mongo = new HashMap<String, Map<String,Map<String,String>>>();
		
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
		String sql = "select id,sql,name,sql_type,sqltmp from "+DB_HOR_PRE+"usersql where input_data=0";
		List<Map<String, Object>> res;
		
		try {
			res = FetchAll(sql);
			if(res!=null){
				SetTree(res, "UserSQl", 4);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void SetTree(List<Map<String, Object>> res, String RootName, int qaction) {
		Map<String, Map<String,String>> file = new HashMap<>();
		for (Map<String, Object> map : res) {
			Map<String, String> Item = new HashMap<>();
			Item.put("name", map.get("name").toString());
			Item.put("type", "item");
			Item.put("id",  map.get("id").toString());
			Item.put("qaction",  qaction+"");	
			if(map.containsKey("sql_type")){
				Item.put("sql_type", map.get("sql_type").toString());
			}
			if(map.containsKey("sqltmp")){
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
