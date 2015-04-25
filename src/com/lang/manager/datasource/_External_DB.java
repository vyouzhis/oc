package com.lang.manager.datasource;

import org.ppl.BaseClass.LibLang;

public class _External_DB extends LibLang {
	public _External_DB() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		GetSubClassName(className);
		SelfPath(this.getClass().getPackage().getName());
	}
}
