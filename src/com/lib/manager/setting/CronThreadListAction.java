package com.lib.manager.setting;

import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;
import org.ppl.etc.globale_config;

import com.alibaba.fastjson.JSON;

public class CronThreadListAction extends Permission implements BasePerminterface{
	private List<String> rmc;

	public CronThreadListAction() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		setRoot("name", _MLang("name"));
		setRoot("fun", this);
		super.setAction(1);
	}
	
	@Override
	public void Show() {
		// TODO Auto-generated method stub
		if (super.Init() == -1)
			return;

		rmc = porg.getRmc();
		if (rmc.size() != 2) {
			Msg(_CLang("error_role"));
			return;
		}
	
		switch (rmc.get(1).toString()) {
		case "read":
			read(null);
			break;

		case "edit":
			edit(null);
			return;

		default:
			Msg(_CLang("error_role"));
			return;
		}

		super.View();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
//		Map<String, Map<String, Object>> CronList = globale_config.CronListQueue;
//		//echo(CronList);
//		UrlClassList ucl = UrlClassList.getInstance();
//		
//		String key=porg.getKey("key");
//		if(key==null || key.length()==0){
//			TipMessage(ucl.read("CronThreadList"), _CLang("error_null"));
//			return;
//		}
//		
//		Map<String, Object> map = CronList.get(key);
//		String property = mConfig.GetValue("cron.property");
//		Map<String, String> proJson = JSON.parseObject(property, Map.class);
//		setRoot("CronMap", map);
//		setRoot("proJson", proJson);
//		
//		
//		String editUrl = ucl.edit(SliceName(stdClass));
//		
//		setRoot("action_url", editUrl);
//		setRoot("ekey", key);
	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void edit(Object arg) {
//		// TODO Auto-generated method stub
//		String key = porg.getKey("key");
//		int minute = toInt(porg.getKey("minute"));
//		int hour = toInt(porg.getKey("hour"));
//		int day = toInt(porg.getKey("day"));
//		boolean isStop = Boolean.valueOf(porg.getKey("isStop").toString());
//		
//		synchronized (globale_config.CronListQueue) {
//			globale_config.CronListQueue.get(key).put("minute", minute);
//			globale_config.CronListQueue.get(key).put("hour", hour);
//			globale_config.CronListQueue.get(key).put("day", day);
//			globale_config.CronListQueue.get(key).put("isStop", isStop);
//		}
//		UrlClassList ucl = UrlClassList.getInstance();
//		TipMessage(ucl.read("CronThreadList"), _CLang("ok_save"));
	}

	@Override
	public void remove(Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void search(Object arg) {
		// TODO Auto-generated method stub
		
	}

}
