package com.lang.manager.datasource;

import org.ppl.BaseClass.LibLang;

public class _csvDb extends LibLang{
	public _csvDb() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		GetSubClassName(className);
		SelfPath(this.getClass().getPackage().getName());
	}
}
