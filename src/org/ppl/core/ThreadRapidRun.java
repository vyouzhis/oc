package org.ppl.core;

import org.ppl.BaseClass.BaseRapidThread;
import org.ppl.etc.globale_config;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class ThreadRapidRun implements Runnable{
	private String myKey;
		
	public ThreadRapidRun(String key) {
		// TODO Auto-generated constructor stub
		myKey = key;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		rapidRun();
	}

	private void rapidRun() {
		Injector injector = globale_config.injector;
		BaseRapidThread rapid = (BaseRapidThread) injector
				.getInstance(Key.get(BaseRapidThread.class,
						Names.named(myKey)));

		while (globale_config.RapidListQueue.get(myKey).size() > 0) {

			Object o = globale_config.RapidListQueue.get(myKey).pop();
			if(rapid.Stop()==true)continue;
			rapid.mailbox(o);
			rapid.Run();
		}
		rapid.free();
	}
}
