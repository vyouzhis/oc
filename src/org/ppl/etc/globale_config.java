package org.ppl.etc;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.Map;

import org.rosuda.JRI.Rengine;

import com.google.inject.Injector;
import com.lib.plug.echarts.echarts;

public class globale_config {
	public static globale_config config = null;
	
	public static String dbCase = "PGSource";  // db source
	public static String PropertiesPath = "properties/";
	public static String ext = ".properties";
	public static String Config = PropertiesPath+"config"+ext;
	public static String Mongo = PropertiesPath+"mongo"+ext;
	public static String Mysql = PropertiesPath+"mysql"+ext;
	public static String PostGre = PropertiesPath+"pg"+ext;
	public static String DBCONFIG = PostGre;
	public static String UrlMap = PropertiesPath+"UrlMap"+ext;
	public static String Mail = PropertiesPath+"mail"+ext;
	
	//Kaptch
	public static String Kaptch = PropertiesPath+"kaptch"+ext;
	public static String KaptchSes = "KAPTCH";
	//session
	public static String SessSalt = "session.salt";
	public static String Ontime = "session.ontime";
	public static String sessAcl = "session.acl";
	public static String CookieSalt = "cookie.csalt";
	
	public static String TimeDelay = "time.delay";
	public static String TimeOut = "time.out";
	
	public static String SubRole = "subrole"; 
	
	public static String CookieRegion = "Region";
	
	//cookie user info
	public static String Uinfo = "iCore";
	
	public static Injector injector = null;
	
	public static Rengine RengineJava = null;
	
	public static Map<Long, Connection> GDB = null;
	
	//listQueue
	public static Map<String, LinkedList<Object>> RapidListQueue;
		
	public static globale_config getInstance() {
		if (config == null) {
			config = new globale_config();
		}

		return config;
	}
	


}
