package org.ppl.plug.Quartz;

import java.util.List;

import org.ppl.common.function;
import org.ppl.etc.UrlClassList;
import org.ppl.etc.globale_config;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class RunQuartz extends function {
	private BaseQuartz runquartz = null;
	private Scheduler scheduler = null;
	@SuppressWarnings("unchecked")
	public void ListQuartz() {
		// TODO Auto-generated method stub
		UrlClassList ucl = UrlClassList.getInstance();

		try {
			//scheduler = StdSchedulerFactory.getDefaultScheduler();
			 
			scheduler = new StdSchedulerFactory("properties/quartz.properties").getScheduler();  
					
			Injector injector = globale_config.injector;

			for (String ps : ucl.getPackList()) {
				try {
					Class<?> clazz = Class.forName(ps);

					if (clazz.getSuperclass().equals(BaseQuartz.class)) {
						String name = SliceName(ps);

						runquartz = (BaseQuartz) injector.getInstance(Key.get(
								BaseQuartz.class, Names.named(name)));

						JobDetail job = (JobDetail) JobBuilder
								.newJob((Class<? extends Job>) runquartz
										.getClass())
								.withIdentity(name, runquartz.getGroup())
								.build();

						Trigger trigger = newTrigger()
								.withIdentity(runquartz.getTrigger(), runquartz.getGroup())
								.withSchedule(
										cronSchedule(runquartz.cronSchedule())
												.withMisfireHandlingInstructionDoNothing())

								.forJob(name, runquartz.getGroup()).build();

						scheduler.scheduleJob(job, trigger);

					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			
			scheduler.start();
			
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
//	public static boolean isJobRunning(String jobName, String groupName)
//	        throws SchedulerException {
//	    List<JobExecutionContext> currentJobs = scheduler.getCurrentlyExecutingJobs();
//
////	    for (JobExecutionContext jobCtx : currentJobs) {
////	        String thisJobName = jobCtx.getJobDetail().getKey().getName();
////	        String thisGroupName = jobCtx.getJobDetail().getKey().getGroup();
////	        if (jobName.equalsIgnoreCase(thisJobName) && groupName.equalsIgnoreCase(thisGroupName)
////	                && !jobCtx.getFireTime().equals(scheduler.getFireTime())) {
////	            return true;
////	        }
////	    }
//	    return false;
//	}
	
	public Boolean isJobPaused(String jobName) throws SchedulerException {

	    JobKey jobKey = new JobKey(jobName);
	    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
	    List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobDetail.getKey());
	    for (Trigger trigger : triggers) {
	        TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
	        if (TriggerState.PAUSED.equals(triggerState)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void delete(String jobName, String groupName) {
		try {
			scheduler.deleteJob(JobKey.jobKey(jobName, groupName));
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
