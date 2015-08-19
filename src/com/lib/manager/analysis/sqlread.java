package com.lib.manager.analysis;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.common.Page;
import org.ppl.db.UserCoreDB;
import org.ppl.etc.UrlClassList;
import org.ppl.io.DesEncrypter;

public class sqlread extends Permission implements BasePerminterface {
	private List<String> rmc;
	private int Limit = 10;
	private int page = 0;

	public sqlread() {
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

		if (porg.getKey("p")!=null && porg.getKey("p").matches("\\d+")) {
			page = Integer.parseInt(porg.getKey("p"));
		}
		
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

	@Override
	public void read(Object arg) {
		
        int offset=0;	
		if(page!=0){
			offset = (page-1)*Limit;
		}
		
		String format = "select * from "+DB_HOR_PRE+"usersql order by id desc  limit %d offset %d";
		String sql = String.format(format, Limit, offset);
		
		List<Map<String, Object>> res;
		
		try {
			res = FetchAll(sql);
			setRoot("csv_list", res);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SetPage();		
			
	}
	
	private void SetPage() {
		Page p = new Page();
		UrlClassList ucl = UrlClassList.getInstance();

		int t = Tol();
		
		String page_html = p.s_page(ucl.read(SliceName(stdClass)), t, page,
				Limit, "");
		
		setRoot("Page", page_html);
		setRoot("edit_url", ucl.read("sqledit"));
		//setRoot("remove_url", ucl.remove("mongo_db_edit_action"));
		//setRoot("new_csv_url", ucl.read("csvDb"));
	}
	
	private int Tol() {
		String sql ="select count(*) as count from "+DB_HOR_PRE+"usersql limit 1";
		Map<String, Object> res;
		res = FetchOne(sql);
		if(res!=null)return Integer.valueOf( res.get("count").toString());
		return 0;
	}

	private void SqlView(String o) {
		String sql = o;
		List<Map<String, Object>> res;

		sql = sql.replace("\r", " ");
		sql = sql.replace("\t", " ");
		sql = sql.replace("\n", " ");

		// echo(sql.toLowerCase().matches("(.*)limit(.*)"));

		if (sql.toLowerCase().matches("(.*)limit(.*)") == false) {
			sql += " LIMIT 20";
		}
		int dbid = 0;
		if (porg.getKey("dbid") != null
				&& porg.getKey("dbid").toString().matches("[0-9]+")) {
			dbid = Integer.valueOf(porg.getKey("dbid"));
		}
		setRoot("dbid", dbid);
		try {
			if (dbid == 0) {
				res = FetchAll(sql);
			} else {
				res = CustomDB(sql, dbid);
				
			}
			if (res != null && res.size()>0) {
				Set<String> key = res.get(0).keySet();
				
				setRoot("Key_Title", key);
				setRoot("List_Data", res);
			}
		} catch (SQLException e) {
			setRoot("ErrorMsg", e.getMessage().toString());
		}
	}

	private List<Map<String, Object>> CustomDB(String sql, int id) {
		List<Map<String, Object>> res = null;
		UserCoreDB ucdb = new UserCoreDB();
		
		
		String format = "select * from "+DB_HOR_PRE+"dbsource where id=%d limit 1";
		String dsql = String.format(format, id);
		
		Map<String, Object> dres ;
		
		dres = FetchOne(dsql);
		if(dres==null)return null;
		
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
				res = ucdb.FetchAll(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				setRoot("ErrorMsg", e.getMessage().toString());
			}

			ucdb.DBEnd();
		}
		return res;
	}

	private void dbList() {
		String sql = "select id,title from "+DB_HOR_PRE+"dbsource order by id desc; ";
		List<Map<String, Object>> res;
		
		
		try {
			res = FetchAll(sql);
			if(res!=null){
				setRoot("dblist", res);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			setRoot("ErrorMsg", e.getMessage().toString());
		}
		
	}
	
	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		String name = porg.getKey("name");
		String usql = porg.getKey("sql");
		int save_id = 0;
		if (porg.getKey("save_id") != null
				&& porg.getKey("save_id").toString().matches("[0-9]+")) {
			save_id = Integer.valueOf(porg.getKey("save_id"));
		}
		
		if (name == null || usql == null)
			return;
		usql = usql.replace("\r", " ");
		usql = usql.replace("\t", " ");
		usql = usql.replace("\n", " ");
		usql = usql.replace("'", "&apos;");
		//echo(usql);
		
		String format = " insert INTO " + DB_HOR_PRE
				+ "usersql (name,sql, dtype)values('%s','%s', %d);";
		String sql = String.format(format, name, usql, save_id);

		try {
			insert(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UrlClassList ucl = UrlClassList.getInstance();
		TipMessage(ucl.read(SliceName(stdClass)), _CLang("ok_save"));
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
