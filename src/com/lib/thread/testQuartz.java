package com.lib.thread;

import org.ppl.plug.Quartz.BaseQuartz;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class testQuartz extends BaseQuartz implements Job {
	String nowTime = null;
	
	public testQuartz() {
		// TODO Auto-generated constructor stub
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
		return "group";
	}
	@Override
	public String cronSchedule() {
		// TODO Auto-generated method stub
		return "0/40 * * * * ?";
	}
	
	public int isRun() {
		
		return 0;
	}
	@Override
	public String getTrigger() {
		// TODO Auto-generated method stub
		return "testTrigger";
	}
	
	

}
