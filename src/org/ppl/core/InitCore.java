package org.ppl.core;

import org.ppl.etc.Config;
import org.ppl.etc.globale_config;

public class InitCore {
	protected String stdClass = null;
	protected static final Config UserCoreConfig = new Config(
			globale_config.Mysql);
	protected static final Config myConfig = new Config(globale_config.DBCONFIG);
	protected static final Config mConfig = new Config(globale_config.Config);
	protected static final Config uConfig = new Config(globale_config.UrlMap);
	protected static final Config mgConfig = new Config(globale_config.Mongo);
	protected static final Config mailConfig = new Config(globale_config.Mail);
}
