package com.lang.manager.user;

import org.ppl.BaseClass.LibLang;

public class _user_profile extends LibLang {
	public _user_profile() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		GetSubClassName(className);
		SelfPath(this.getClass().getPackage().getName());
	}
}
