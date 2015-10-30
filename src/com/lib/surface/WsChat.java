package com.lib.surface;


import org.ppl.BaseClass.BaseSurface;

public class WsChat extends BaseSurface {

	public WsChat() {
		// TODO Auto-generated constructor stub
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		setRoot("user", "Big Joe");
		super.View();
	}

}
