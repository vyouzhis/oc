package com.lib.manager.datasource;

import java.util.List;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;

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
		setRoot("new_api_url", ucl.create("api_secret_action"));
	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		int count = toInt(porg.getKey("count"));
		if(count!=0){
			echo(count);
		}else {
			echo("error");
		}
		
		for (int i = 0; i < count; i++) {
			
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
