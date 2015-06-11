package com.lib.manager.datasource;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
		case "edit":
			edit(null);
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
		int id = toInt(porg.getKey("id"));

		if (id == 0) {
			setRoot("action_url", ucl.create(SliceName(stdClass)));
			setRoot("dcname", "");
		} else {

			String format = "select  title,dcname,url as urls_name,username from "
					+ DB_HOR_PRE + "dbsource where id=%d";
			String sql = String.format(format, id);

			Map<String, Object> res = FetchOne(sql);

			if (res != null) {
				echo(res);
				setRoot("action_url", ucl.edit(SliceName(stdClass)) + "?id="
						+ id);
				setRoot("title", res.get("title").toString());
				setRoot("url", res.get("urls_name").toString());
				setRoot("username", res.get("username").toString());
				setRoot("dcname", res.get("dcname").toString());
			} else {
				setRoot("action_url", ucl.create(SliceName(stdClass)));
				setRoot("dcname", "");
			}
		}

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

		String format = "insert INTO " + DB_HOR_PRE + "dbsource "
				+ "(title ,username,password,url,dcname,ctime)"
				+ "values('%s','%s','%s','%s','%s', %d);";
		String sql = String.format(format, title, username, password, url,
				dcname, time());

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
		int id = toInt(porg.getKey("id"));

		if (id != 0) {
			String title = porg.getKey("title");
			String url = porg.getKey("url");
			String username = porg.getKey("username");
			String password = porg.getKey("password");
			String dcname = porg.getKey("dcname");

			if (title == null || url == null || username == null
					|| dcname == null) {
				TipMessage(ucl.create(SliceName(stdClass)),
						_CLang("error_null"));
				return;
			}

			String ext = " ";
			if (password != null && password.length() > 0) {
				try {
					DesEncrypter de = new DesEncrypter();
					password = de.encrypt(password);
					ext = " ,passwd='" + password + "'";
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			String format = " update "
					+ DB_HOR_PRE
					+ "dbsource SET title='%s', url='%s',username='%s', dcname='%s' %s WHERE id=%d";
			String sql = String.format(format, title, url, username, dcname,
					ext, id);

			try {
				update(sql);
				TipMessage(ucl.read("External_list"), _CLang("ok_save"));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				TipMessage(ucl.create(SliceName(stdClass)), e.getMessage());
			}
		} else {
			TipMessage(ucl.read(SliceName(stdClass)) + "?id=" + id,
					_CLang("error_null"));
		}
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
