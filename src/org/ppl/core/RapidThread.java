package org.ppl.core;

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

				ExecutorService cachedThreadPool = Executors.newFixedThreadPool(globale_config.RapidListQueue.size());
				
				for (String key : globale_config.RapidListQueue.keySet()) {
					ThreadRapidRun trr = new ThreadRapidRun(key);
					cachedThreadPool.execute(trr);					
				}
			}
		}
	}
}
