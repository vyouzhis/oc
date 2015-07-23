package com.lib.manager.datasource;

import java.sql.SQLException;
import java.util.List;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;

public class private_table_action extends Permission implements
		BasePerminterface {
	private List<String> rmc;
	private UrlClassList ucl = UrlClassList.getInstance();

	public private_table_action() {
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
		setRoot("new_table_url", ucl.create(SliceName(stdClass)));
	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		int count = toInt(porg.getKey("count"));
		if (count == 0) {			
			TipMessage(ucl.read("private_table_list"), _CLang("error_null"));
			return;
		}

		String formatc = "CREATE TABLE c_%s (%s)";
		String format = "%s %s NOT NULL DEFAULT '%s' ";
		String formatxt = "%s %s NOT NULL ";

		String sub = "";

		for (int i = 1; i < count; i++) {

			if (porg.getKey("column_type_" + i).toString().equals("TEXT")) {
				sub += String
						.format(formatxt, porg.getKey("field_" + i).toString(),
								porg.getKey("column_type_" + i).toString());
			} else {
				sub += String.format(format, porg.getKey("field_" + i)
						.toString(),
						porg.getKey("column_type_" + i).toString(), porg
								.getKey("default_" + i).toString());
			}
			if (i != count - 1) {
				sub += ",";
			}
		}
		String sql = String.format(formatc, porg.getKey("table_name"), sub);

		try {
			dbcreate(sql);
			TipMessage(ucl.read("private_table_list"), _CLang("ok_save"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			TipMessage(ucl.read("private_table_list"), e.getMessage());

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
