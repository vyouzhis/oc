package com.lib.thread;

import org.ppl.BaseClass.BaseRapidThread;
import org.ppl.net.cUrl;

public class getJPGovData extends BaseRapidThread{
	cUrl curl;
	String url;
	
	@Override
	public String title() {
		// TODO Auto-generated method stub
		String className = this.getClass().getCanonicalName();

		return _CLang(SliceName(className));
	}

	@Override
	public void Run() {
		// TODO Auto-generated method stub
		String ver = "2.0";
		String appId = "abb68400ed0dd8e8828b6d8b3e32154c111561b4";
		int limit = 5;
		int startPosition = 1;
		url = "http://api.e-stat.go.jp/rest/"+ver+"/app/getStatsList?appId="+appId;
		
	}

	@Override
	public boolean isRun() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean Stop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void mailbox(Object o) {
		// TODO Auto-generated method stub
		
	}
	
	private void getStatsList() {
		
	}
}
