package org.ppl.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ppl.common.CookieAction;
import org.ppl.common.PorG;
import org.ppl.common.SessionAction;
import org.ppl.etc.Config;
import org.ppl.etc.UrlClassList;
import org.ppl.etc.globale_config;
import org.ppl.io.Encrypt;
import org.ppl.io.TimeClass;

public class PObject {
	protected String stdClass = null;
	private String BindName = null;

	protected static final Config UserCoreConfig = new Config(
			globale_config.Mysql);
	protected static final Config myConfig = new Config(globale_config.DBCONFIG);
	protected static final Config mConfig = new Config(globale_config.Config);
	protected static final Config uConfig = new Config(globale_config.UrlMap);
	protected static final Config mgConfig = new Config(globale_config.Mongo);
	protected static final Config mailConfig = new Config(globale_config.Mail);

	protected SessionAction SessAct = SessionAction.getInstance();
	protected CookieAction cookieAct = CookieAction.getInstance();
	protected PorG porg = PorG.getInstance();

	public void echo(Object o) {
		if (stdClass != null) {
			Logger log = Logger.getLogger(stdClass);
			log.info(o.toString());
		} else {
			// System.out.println("stdClass:"+stdClass);
			System.out.println(o);
		}
	}

	public void echo(Object o, String file, int line) {
		if (stdClass != null) {
			Logger log = Logger.getLogger(stdClass);
			log.info(o.toString() + "file:"+file+ ":" + line);
		} else {
			// System.out.println("stdClass:"+stdClass);
			System.out.println(o + "file:"+file + ":" + line);
		}
	}

	public static String _FILE_() {
		StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();
		return stackTraces[1].getFileName();
	}

	public static int _LINE_() {
		StackTraceElement stackTraces[] = (new Throwable()).getStackTrace();
		return stackTraces[1].getLineNumber();
	}

	protected void GetSubClassName(String subClassname) {
		stdClass = subClassname;
	}

	public String SliceName(String k) {
		String[] name = k.split("\\.");
		String cName = name[name.length - 1];
		return cName;
	}

	public String getBindName() {
		if (BindName == null) {
			BindName = stdClass;

		}
		return BindName;
	}

	public void setBindName(String bindName) {
		BindName = bindName;
	}

	public String getSalt() {
		TimeClass tc = TimeClass.getInstance();
		Encrypt ec = Encrypt.getInstance();

		String salt = ec.MD5(String.valueOf(tc.time()));
		Config mConfig = new Config(globale_config.Config);

		SessAct.SetSession(mConfig.GetValue(globale_config.SessSalt), salt);

		return salt;
	}

	public boolean checkSalt(String salt) {

		Encrypt ec = Encrypt.getInstance();
		String new_salt = ec.MD5(String.valueOf(time()));

		String sess_salt = SessAct.GetSession(mConfig
				.GetValue(globale_config.SessSalt));

		if (sess_salt == null)
			return false;
		if (sess_salt.equals(salt)) {
			SessAct.SetSession(mConfig.GetValue(globale_config.SessSalt),
					new_salt);
			return true;
		}

		return false;
	}

	public List<String> PermFileList(String directoryName) {
		List<String> fl = new ArrayList<String>();
		File directory = new File(directoryName);
		Map<String, List<String>> PackClassList;
		UrlClassList ucl = UrlClassList.getInstance();
		PackClassList = ucl.getPackClassList();
		if (PackClassList == null) {
			PackClassList = new HashMap<String, List<String>>();
		}
		// get all the files from a directory
		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.isFile()) {
				// echo("name:"+directory.getName()+"__"+file.getName());
				String lib = file.getName().split("\\.")[0];
				String index = directory.getName();
				if (!index.equals("manager") && !lib.matches("(.*)_index")) {

					if (PackClassList.get(index) != null) {
						if (!PackClassList.get(index).contains(lib))
							PackClassList.get(index).add(lib);
					} else {
						List<String> l = new ArrayList<String>();
						l.add(lib);
						PackClassList.put(index, l);
					}
				}
				fl.add("Permission." + lib);
			} else if (file.isDirectory()) {
				fl.addAll(PermFileList(file.getAbsolutePath()));
			}
		}
		ucl.setPackClassList(PackClassList);

		return fl;
	}

	public List<String> PermUrlMap() {
		Config mConfig = new Config(globale_config.Config);
		String path = this.getClass().getResource("/").getPath()
				+ mConfig.GetValue("perm_class_path");

		List<String> pum = PermFileList(path);

		return pum;
	}

	public void findPack(String path) {
		UrlClassList ucl = UrlClassList.getInstance();
		File directory = new File(path);
		File[] fList = directory.listFiles();
		String[] pack = path.split("classes");
		if (pack.length != 2)
			return;
		String pn = pack[1].replace("/", ".");
		pn = pn.replace("\\", ".");

		if (!pn.substring(pn.length() - 1, pn.length()).equals(".")) {
			pn = pn + ".";
		}
		pn = pn.substring(1);

		for (File file : fList) {
			if (file.isFile()) {
				String lib = file.getName().split("\\.")[0];
				ucl.setPackList(pn + lib);
			} else if (file.isDirectory()) {
				findPack(file.getAbsolutePath());
			}
		}
	}

	public void TellPostMan(String ThreadName, Object message) {

		synchronized (globale_config.RapidListQueue) {
			if (globale_config.RapidListQueue.containsKey(ThreadName)) {
				globale_config.RapidListQueue.get(ThreadName).add(message);
			} else {
				LinkedList<Object> m = new LinkedList<Object>();
				m.add(message);
				globale_config.RapidListQueue.put(ThreadName, m);
			}
			// message.setText("Notifier took a nap for 3 seconds");
			// System.out.println("Notifier is notifying waiting thread to wake up at "
			// + new Date());
			globale_config.RapidListQueue.notify();
		}
	}

	public int time() {
		TimeClass tc = TimeClass.getInstance();
		return (int) tc.time();
	}

	public String DateFormat(Long TimeStamp, String format) {
		TimeClass tc = TimeClass.getInstance();
		return tc.TimeStamptoDate(TimeStamp, format);
	}

	public long myThreadId() {
		return Thread.currentThread().getId();
	}

	public boolean validateEmailAddress(String emailAddress) {
		Pattern regexPattern;
		Matcher regMatcher;
		regexPattern = Pattern
				.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
		regMatcher = regexPattern.matcher(emailAddress);
		if (regMatcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	public int toInt(Object o) {
		if (o != null && o.toString().matches("[0-9-]+")) {
			return Integer.valueOf(o.toString());
		} else {
			return 0;
		}
	}
	
	public float toFloat(Object o) {
		if (o != null && o.toString().matches("[0-9.-]+")) {
			return Float.valueOf(o.toString());
		} else {
			return 0;
		}
	}

	public String escapeHtml(String old) {
		if (old == null)
			return "";

		String news = old.replace("&nbsp;", "");
		news = news.replace("&quot;", "\"");
		news = news.replace("&apos;", "\'");
		news = news.replace(";", "");
		news = news.replace("'", "\'");
		// news = news.replace("%", "%%");
		news = news.replace("\r", " ");
		news = news.replace("\t", " ");
		news = news.replace("\n", " ");

		return news;
	}

	public String unescapeHtml(String old) {
		if (old == null)
			return "";

		String news = old.replace("\"", "&quot;");
		news = news.replace("\'", "&apos;");
		news = news.replace("\r", " ");
		news = news.replace("\t", " ");
		news = news.replace("\n", " ");
		// news = news.replace("%", "%%");
		return news;

	}

}
