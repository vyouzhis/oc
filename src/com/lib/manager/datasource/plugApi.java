package com.lib.manager.datasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;

public class plugApi extends Permission implements BasePerminterface {
	private List<String> rmc;
	private int Limit = 10;
	private int page = 0;

	public plugApi() {
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

		page = toInt(porg.getKey("p"));

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
		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("runUrl", ucl.create(SliceName(stdClass)));		
	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub

		UrlClassList ucl = UrlClassList.getInstance();
		String pack = porg.getKey("pack");
		
		for (int i = 1; i < 8; i++) {			
			TellPostMan(pack, String.format("%02d", i));
		}
		//TellPostMan(pack, "99");
				
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
