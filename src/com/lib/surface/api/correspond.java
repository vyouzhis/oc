package com.lib.surface.api;

import java.util.List;

import org.ppl.BaseClass.BaseSurface;

public class correspond extends BaseSurface{
	private List<String> rmc;
	public correspond() {
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
					
		rmc = porg.getRmc();
		if(rmc.size()!=3) {
			super.setHtml(mConfig.GetValue("api.error.url"));
			return;
		}
		String salt = rmc.get(2);
		if(salt == null || salt.length()!=32){
			super.setHtml(mConfig.GetValue("api.error.arg"));
			return;
		}
		
		String salt_me = SessAct.GetSession(mConfig.GetValue("session.api"));
		if(!salt_me.equals(salt)){
			super.setHtml(mConfig.GetValue("api.error.salt"));
			return;
		}
		
		String exec = porg.getKey("exec");
		if(exec!=null){
			echo(exec);
		}else{
			echo("error");
		}
			
		super.setHtml("ok");
	}
	
	public void set_entries() {
		
	}
}
