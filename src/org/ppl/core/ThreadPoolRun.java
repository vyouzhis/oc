package org.ppl.core;

import org.ppl.BaseClass.BaseCronThread;
import org.ppl.etc.globale_config;
import org.ppl.io.TimeClass;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class ThreadPoolRun implements Runnable {
	private String tpKey;
	private int sTime = 0;
	private boolean isRun=false;
	private BaseCronThread cron;
	
	public ThreadPoolRun(String key, int now) {
		// TODO Auto-generated constructor stub
		tpKey = key;
		sTime = now;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("i am run!");
		ThreadRuns();
	}
	
	public int etime() {
		isRun=false;
		//System.out.println("i am etime!");
		TimeClass tc = TimeClass.getInstance();
		int now = (int) tc.time();

		int nowHour = Integer.valueOf(tc.TimeStamptoDate(tc.time(), "hh"));
		int nowDay = Integer.valueOf(tc.TimeStamptoDate(tc.time(), "dd"));
		Injector injector = globale_config.injector;
		cron = (BaseCronThread) injector.getInstance(Key.get(
				BaseCronThread.class, Names.named(tpKey)));
		boolean isStop = cron.isStop();
		
		if (isStop == true) {
			cron.free();
			return 0;
		}

		int minu = cron.minute();
		int hour = cron.hour();
		int day = cron.day();

		int sleepTime = sTime;
		
		int newTime = 0;
		if (day == 0 && hour == 0 && sleepTime < now) {
			newTime = now + minu * 60;
		} else if (day == 0 && hour == nowHour && sleepTime < now) {
			newTime = now + minu * 60 + hour * 60 * 60;
		} else if (day == nowDay && sleepTime < now) {
			newTime = now + hour * 60 * 60 + minu * 60 + 86400;
		} else {
			cron.free();
			System.out.println("continue key:" + tpKey + " sleepTime:"
					+ sleepTime + " day:" + day + " hour:" + hour + " now:"
					+ now);
			return 0;
		}

		isRun = true;
		return newTime;
	}
	
	private void ThreadRuns() {
		if(isRun){
			cron.Run();
			cron.free();
		}else {
			System.out.println("no run!");
		}
	}

}
