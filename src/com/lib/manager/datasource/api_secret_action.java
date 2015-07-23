package com.lib.manager.datasource;

import java.sql.SQLException;
import java.util.List;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;
import org.ppl.io.Encrypt;

public class api_secret_action extends Permission implements BasePerminterface  {
	private List<String> rmc;
	private UrlClassList ucl = UrlClassList.getInstance();
	
	public api_secret_action() {
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

		super.View();
	}
	
	
	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		setRoot("new_api_url", ucl.create(SliceName(stdClass)));
		Encrypt en  = Encrypt.getInstance();
		setRoot("secret", en.MD5(time()+""));
	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		String title = porg.getKey("title");
		String desc = porg.getKey("desc");
		String secret = porg.getKey("secret");
		String username = porg.getKey("username");
		String password = porg.getKey("password");
		if (title == null || desc == null || secret == null|| username == null || password == null) {
			TipMessage(ucl.read("private_table_list"), _CLang("error_null"));
			return;
		}
		Encrypt en = Encrypt.getInstance();		
		password = en.MD5(password); 
		
		String format = "INSERT INTO "+DB_HOR_PRE+"apisecret (title,idesc,username,passwd,secret, ctime)values('%s','%s','%s','%s','%s',%d);";
		String sql = String.format(format, title,desc, username, password,secret, time());
		echo(sql);
		try {
			insert(sql);
			TipMessage(ucl.read("api_secret_list"), _CLang("ok_save"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			TipMessage(ucl.read("api_secret_list"), getErrorMsg());
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
