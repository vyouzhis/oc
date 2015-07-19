package com.lib.manager.dashboard;

import java.util.List;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.io.Encrypt;
import org.ppl.io.ProjectPath;

import com.alibaba.fastjson.util.Base64;


public class SaveImg extends Permission implements BasePerminterface {
	private List<String> rmc;

	public SaveImg() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		setRoot("name", _MLang("name"));
		InAction(); // 设置只是动作
		setAjax(true); // 设置是 ajax
		isAutoHtml = false; // 不用加载页头和页脚
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
		case "create":
			create(null);
			break;
		

		default:
			Msg(_CLang("error_role"));
			return;
		}
	}

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		String data = porg.getKey("data");
		byte[] baseImg ;
	
		if(data!=null){
			
			baseImg = Base64.decodeFast(data.substring(22, data.length()));
			
			if(data.length()<1)return;
			ProjectPath pp = ProjectPath.getInstance();
			String name = time()+".png";
			pp.SaveFile(name, baseImg);
			super.setHtml(name);
		}
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
