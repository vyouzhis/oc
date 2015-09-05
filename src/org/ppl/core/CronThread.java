package org.ppl.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ppl.BaseClass.BaseCronThread;
import org.ppl.BaseClass.LibThread;
import org.ppl.etc.Config;
import org.ppl.etc.UrlClassList;
import org.ppl.etc.globale_config;
import org.ppl.io.TimeClass;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.lib.plug.echarts.echarts;

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
			System.out.println("start...");
			ExecutorService cachedThreadPool = Executors.newFixedThreadPool(cronMap.size());
			
			for (String key : cronMap.keySet()) {
				// System.out.print("KEY:"+key);	
//				cronMap.put(key, newTime);
//				
//				cronMap.put(key, cachedThreadPool.etime());
				
				cachedThreadPool.execute(new ThreadPoolRun(key, cronMap.get(key)));										
			}
			
			cachedThreadPool.shutdown();
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
