package com.lib.thread;

import java.util.Map;

import org.ppl.BaseClass.BaseCronThread;

public class loopThread extends BaseCronThread{

	@Override
	public void Run() {
		// TODO Auto-generated method stub
		echo("i am a loop thread!");
	}

	@Override
	public int minute() {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM `test_thread` limit 1";
		Map<String, Object> res ;
		res = FetchOne(sql);
		
		if(res!=null){
			return Integer.valueOf(res.get("minute").toString());
		}
		return 1;
	}

	@Override
	public int hour() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int day() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isStop() {
		// TODO Auto-generated method stub
		String sql = "SELECT * FROM `test_thread` limit 1";
		Map<String, Object> res ;
		res = FetchOne(sql);
		int flag = Integer.valueOf(res.get("minute").toString());
		if(flag == 1){
			return false;
		}
		
		return true;
	}

}
