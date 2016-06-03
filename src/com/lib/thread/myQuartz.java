package com.lib.thread;

import org.ppl.plug.Quartz.BaseQuartz;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class myQuartz extends BaseQuartz implements Job{

	public myQuartz() {
		// TODO Auto-generated constructor stub
		echo("myQuartz");
	}
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// TODO Auto-generated method stub
		echo("myquartz ... execute ");
	}

	@Override
	public String getGroup() {
		// TODO Auto-generated method stub
		return "myQuartzgroup";
	}

	@Override
	public String cronSchedule() {
		// TODO Auto-generated method stub
		return "0/20 * * * * ?";
	}

	@Override
	public String getTrigger() {
		// TODO Auto-generated method stub
		return "myTrigger";
	}
	
	

}
