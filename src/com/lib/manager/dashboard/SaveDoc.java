package com.lib.manager.dashboard;

import java.sql.SQLException;
import java.util.List;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.io.ProjectPath;

public class SaveDoc extends Permission implements BasePerminterface {
	private List<String> rmc;

	public SaveDoc() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		setRoot("name", _MLang("name"));
		InAction(); // 设置只是动作
		setAjax(true); // 设置是 ajax
		isAutoHtml = false; // 不用加载页头和页脚
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
		case "create":
			create(null);
			break;
		default:
			Msg(_CLang("error_role"));
			return;
		}
	}

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		String doc = porg.getKey("doc");
		String title = porg.getKey("title");

		if (doc == null || title == null) {
			super.setHtml(_CLang("error_null"));
			return;
		}
		
		String format = "insert into " + DB_HOR_PRE
				+ "doc(title,doc,ctime)VALUES('%s','%s', %d)";
		String sql = String.format(format, title, doc, time());

		try {
			insert(sql);
			super.setHtml(_CLang("ok_save"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			super.setHtml(_CLang("error_save"));
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
