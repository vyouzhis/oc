package com.lib.surface.api;

import org.ppl.BaseClass.BaseSurface;

public class alogout extends BaseSurface {
	public alogout() {
		// TODO Auto-generated constructor stub
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
		isAutoHtml = false;
	}
	
	@Override
	public void Show() {
		// TODO Auto-generated method stub
		super.setAjax(true);
		SessAct.SetSession(mConfig.GetValue("session.api"), time()+"");
	}
}
