package com.lib.thread;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BaseRapidThread;
import org.ppl.db.UserCoreDB;
import org.ppl.io.DesEncrypter;

public class updateSQLView extends BaseRapidThread{
	private Map<String, Object> mail = null;
	@Override
	public void Run() {
		// TODO Auto-generated method stub
		if(mail == null)return;
		
		List<Map<String, Object>> lres;
		lres = CustomDB(mail.get("sql").toString(), toInt(mail.get("id")));
		
		if(lres!=null){
			String field = "";
			String values = "";
			String view_field = "";
			int m=0;
			for (Map<String, Object> map : lres) {
				for (String key:map.keySet()) {
					view_field += "act_v" + Integer.toHexString(m)
							+ " AS " + key + ", ";
					field += "act_v" + Integer.toHexString(m) + ", ";
					
					values += "(";
					//values += "'" + s.trim() + "',";
					//values += rule+"),"; 
				}
				
			}
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
	
	private List<Map<String, Object>> CustomDB(String sql, int id) {
		List<Map<String, Object>> res = null;
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
			
		} else {
			try {
				res = ucdb.FetchAll(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				
			}

			ucdb.DBEnd();
		}
		return res;
	}

}
