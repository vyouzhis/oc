package com.lib.manager.analysis;

import java.util.List;

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
		String sql = porg.getKey("sql_script");
		if(sql!=null){
			echo(Myreplace(sql));
			setRoot("sql_edit", "\r\n"+sql.replace("&apos;", "\'"));
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
	
	private String Myreplace(String old) {
		if (old == null)
			return "";

		String news = old.replace("&nbsp;", "");
		news = news.replace("&quot;", "\"");
		news = news.replace("&apos", "\'");

		return news;
	}

}
