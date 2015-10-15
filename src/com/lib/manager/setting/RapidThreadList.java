package com.lib.manager.setting;

import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.globale_config;

import com.alibaba.fastjson.JSON;

public class RapidThreadList extends Permission implements BasePerminterface{

	private List<String> rmc;
	private int Limit = 10;
	private int page = 0;
	
	public RapidThreadList() {
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
		page = toInt(porg.getKey("p"));
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
		Map<String, Map<String, Object>> rapidList = globale_config.RapidList;
		setRoot("rapidList", rapidList);
		
		String property = mConfig.GetValue("rapid.property");		
		Map<String, String> proJson = JSON.parseObject(property, Map.class);
		setRoot("proJson", proJson);
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
