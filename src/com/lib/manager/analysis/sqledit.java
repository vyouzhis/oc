package com.lib.manager.analysis;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;

public class sqledit extends Permission implements BasePerminterface {
	private List<String> rmc;
	
	public sqledit() {
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
		// TODO Auto-generated method stub
		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("action_url", ucl.read(SliceName(stdClass)));
		setRoot("create_url", ucl.create(SliceName(stdClass)));
		
		String sql = porg.getKey("sql_script");
									
		if(sql!=null){			
			setRoot("sql_edit", "\r\n"+sql.replace("&apos;", "\'"));
			sql = Myreplace(sql);
			
			setRoot("create_sql", unescapeHtml(sql));
			SqlView(sql);
					
		}
	}

	private void SqlView(String o) {
		String sql = o;
		List<Map<String, Object>> res;
		
		if(sql.toLowerCase().matches("(.*)limit(.*)") == false){
			sql += " LIMIT 20";
		}
		
		try {
			res = FetchAll(sql);
			if(res!=null){
				Set<String> key = res.get(0).keySet();
				
				setRoot("Key_Title", key);
				setRoot("List_Data", res);
								
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
			setRoot("ErrorMsg", e.getMessage().toString());
		}
	}
		
	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		String name = porg.getKey("name");
		String usql = porg.getKey("sql");
		
		if(name==null || usql==null)return;
		usql = usql.replace("\r", " ");
		usql = usql.replace("\t", " ");
		usql = usql.replace("\n", " ");
		
		String format = " insert INTO "+DB_HOR_PRE+"usersql (name,sql)values('%s','%s');";
		String sql = String.format(format, name, usql);
		
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
	
	private String Myreplace(String old) {
		if (old == null)
			return "";

		String news = old.replace("&nbsp;", "");
		news = news.replace("&quot;", "\"");
		news = news.replace("&apos;", "\'");
		news = news.replace(";", "");
		news = news.replace("'", "\'");
		
		return news;
	}
	
	private String unescapeHtml(String old) {
		if (old == null)
			return "";

		String news = old.replace("\"", "&quot;");		
		news = news.replace("\'", "&apos;");		
		news = news.replace("\r", " ");
		news = news.replace("\t", " ");
		news = news.replace("\n", " ");
		
		return news;
		
	}

}
