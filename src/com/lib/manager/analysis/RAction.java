package com.lib.manager.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.globale_config;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RFactor;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RserveException;

import com.alibaba.fastjson.JSON;

public class RAction extends Permission implements BasePerminterface {
	private List<String> rmc;
	private List<Map<String, Object>> RListJson;
	private int index = 0;

	public RAction() {
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

		String RJson = porg.getKey("query");
		echo(RJson);
		if (RJson == null || RJson.length() == 0)
			return;

		REXP r = null;

		RListJson = new ArrayList<>();
		try {
			r = globale_config.rcoonnect.eval(RJson);
			SelectREXP(r._attr(), "attr");
			SelectREXP(r, "val");
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		super.setHtml(JSON.toJSONString(RListJson));
	}

	private void SelectREXP(REXP r, String key) {
		// REXP r = c.eval("print(df)");
		if(r==null)return;
		try {
			if (r.isComplex()) {
				echo("isComplex");
			} else if (r.isEnvironment()) {
				echo("isEnvironment");
			} else if (r.isExpression()) {
				echo("isExpression");
			} else if (r.isFactor()) {
				RFactor factors = r.asFactor();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(key, factors.asStrings());
				RListJson.add(map);
				echo("isFactor");
			} else if (r.isInteger()) {
				int[] rint = r.asIntegers();
				Map<String, Object> map = new HashMap<String, Object>();
				if (key==null || key.length() == 0) {
					map.put(index + "", rint);
					index++;
				} else {
					map.put(key, rint);
				}
				SelectREXP(r._attr(), key);
				RListJson.add(map);
				echo("isInteger");
			} else if (r.isLanguage()) {
				RList lanList = r.asList();
				String nkey="";
				for (int i = 0; i < lanList.size(); i++) {
					nkey = lanList.keyAt(i);
					if(nkey == null) nkey = key;					
					SelectREXP(lanList.at(i), nkey);
				}
				echo("isLanguage");
			} else if (r.isList()) {
				echo("isList");
				RList rList;
				String nkey = "";
				rList = r.asList();

				for (int i = 0; i < rList.size(); i++) {
					nkey = rList.keyAt(i);
					if(nkey == null) nkey = key;
					if (nkey.equals("dim"))
						continue;
					SelectREXP(rList.at(i), nkey);
				}
			} else if (r.isLogical()) {
				String[] b = r.asStrings();
				Map<String, Object> map = new HashMap<String, Object>();
				if (key == null || key.length() == 0) {
					map.put(index + "", b);
					index++;
				} else {
					map.put(key, b);
				}

				RListJson.add(map);
				echo("isLogical");
			} else if (r.isNull()) {
				echo("isNull");
			} else if (r.isNumeric()) {
				Map<String, Object> map = new HashMap<String, Object>();

				int[] dim = r.dim();

				if (dim == null || dim.length==1) {

					double[] m = r.asDoubles();

					if (key.length() == 0) {
						map.put(index + "", m);
						index++;
					} else {
						map.put(key, m);
					}

				} else {
					double[][] md = r.asDoubleMatrix();

					if (key == null || key.length() == 0) {
						map.put(index + "", md);
						index++;
					} else {
						map.put(key, md);
					}

				}

				RListJson.add(map);
				echo("isNumeric");
			} else if (r.isPairList()) {
				echo("isPairList");
			} else if (r.isRaw()) {
				echo("isRaw");
			} else if (r.isRecursive()) {
				echo("isRecursive");
			} else if (r.isReference()) {
				echo("isReference");
			} else if (r.isString()) {
				String[] d = r.asStrings();

				Map<String, Object> map = new HashMap<String, Object>();
				if (key == null || key.length() == 0) {
					map.put(index + "", d);
					index++;
				} else {
					map.put(key, d);
				}

				RListJson.add(map);
				echo("isString");
			} else if (r.isSymbol()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(key, r.asString());
				RListJson.add(map);
				echo("isSymbol");
			} else if (r.isVector()) {
				echo("isVector");
			} else {
				echo("else");
				echo(r.isNull());
			}

		} catch (REXPMismatchException e) {
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
