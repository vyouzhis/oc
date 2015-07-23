package com.lib.surface.api;

import java.util.HashMap;
import java.util.Map;

import org.ppl.BaseClass.BaseSurface;
import com.alibaba.fastjson.JSON;

public class alogin extends BaseSurface {
	public alogin() {
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
		String salt = porg.getKey("salt");
		String login = porg.getKey("login");
		String pwd = porg.getKey("pwd");
		if(salt == null || salt.length()!=32 || login == null || pwd == null){
			super.setHtml(mConfig.GetValue("api.error.arg"));
			return;
		}
		boolean f = checkSalt(salt);
		if(f==false){
			super.setHtml(mConfig.GetValue("api.error.salt"));
			return;
		}
		
		String format = "SELECT id from "+DB_HOR_PRE+"apisecret where username = '%s' and passwd='%s' limit 1";
		String sql = String.format(format, login, pwd);
		Map<String, Object> res;
		
		res = FetchOne(sql);
		if(res==null || res.size()==0) {
			super.setHtml(mConfig.GetValue("api.error.pwd"));
			return;
		}
		
		String login_salt = getSalt();
		SessAct.SetSession(mConfig.GetValue("session.api"), login_salt);
		
		Map<String, String> m = new HashMap<String, String>();
		m.put("salt", login_salt);
		String json = JSON.toJSONString(m);
		super.setHtml(json);
		
	}
}
