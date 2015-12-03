package com.lib.manager;

import org.ppl.BaseClass.Permission;
import org.ppl.common.ShowMessage;
import org.ppl.io.FMConfig;

public class admin_index extends Permission {

	public admin_index() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);

	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub

		if (super.Init() == -1)
			return;
		int update = toInt(porg.getKey("update"));
		int clear = toInt(porg.getKey("clear"));
		
		
		if(FoundRole()==-1 && update==0){
			ShowMessage sm = ShowMessage.getInstance();
			sm.forward("?update=1");
		}
		if(update==1){
			InitRole();
		}
		
		if(clear==1){
			FMConfig fmc = FMConfig.getInstance();
			fmc.clearTL();
			setRoot("role_update_tip", _CLang("ok_tmeple_clear"));
		}
				
		super.View();
	}

	public int FoundRole() {
		
		String role = aclfetchMyRole();
		
		if (role == null || role.length() < 2) {
			return -1;
		}
		return 0;
	}
	
	public void InitRole() {
		if(RoleUpdate()==0){
			setRoot("role_update_tip", _CLang("ok_role_update"));
		}
	}
}