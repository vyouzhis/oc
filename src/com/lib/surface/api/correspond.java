package com.lib.surface.api;

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

	public void set_entries(Map<String, Object> o) {
		echo(o);
		String format = "INSERT INTO %s (%s) VALUES %s ;";
		String table = o.get("module_name").toString();
		String string;
	}
}
