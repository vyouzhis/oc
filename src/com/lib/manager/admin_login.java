package com.lib.manager;

import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;

public class admin_login extends Permission {

	public admin_login() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
	}
	
	@Override
	public void Show() {
		// TODO Auto-generated method stub		
		
		UrlClassList ucl = UrlClassList.getInstance();
		
		setRoot("admin_login_action_uri", ucl.BuildUrl("admin_login_action", ""));
		setRoot("salt", getSalt());
		super.View();
	}
}
