package org.ppl.core;

import java.util.HashMap;
import java.util.Map;

import org.ppl.BaseClass.BaseCronThread;
import org.ppl.BaseClass.LibThread;
import org.ppl.etc.Config;
import org.ppl.etc.UrlClassList;
import org.ppl.etc.globale_config;
import org.ppl.io.TimeClass;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class CronThread extends LibThread {
	private Map<String, Integer> cronMap;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		ListQueue();
	}

	@Override
	public void ListQueue() {
		// TODO Auto-generated method stub
		UrlClassList ucl = UrlClassList.getInstance();
		TimeClass tc = TimeClass.getInstance();
		cronMap = new HashMap<String, Integer>();
		Config mConfig = new Config(globale_config.Config);
		int cronDelay = mConfig.GetInt("cronDelay");
		for (String ps : ucl.getPackList()) {
			try {
				Class<?> clazz = Class.forName(ps);

				if (clazz.getSuperclass().equals(BaseCronThread.class)) {
					String name = SliceName(ps);
					cronMap.put(name, 0);					
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		while (true) {
			
			int now = (int)tc.time();
			
			int nowHour = Integer.valueOf(tc.TimeStamptoDate(tc.time(), "hh"));
			int nowDay = Integer.valueOf(tc.TimeStamptoDate(tc.time(), "dd"));
						
			for (String key:cronMap.keySet()) {
				//System.out.print("KEY:"+key);
				Injector injector = globale_config.injector;
				BaseCronThread cron = (BaseCronThread) injector
						.getInstance(Key.get(BaseCronThread.class,
								Names.named(key)));
				boolean isStop = cron.isStop();
				if(isStop == true) {
					cron.free();
					continue;
				}
				
				int minu = cron.minute();
				int hour = cron.hour();
				int day = cron.day();
				
				int sleepTime = cronMap.get(key);
				int newTime = 0;
				if(day == 0 && hour == 0 && sleepTime < now){					
					newTime = now+minu*60;
				}else if (day==0 && hour == nowHour && sleepTime < now) {					
					newTime = now+minu*60+hour*60*60;
				}else if (day == nowDay && sleepTime < now) {					
					newTime =  now+hour*60*60+minu*60+86400;
				}
				else {
					cron.free();
					System.out.println("continue key:"+key+" sleepTime:"+sleepTime+" day:"+day+" hour:"+hour+" now:"+now);
					continue;
				}
				
				cronMap.put(key, newTime);
				cron.Run();
				cron.free();
				
			}
			try {
				Thread.sleep(60 * cronDelay * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private String SliceName(String k) {
		String[] name = k.split("\\.");
		String cName = name[name.length - 1];
		return cName;
	}

}
