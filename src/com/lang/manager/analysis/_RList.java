package com.lang.manager.analysis;

import org.ppl.BaseClass.LibLang;

public class _RList extends LibLang{
	public _RList() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		GetSubClassName(className);
		SelfPath(this.getClass().getPackage().getName());
	}
}
