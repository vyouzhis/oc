package org.ppl.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ppl.etc.Config;
import org.ppl.etc.globale_config;

public class CookieAction{
	private HttpServletRequest request;
	private HttpServletResponse response;
	private static CookieAction source;
	private String Path;
	private int TimeOut;

	public static CookieAction getInstance() {
		if (source == null) {
			source = new CookieAction();					
		}

		return source;
	}

	public void init(HttpServletRequest req,HttpServletResponse res) {
		request = req;
		response = res;	
		Config mConfig = new Config(globale_config.Config);
		Path = mConfig.GetValue("cookie.path");
		TimeOut = mConfig.GetInt("cookie.timeout");
	}

	public void SetCookie(String key, String val) {		
		Cookie userCookie = new Cookie(key, val);
		userCookie.setMaxAge(TimeOut); //Store cookie for 1 year
		userCookie.setPath(Path);		
		response.addCookie(userCookie);
	}
	
	public void SetCookie(String key, String val, int timeOut) {		
		Cookie userCookie = new Cookie(key, val);
		userCookie.setMaxAge(timeOut); //Store cookie for timeOut second
		userCookie.setPath(Path);	
	
		response.addCookie(userCookie);		
	}
	
	public void DelCookie(String key) {		
		Cookie[] cookies = request.getCookies();
		if(cookies==null)return;
		for (Cookie cookie : cookies) {
			if(cookie.getName().equals(key)){
				cookie.setValue(null);
				cookie.setMaxAge(0);
				cookie.setPath(Path);
				response.addCookie(cookie);
			}
		}
		
	}

	public String GetCookie(String key) {
		Cookie[] cookies = request.getCookies();
		if (cookies==null) {
			return null;
		}
		for(Cookie cookie : cookies){
		    if(key.equals(cookie.getName())){
		        return cookie.getValue();
		    }
		}
		
		return null;
	}
}
