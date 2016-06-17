package com.lib.manager.setting;

import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;
import org.ppl.etc.globale_config;

import com.alibaba.fastjson.JSON;

public class CronThreadList extends Permission implements
BasePerminterface{

	private List<String> rmc;
	
	public CronThreadList() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		setRoot("name", _MLang("name"));
		setRoot("fun", this);
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
//		String property = mConfig.GetValue("cron.property");
//		Map<String, String> proJson = JSON.parseObject(property, Map.class);
//		setRoot("cron_list", CronList);
//		setRoot("proJson", proJson);
//		
//		UrlClassList ucl = UrlClassList.getInstance();
//		String editUrl = ucl.read("CronThreadListAction");
//		
//		setRoot("editUrl", editUrl);
	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void edit(Object arg) {
		// TODO Auto-generated method stub
		
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
