package com.lib.manager.dashboard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.common.Escape;
import org.ppl.etc.UrlClassList;
import org.ppl.etc.globale_config;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RserveException;

import com.alibaba.fastjson.JSON;

public class DashboardView extends Permission implements BasePerminterface {
	private List<String> rmc;

	public DashboardView() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
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
		case "edit":
			edit(null);
			break;
		default:
			Msg(_CLang("error_role"));
			return;
		}
		GraphList();

		super.View();
	}

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub

		ListMainClassify();
		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("json_url", ucl.read("EchartsJson"));
		setRoot("table_url", ucl.search("EchartsJson"));
		setRoot("saveimg_url", ucl.create("SaveImg"));
		setRoot("searchimg_url", ucl.search("SaveImg"));
		setRoot("doc_url", ucl.create("SaveDoc"));
		setRoot("listSQL_url", ucl.read("ListSQL"));
		cardioid();
	}

	@SuppressWarnings("unchecked")
	private void ListMainClassify() {
		String sql = "select id,name,pid,(select name from " + DB_HOR_PRE
				+ "classify h where h.id=c.pid ) as pname from " + DB_HOR_PRE
				+ "classify c where displays=0 and "+UserPermi()+" order by pid,id";

		List<Map<String, Object>> res;

		Map<String, Map<String, Object>> TreeObject = new HashMap<>();
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
						SubTree.put("additionalParameters", SetTree(RootRes));
						SubTree.put("name", map.get("name").toString());
						SubTree.put("type", "folder");
						TreeObject.put(map.get("name").toString(), SubTree);
						
					} else {
						Map<String, Object> Item = new HashMap<>();
						Item.put("type", "folder");
						Item.put("name", map.get("name"));
						Item.put("additionalParameters", SetTree(RootRes));
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

		RootSql = "select id,name,sqltmp,'6' as qaction from " + DB_HOR_PRE
				+ "usersql where sql_type=1 and "+UserPermi()+" order by id desc;";
		try {
			tRes = FetchAll(RootSql);
			if (tRes != null) {

				SubTree = new HashMap<>();
				SubTree.put("name", _MLang("tmp"));
				SubTree.put("type", "folder");
				SubTree.put("additionalParameters", SetTree(tRes));

				TreeObject.put(_MLang("tmp"), SubTree);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String treeObjectJson = JSON.toJSONString(TreeObject);
		setRoot("treeObjectJson", treeObjectJson);

	}

	private Map<String, Map<String, Map<String, String>>> SetTree(
			List<Map<String, Object>> res) {
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

		Map<String, Map<String, Map<String, String>>> Mongo;
		Mongo = new HashMap<String, Map<String, Map<String, String>>>();

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
		read(null);
		
		int id = toInt(porg.getKey("id"));
		String format = "select * from "+DB_HOR_PRE+"doc where id=%d and "+UserPermi();
		String sql = String.format(format, id);
		
		Map<String, Object> res;
		
		res = FetchOne(sql);
		setRoot("bootbox_val", res.get("title").toString());
		setRoot("edit_id", id);
		setRoot("cacheEdit", Escape.escape(res.get("doc").toString()));
		
		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("doc_url", ucl.edit("SaveDoc"));
		
	}

	@Override
	public void remove(Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void search(Object arg) {
		// TODO Auto-generated method stub

	}

	private void cardioid() {

		List<Double> xAxis = new ArrayList<>();
		double[] yAxis = null;
		for (double i = 1; i < 51; i++) {
			xAxis.add(i);
		}
				
		try {
			globale_config.rcoonnect.voidEval("mlg<-c(1:51)");
			yAxis = globale_config.rcoonnect.eval("log(mlg)").asDoubles();
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(yAxis != null){
			setRoot("xAxis", JSON.toJSONString(yAxis));
			
			setRoot("yAxis", JSON.toJSONString(xAxis));
			
		}else {
			setRoot("xAxis", "[]");
			setRoot("yAxis", "[]");
		}
	}

	private void cardioidg() {
		// double r[] = new double[20];
		//
		// double theta[] = new double[r.length];
		//
		// for (int k = 0; k < r.length; k++) {
		//
		// theta[k] = Math.PI*k/(r.length-1);
		//
		// r[k] = 0.5 + Math.cos(theta[k]);
		//
		// }
		// double[] x =
		// globale_config.RengineJava.eval("seq(-3,3,.05)").asDoubleArray();
		// double[] y =
		// globale_config.RengineJava.eval("dnorm(seq(-3,3,.05))").asDoubleArray();
		// int i=0;
		// for (double a : y) {
		// y[i]=a*1000;
		// i++;
		// }
		// echo("---");
		// for (int i = 0; i < theta.length; i++) {
		// echo(theta[i]);
		// }
		// echo("---");
		// for (int i = 0; i < r.length; i++) {
		// echo(r[i]);
		// }

		// setRoot("xAxis", JSON.toJSONString(y));
		// setRoot("yAxis", JSON.toJSONString(x));
	}

	@SuppressWarnings("unchecked")
	private void GraphList() {
		List<Map<String, Object>> GL;
		String glJson = mConfig.GetValue("echarts.graph");

		GL = JSON.parseObject(glJson, List.class);
		setRoot("graphList", GL);
	}

}
