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

		
		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("json_url", ucl.read("EchartsJson"));
		setRoot("table_url", ucl.search("EchartsJson"));
		setRoot("saveimg_url", ucl.create("SaveImg"));
		setRoot("searchimg_url", ucl.search("SaveImg"));
		setRoot("doc_url", ucl.create("SaveDoc"));
		setRoot("listSQL_url", ucl.read("ListSQL"));
		cardioid();
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
