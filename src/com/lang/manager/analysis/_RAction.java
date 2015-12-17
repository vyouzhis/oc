package com.lang.manager.analysis;

import org.ppl.BaseClass.LibLang;

public class _RAction extends LibLang {
	public _RAction() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		GetSubClassName(className);
		SelfPath(this.getClass().getPackage().getName());
	}
}
