package com.lib.manager.analysis;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;
import org.ppl.io.ProjectPath;
import org.ppl.plug.R.Rlan;
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
		case "create":
			create(null);
			return;

		default:
			Msg(_CLang("error_role"));
			return;
		}
	}

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub

		String RJson = porg.getKey("query");
		int listPack = toInt(porg.getKey("listPack"));

		REXP r = null;

		RListJson = new ArrayList<>();
		Rlan rcoonnect = Rlan.getInstance();
		try {
			if (listPack == 1) {

				rcoonnect
						.connection()
						.voidEval(
								"ip <- as.data.frame(installed.packages()[,c(1,3:4)]) ");
				rcoonnect.connection().voidEval("rownames(ip) <- NULL");
				rcoonnect.connection().voidEval(
						"ip <- ip[is.na(ip$Priority),1:2,drop=FALSE]");
				r = rcoonnect.connection().eval("print(ip, row.names=FALSE)");

			} else {
				if (RJson == null || RJson.length() == 0)
					return;
				ProjectPath pp = ProjectPath.getInstance();
				URI uri = pp.DataDir();
				String path = uri.getPath();

				if (System.getProperty("file.separator").equals("\\")) {
					path = path.substring(1);
				}
				String setwd = String.format("setwd('%s')", path);

				rcoonnect.connection().voidEval(setwd);

				r = rcoonnect.connection().eval(RJson);

			}
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (r != null) {
			SelectREXP(r._attr(), "attr");
			SelectREXP(r, "val");
			echo("end");
		}
		rcoonnect.close();
		super.setHtml(JSON.toJSONString(RListJson));
	}

	private void SelectREXP(REXP r, String key) {
		// REXP r = c.eval("print(df)");
		if (r == null)
			return;
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
				if (key == null || key.length() == 0) {
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
				String nkey = "";
				for (int i = 0; i < lanList.size(); i++) {
					nkey = lanList.keyAt(i);
					if (nkey == null)
						nkey = key;
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
					if (nkey == null)
						nkey = key;
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

				if (dim == null || dim.length == 1) {

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
		UrlClassList ucl = UrlClassList.getInstance();
		Map<String, byte[]> file = porg.getUpload_string();
		// String name = porg.getKey("attachment");
		Map<String, String> fileName = porg.getUpload_name();
		ProjectPath pp = ProjectPath.getInstance();
		String dir = "rexcel";
		String path = "";
		boolean bool = false;
		if (pp.isDir(dir) == false) {
			bool = pp.mkDir(dir);
			if (bool == false) {
				TipMessage(ucl.read("Rlang"), _CLang("error_nothing"));
			}
		}

		for (String key : file.keySet()) {
			echo(fileName.get(key));
			path = dir + "/" + time() + "." + fileName.get(key).split("\\.")[1];

			Save(fileName.get(key), path);

			pp.SaveFile(path, file.get(key), false);
		}

		TipMessage(ucl.read("Rlang"), _CLang("ok_save"));
	}

	private void Save(String title, String path) {
		int now = time();
		String format = " insert INTO "
				+ DB_HOR_PRE
				+ "rexcel ( title,path,uid,isshare, ctime,etime)values('%s', '%s', %d,%d, %d,%d);";
		String sql = String.format(format, title, path, aclGetUid(), 0, now,
				now);

		try {
			insert(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
