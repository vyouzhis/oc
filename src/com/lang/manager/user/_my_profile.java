package com.lang.manager.user;

import org.ppl.BaseClass.LibLang;

public class _my_profile extends LibLang {
	public _my_profile() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		GetSubClassName(className);
		SelfPath(this.getClass().getPackage().getName());
	}
}
