package com.lang.manager.analysis;

import org.ppl.BaseClass.LibLang;

public class _Rlang extends LibLang{
	public _Rlang() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		GetSubClassName(className);
		SelfPath(this.getClass().getPackage().getName());
	}
}
