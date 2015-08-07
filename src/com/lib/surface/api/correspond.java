package com.lib.surface.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BaseSurface;

import com.alibaba.fastjson.JSON;
import com.github.abel533.echarts.style.itemstyle.Emphasis;

public class correspond extends BaseSurface {
	private List<String> rmc;

	public correspond() {
		// TODO Auto-generated constructor stub
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
		isAutoHtml = false;
	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		super.setAjax(true);

		rmc = porg.getRmc();
		if (rmc.size() != 3) {
			super.setHtml(mConfig.GetValue("api.error.url"));
			return;
		}
		String salt = rmc.get(2);
		if (salt == null || salt.length() != 32) {
			super.setHtml(mConfig.GetValue("api.error.arg"));
			return;
		}

		String salt_me = SessAct.GetSession(mConfig.GetValue("session.api"));
		if (!salt_me.equals(salt)) {
			super.setHtml(mConfig.GetValue("api.error.salt"));
			return;
		}

		String exec = porg.getKey("exec");
		String action = porg.getKey("action");
		
		if (exec != null && action != null) {
			exec(exec, action);
		} else {
			echo("error");
		}

		super.setHtml("ok");
	}

	@SuppressWarnings("unchecked")
	private void exec(String execs, String action) {
		Map<String, Object> eMap = null;
		
		eMap = JSON.parseObject(execs, Map.class);
		if (eMap == null)
			return;
		
		switch (action) {
		case "set_entries":
			set_entries(eMap);
			break;

		default:
			break;
		}

	}
	
	@SuppressWarnings("unchecked")
	public void set_entries(Map<String, Object> o) {
		echo(o);
		String format = "INSERT INTO %s (%s) VALUES %s ;";
		String table = o.get("module_name").toString();
		String name_value_list = o.get("name_value_list").toString();
		String fields="";
		String values = "";
		boolean flag = false;
		List<List<Map<String, Object>>> nvl = JSON.parseObject(name_value_list, List.class);
		if(nvl == null) return;
		
		for (int i = 0; i < nvl.size(); i++) {
			List<Map<String, Object>> nmap = nvl.get(i);
			values += "(";
			for (int j = 0; j < nmap.size(); j++) {
				if(flag == false){
					fields += nmap.get(j).get("name").toString()+",";
				}
				values += "'"+nmap.get(j).get("value").toString()+"',";
			}
			if(values.length() > 3);
			values = values.substring(0, values.length()-1);
			values += "),";
			flag = true;
		}
		if(fields.length() > 3) {
			fields = fields.substring(0, fields.length()-1);
			values = values.substring(0, values.length()-1);
		}
		String sql = String.format(format, table, fields, values);
		//echo(sql);
		
		try {
			insert(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
