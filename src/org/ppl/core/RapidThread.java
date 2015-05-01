package org.ppl.core;

import org.ppl.BaseClass.BaseRapidThread;
import org.ppl.BaseClass.LibThread;
import org.ppl.etc.globale_config;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

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

				for (String key : globale_config.RapidListQueue.keySet()) {

					Injector injector = globale_config.injector;
					BaseRapidThread rapid = (BaseRapidThread) injector
							.getInstance(Key.get(BaseRapidThread.class,
									Names.named(key)));

					while (globale_config.RapidListQueue.get(key).size() > 0) {

						Object o = globale_config.RapidListQueue.get(key).pop();
						if(rapid.Stop()==true)continue;
						rapid.mailbox(o);
						rapid.Run();
					}
					rapid.free();
				}
			}
		}
	}
}
