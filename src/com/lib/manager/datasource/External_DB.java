package com.lib.manager.datasource;

import java.sql.SQLException;
import java.util.List;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;
import org.ppl.io.DesEncrypter;

public class External_DB extends Permission implements BasePerminterface {
	private List<String> rmc;
	private UrlClassList ucl = UrlClassList.getInstance();

	public External_DB() {
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
		case "search":
			search(null);
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

		setRoot("action_url", ucl.create(SliceName(stdClass)));
		setRoot("dcname", "mysql");
	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		String title = porg.getKey("title");
		String url = porg.getKey("url");
		String username = porg.getKey("username");
		String password = porg.getKey("password");
		String dcname = porg.getKey("dcname");

		if (title == null || url == null || username == null
				|| password == null || dcname == null) {
			TipMessage(ucl.create(SliceName(stdClass)), _CLang("error_null"));
			return;
		}
		
		try {
			DesEncrypter de = new DesEncrypter();
			password = de.encrypt(password);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String format = "insert INTO hor_dbsource " +
				"(title ,username,password,url,dcname,ctime)" +
				"values('%s','%s','%s','%s','%s', %d);";
		String sql = String.format(format, title,username,password,url,dcname, time());
		
		try {
			insert(sql);
			TipMessage(ucl.read("External_list"), _CLang("ok_save"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			TipMessage(ucl.create(SliceName(stdClass)), e.getMessage());
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
