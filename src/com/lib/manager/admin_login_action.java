package com.lib.manager;

import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;

public class admin_login_action extends Permission {

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		isAutoHtml = false;
		String action = porg.getKey("action");
		if(action!=null){
			login();
		}else {
			LogOut();
		}
	}
	
	private void login() {
		
		String salt = porg.getKey("salt");
		String email = porg.getKey("email");
		String passwd = porg.getKey("passwd");
				
		UrlClassList ucl = UrlClassList.getInstance();
		String bad_url = ucl.BuildUrl("admin_login", "");
		String ok_url = ucl.BuildUrl("admin_index", "");
		
		if(passwd == null || salt == null || email == null){
			TipMessage(bad_url,super._CLang("error_passwd"));
			return;
		}
		
		boolean isSalt = checkSalt(salt);
		if(isSalt==false){
			TipMessage(bad_url,super._CLang("error_salt"));
			return;
		}
		
		int i = aclLogin(email, passwd, salt);
		
		switch (i) {
		case 0:
			TipMessage(ok_url,_CLang("welcome"));
			return;			
		case -1:			
			TipMessage(bad_url,_CLang("error_notexist"));
			return;
		case -2:			
			TipMessage(bad_url,_CLang("error_passwd"));
			return;
		case -3:
			TipMessage(bad_url,_CLang("error_errcount"));
			return;
		case -4:
			TipMessage(bad_url,_CLang("error_role"));
			return;
		case -5:
			TipMessage(bad_url, _CLang("ok_init_role"));
			return;
		default:
			TipMessage(bad_url,_CLang("error_nothing"));
			return;
		}
	}
	
	private void LogOut() {
		aclLogout();
		UrlClassList ucl = UrlClassList.getInstance();
		String bad_url = ucl.BuildUrl("admin_login", "");
		TipMessage(bad_url, super._CLang("bye"));
		return;
	}
	
}
