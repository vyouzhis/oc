package com.lib.surface.api;

import java.util.HashMap;
import java.util.Map;

import org.ppl.BaseClass.BaseSurface;

import com.alibaba.fastjson.JSON;

public class build_salt extends BaseSurface{
	public build_salt() {
		// TODO Auto-generated constructor stub
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
		isAutoHtml = false;
	}
	
	@Override
	public void Show() {
		// TODO Auto-generated method stub
		super.setAjax(true);
		String salt = getSalt();
		Map<String, String> m = new HashMap<String, String>();
		m.put("salt", salt);
		String json = JSON.toJSONString(m);
		super.setHtml(json);
	}
}
