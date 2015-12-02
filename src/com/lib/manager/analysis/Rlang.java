package com.lib.manager.analysis;

import java.util.List;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;
import org.ppl.etc.globale_config;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RserveException;

import com.alibaba.fastjson.JSON;

public class Rlang extends Permission implements BasePerminterface {
	private List<String> rmc;

	public Rlang() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
		setRoot("name", _MLang("name"));
		InAction();
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
		case "search":
			search(null);
			return;
		case "create":
			create(null);
			return;
		case "edit":
			edit(null);
			break;
		default:
			Msg(_CLang("error_role"));
			return;
		}

		super.View();
	}

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("action_url", ucl.read(SliceName(stdClass)));
		setRoot("search_url", ucl.search(SliceName(stdClass)));
		String[] sR;

		try {
			sR = globale_config.rcoonnect.eval("ls('package:base')")
					.asStrings();
			String json = JSON.toJSONString(sR);
			//echo(json);
			setRoot("r_key_list", json);
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		String key = porg.getKey("key");
		String asy = "";
		String format = "a<-getAnywhere('%s')";
		if(key==null || key.length()==0)return;
		
		try {
			globale_config.rcoonnect.voidEval(String.format(format, key));
			globale_config.rcoonnect.voidEval("c<-as.character(a)");
			String[] my;

			my = globale_config.rcoonnect.eval("c").asStrings();

			for (int i = 0; i < my.length; i++) {
				asy += my[i]+"<br/>";
			}
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		asy = asy.replace(" ", "&nbsp;");
		asy = asy.replace("<br/>", "<br />");
		asy = asy.replace("\n", "<br />");
		super.setHtml(asy);

	}

}
