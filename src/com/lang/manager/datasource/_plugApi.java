package com.lang.manager.datasource;

import org.ppl.BaseClass.LibLang;

public class _plugApi extends LibLang {
	public _plugApi() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		GetSubClassName(className);
		SelfPath(this.getClass().getPackage().getName());
	}
}
