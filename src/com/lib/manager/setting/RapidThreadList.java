package com.lib.manager.setting;

import java.util.List;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;

public class RapidThreadList extends Permission implements BasePerminterface{

	private List<String> rmc;
	private int Limit = 10;
	private int page = 0;
	
	public RapidThreadList() {
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
		case "search":
			search(null);
			break;
		case "create":
			create(null);
			return;
		case "edit":
			edit(null);
			break;
		case "remove":
			remove(null);
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

}
