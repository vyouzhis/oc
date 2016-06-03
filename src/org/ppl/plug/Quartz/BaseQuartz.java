package org.ppl.plug.Quartz;

import org.ppl.common.function;

public abstract  class BaseQuartz extends function {
	
	public abstract String getGroup();
	public abstract String getTrigger();
	public abstract String cronSchedule();
}
