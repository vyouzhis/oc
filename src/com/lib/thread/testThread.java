package com.lib.thread;

import org.ppl.plug.Quartz.SimpleQuartz;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class testThread extends SimpleQuartz implements Job {

	public testThread() {
		// TODO Auto-generated constructor stub
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
	}
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// TODO Auto-generated method stub
		
		echo("testThread=========="+time());
	}

	@Override
	public String getGroup() {
		// TODO Auto-generated method stub
		return "Group_"+SliceName(stdClass);
	}

	@Override
	public int withRepeatCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int withIntervalInSeconds() {
		// TODO Auto-generated method stub
		return 20;
	}

}