package com.lib.thread;

import org.ppl.plug.Quartz.CronQuartz;
import org.ppl.plug.Quartz.SimpleQuartz;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class myQuartz extends CronQuartz implements Job{
	String nowTime = null;
	public myQuartz() {
		// TODO Auto-generated constructor stub
		nowTime = DateFormat((long)time(), "yyyy-MM-dd HH:mm:ss");
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
		echo("myQuartz:"+nowTime);
	}
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// TODO Auto-generated method stub
		echo("myquartz ... execute :"+nowTime);
	}

	@Override
	public String getGroup() {
		// TODO Auto-generated method stub
		return "Group_"+SliceName(stdClass);
	}

	@Override
	public String cronSchedule() {
		// TODO Auto-generated method stub
		return "0/20 1 * * * ?";
	}

	@Override
	public int isRun() {
		// TODO Auto-generated method stub
		return 0;
	}


}
