package com.lib.thread;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ppl.BaseClass.BaseRapidThread;
import org.ppl.db.UserCoreDB;
import org.ppl.io.DesEncrypter;

public class updateSQLView extends BaseRapidThread {
	private Map<String, Object> mail = null;
	private long rule = 0;
	private String view_field = "";
	private boolean isvfield = true;
	
	@Override
	public void Run() {
		// TODO Auto-generated method stub
		if (mail == null)
			return;
		String format = "insert INTO hor_classinfo (title,view_name,ctype)values('%s', '%s', 1)";
		String sql = String.format(format, mail.get("name").toString(), mail
				.get("view").toString());

		try {
			rule = insert(sql, true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CustomDB(mail.get("sql").toString(), toInt(mail.get("dtype")));
		
		format = "CREATE VIEW %s AS SELECT %s FROM " + DB_HOR_PRE
				+ "class WHERE rule=%d";
		
		view_field = clear(view_field);
		
		if(view_field.length()==0)return;
		
		sql = String.format(format, mail
				.get("view").toString(), view_field, rule);
		
		try {
			//echo(sql);
			insert(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isRun() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean Stop() {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void mailbox(Object o) {
		// TODO Auto-generated method stub
		mail = (Map<String, Object>) o;
	}

	private void CustomDB(String sql, int id) {
		List<Map<String, Object>> tmp;
		int Limit = 900;
		int offset = 0;
		String mySQl = escapeHtml(sql);
		UserCoreDB ucdb = new UserCoreDB();

		String format = "select * from " + DB_HOR_PRE
				+ "dbsource where id=%d limit 1";
		String dsql = String.format(format, id);

		Map<String, Object> dres;
		
		dres = FetchOne(dsql);
		if (dres == null)
			return;

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
		//echo("pwd:" + pwd);
		if (mySQl.toLowerCase().matches("(.*)limit(.*)") == false) {
			mySQl = mySQl + " LIMIT " + Limit +" offset ";
		}
		ucdb.setDbPwd(pwd);

		if (ucdb.Init() == false) {
			echo("init error");
		} else {
			//echo(mySQl);
			try {
				while (true) {					
					tmp = ucdb.FetchAll(mySQl+offset);
					if(tmp.size()==0)break;
					saveData(tmp);	
					offset+=Limit;
					echo("offset:"+offset);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ucdb.DBEnd();
		}

	}

	private void saveData(List<Map<String, Object>> lres) {
		if (lres == null)
			return;

		String field = "";
		String values = "";
		String value = "";
		int m = 0;
		boolean f = true;
		for (Map<String, Object> map : lres) {
			values += "(";
			for (String key : map.keySet()) {
				if (f) {
					field += "act_v" + Integer.toHexString(m) + ", ";
				}

				if(isvfield){
					view_field += "act_v" + Integer.toHexString(m) + " AS "
							+ key + ", ";
				}
				m++;
				value = map.get(key).toString();
				value = unescapeHtml(value);
				values += "'" + value + "',";

			}
			values += rule + "),";
			f = false;
			isvfield = false;
		}

		values = values.substring(0, values.length() - 1);
		//echo(values);

		String format = "insert INTO " + DB_HOR_PRE + "class (%s)values %s";
		String sql = String.format(format, field + " rule", values);
		try {
			insert(sql);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String clear(String v) {
		String s = v.trim();
		if (s.length() > 1) {
			return s.substring(0, s.length() - 1);
		} else {
			return v;
		}
	}
	
}
