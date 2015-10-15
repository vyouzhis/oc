package org.ppl.core;

import java.util.LinkedList;

import org.ppl.common.CookieAction;
import org.ppl.common.PorG;
import org.ppl.common.SessionAction;
import org.ppl.common.function;
import org.ppl.etc.Config;
import org.ppl.etc.globale_config;
import org.ppl.io.Encrypt;
import org.ppl.io.TimeClass;

public class PObject extends function{
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

	protected void GetSubClassName(String subClassname) {
		stdClass = subClassname;
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

	public long myThreadId() {
		return Thread.currentThread().getId();
	}

	

}
