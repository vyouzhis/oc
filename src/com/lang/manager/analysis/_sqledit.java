package com.lang.manager.analysis;

import org.ppl.BaseClass.LibLang;

public class _sqledit extends LibLang{
	public _sqledit() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		GetSubClassName(className);
		SelfPath(this.getClass().getPackage().getName());
	}
}
