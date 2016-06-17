package com.lib.thread;

import org.ppl.plug.Quartz.CronQuartz;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class testQuartz extends CronQuartz implements Job {
	String nowTime = null;
	
	public testQuartz() {
		// TODO Auto-generated constructor stub
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
		
		nowTime = DateFormat((long)time(), "yyyy-MM-dd HH:mm:ss");
		echo("testQuartz ...."+nowTime);
	}
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// TODO Auto-generated method stub
		echo("execute :"+nowTime);
		int cronDelay = 3;
		
		try {
			Thread.sleep(63 * cronDelay * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		echo("run end ..."+nowTime+" --- "+DateFormat((long)time(), "yyyy-MM-dd HH:mm:ss"));
	}
	
	
	@Override
	public String getGroup() {
		// TODO Auto-generated method stub
		return "Group_"+SliceName(stdClass);
	}
	@Override
	public String cronSchedule() {
		// TODO Auto-generated method stub
		return "0/40 1 * * * ?";
	}
	
	public int isRun() {
		
		return 0;
	}
	
	
	

}
