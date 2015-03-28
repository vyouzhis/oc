package com.lib.thread;

import org.ppl.BaseClass.BaseCronThread;

public class loopTen extends BaseCronThread {

	@Override
	public int minute() {
		// TODO Auto-generated method stub
		return 10;
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
	public void Run() {
		// TODO Auto-generated method stub
		echo("10 minu!!");
	}

	@Override
	public boolean isStop() {
		// TODO Auto-generated method stub
		return false;
	}

}
