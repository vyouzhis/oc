package com.lib.manager.analysis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.db.UserCoreDB;
import org.ppl.etc.UrlClassList;
import org.ppl.io.DesEncrypter;

import com.alibaba.fastjson.JSON;

public class sqledit extends Permission implements BasePerminterface {
	private List<String> rmc;
	private int dbid = 0;

	public sqledit() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		setRoot("name", _MLang("name"));
		InAction();
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
		ListTip("name", "mongodbrule", "ListRule");
		ListTip("title,view_name", "classinfo", "ListView");

		if (porg.getKey("dbid") != null
				&& porg.getKey("dbid").toString().matches("[0-9]+")) {
			dbid = Integer.valueOf(porg.getKey("dbid"));
		}
		setRoot("dbid", dbid);
		setRoot("sql_type", 0);
		switch (rmc.get(1).toString()) {
		case "read":
			read(null);
			break;
		case "search":
			search(null);
			break;
		case "create":
			create(null);
			return;
		case "edit":
			edit(null);
			break;
		default:
			Msg(_CLang("error_role"));
			return;
		}

		super.View();
	}

	public void read(Object arg) {

		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("action_url", ucl.read(SliceName(stdClass)));
		setRoot("create_url", ucl.create(SliceName(stdClass)));
		dbList();
		String TransferSQL = porg.getKey("sql_script");
		if (TransferSQL == null) {
			TransferSQL = "";
		}
		TransferSQL = TransferSQL.trim();
		String RunSQL = escapeHtml(TransferSQL);
		String HtmlSQL = unescapeHtml(TransferSQL);

		String esql = cookieAct.GetCookie("edit_sql");

		if (TransferSQL.length() > 1) {

			esql = HtmlSQL;
			setRoot("sql_pre", "\r\n" + TransferSQL);
			setRoot("create_sql", HtmlSQL);
			cookieAct.SetCookie("edit_sql", RunSQL);
			
			SqlView(RunSQL);
		}

		if (esql != null) {
			setRoot("sql_edit", esql);
		}

	}

	@SuppressWarnings("unchecked")
	private String tmpSql(String sql) {
		if (sql == null) {
			return null;
		}
		String jsonTmp = porg.getKey("jsontmp");
		if (jsonTmp == null || jsonTmp.length() < 1)
			return sql;
		
		List<List<String>> jsonList = JSON.parseObject(jsonTmp, List.class);
		if (jsonList == null)
			return sql;
		List<String> tList = new ArrayList<>();
		for (List<String> list : jsonList) {
			tList.add(list.get(0));
			sql = sql.replace("@" + list.get(0) + "@", list.get(1));
		}

		setRoot("temples", JSON.toJSONString(tList));
		setRoot("jsonList", jsonList);

		return sql;
	}

	private void SqlView(String o) {
		String sql = o;
		List<Map<String, Object>> res = null;

		if (sql.toLowerCase().matches("(.*)limit(.*)") == false) {
			sql += " LIMIT 20";
		}
		if (porg.getKey("sql_type").equals("1")) {
			sql = tmpSql(sql);
			setRoot("sql_type", porg.getKey("sql_type"));
		}

		if (dbid == 0) {
			try {
				res = FetchAll(sql);
			} catch (SQLException e) {
				setRoot("ErrorMsg", e.getMessage().toString());
				end();
			} catch (ArrayIndexOutOfBoundsException e) {
				// TODO: handle exception
				setRoot("ErrorMsg", "error sql");
			}
		} else {
			res = CustomDB(sql, dbid);

		}
		if (res != null && res.size() > 0) {
			Set<String> key = res.get(0).keySet();

			setRoot("Key_Title", key);
			setRoot("List_Data", res);
		}
	}

	private List<Map<String, Object>> CustomDB(String sql, int id) {
		List<Map<String, Object>> res = new ArrayList<>();
		UserCoreDB ucdb = new UserCoreDB();

		String format = "select * from " + DB_HOR_PRE
				+ "dbsource where id=%d limit 1";
		String dsql = String.format(format, id);

		Map<String, Object> dres;

		dres = FetchOne(dsql);
		if (dres == null)
			return null;

		ucdb.setDriverClassName(dres.get("dcname").toString());
		ucdb.setDbUrl(dres.get("url").toString());
		ucdb.setDbUser(dres.get("username").toString());
		String pwd = dres.get("password").toString();

		try {
			DesEncrypter de = new DesEncrypter();
			pwd = de.decrypt(pwd);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ucdb.setDbPwd(pwd);

		if (ucdb.Init() == false) {
			setRoot("ErrorMsg", ucdb.getErrorMsg());
		} else {
			try {
				List<Map<String, Object>> tmp;
				while (true) {
					tmp = ucdb.FetchAll(sql);
					
					res.addAll(tmp);
					if (ucdb.isFetchFinal())
						break;
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				setRoot("ErrorMsg", e.getMessage().toString());
			}

			ucdb.DBEnd();
		}
		return res;
	}

	private void dbList() {
		String sql = "select id,title from " + DB_HOR_PRE
				+ "dbsource order by id desc; ";
		List<Map<String, Object>> res;

		try {
			res = FetchAll(sql);
			if (res != null) {
				setRoot("dblist", res);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			setRoot("ErrorMsg", e.getMessage().toString());
		}

	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		String name = porg.getKey("name");
		String usql = porg.getKey("sql");
		String jsonTmp = porg.getKey("jsontmp");
		String nview = porg.getKey("nview");

		int is_get_data = toInt(porg.getKey("get_data"));

		int sql_type = toInt(porg.getKey("sql_type"));

		int save_id = toInt(porg.getKey("save_id"));

		if (name == null || usql == null)
			return;

		usql = unescapeHtml(usql);

		if (jsonTmp == null)
			jsonTmp = "";
		if (nview == null)
			nview = "";

		String format = " insert INTO "
				+ DB_HOR_PRE
				+ "usersql (name,sql, dtype, sql_type, sqltmp, input_data, uview)values('%s','%s', %d, %d, '%s', %d, '%s');";
		String sql = String.format(format, name, usql, save_id, sql_type,
				jsonTmp, is_get_data, nview);
		

		UrlClassList ucl = UrlClassList.getInstance();
		String msg = _CLang("ok_save");
		try {
			long id = insert(sql, true);

			if (id > 0 && sql_type == 0 && is_get_data == 1
					&& nview.length() > 0) {
				// 后台运行获取数据
				Map<String, Object> mail = new HashMap<>();
				// mail.put("id", id);
				mail.put("sql", usql);
				mail.put("view", nview);
				mail.put("name", name);
				mail.put("dtype", save_id);
				TellPostMan("updateSQLView", mail);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			msg = e.getMessage();
		}

		TipMessage(ucl.read(SliceName(stdClass)), msg);
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

	private void ListTip(String n, String m, String l) {
		String sql = "select id," + n + " from " + DB_HOR_PRE + m
				+ " order by id desc";

		List<Map<String, Object>> res;
	
		try {
			res = FetchAll(sql);
			if (res != null) {
				setRoot(l, res);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
