package com.lang.manager.dashboard;

import org.ppl.BaseClass.LibLang;

public class _DashboardView extends LibLang {
	public _DashboardView() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		GetSubClassName(className);
		SelfPath(this.getClass().getPackage().getName());
	}
}
