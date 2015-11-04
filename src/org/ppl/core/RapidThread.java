package org.ppl.core;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ppl.BaseClass.LibThread;
import org.ppl.etc.globale_config;


public class RapidThread extends LibThread {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("rapidthread run");
		ListQueue();
	}

	public void ListQueue() {
		
		
		while (true) {
			synchronized (globale_config.RapidListQueue) {
				try {
					globale_config.RapidListQueue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int i= globale_config.RapidListQueue.size();
				System.out.println("RapidListQueue size:"+i);
				ExecutorService cachedThreadPool = Executors.newFixedThreadPool(i);
				
				for (Map<String, Object> key : globale_config.RapidListQueue) {
					ThreadRapidRun trr = new ThreadRapidRun(key.get("name").toString(), key.get("obj"));
					cachedThreadPool.execute(trr);					
				}
				//cachedThreadPool.shutdown();
				
			}
		}
	}
}
