package com.lib.thread;

import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BaseRapidThread;

public class testThread extends BaseRapidThread {

	@Override
	public void Run() {
		// TODO Auto-generated method stub
		String sql = "select * from web_user limit 1";
		Map<String, Object> res = FetchOne(sql);
		echo(res);
		echo("hi i am test thread !!!!!!!!!!!");
	}

	@Override
	public boolean isRun() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean Stop() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void mailbox(Object o) {
		// TODO Auto-generated method stub
		
		List<String> s = (List<String>) o;
		for(String k:s){
			echo(k);
		}
	}

}