package com.lang.manager.crm;

import org.ppl.BaseClass.LibLang;

public class _SugarCRM_List extends LibLang {
	public _SugarCRM_List() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		GetSubClassName(className);
		SelfPath(this.getClass().getPackage().getName());
	}
}
