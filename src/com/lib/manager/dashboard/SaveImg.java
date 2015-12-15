package com.lib.manager.dashboard;

import java.util.List;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
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
		
		case "search":
			search(null);
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
		String tmp="";
		int n = toInt(porg.getKey("n"));
		int len = toInt(porg.getKey("len"));
		String name = porg.getKey("name")+".png";	
		byte[] baseImg ;
		//echo("n:"+n+" len:"+len);
		if(data!=null){
			
			if(data.length()<1)return;
			ProjectPath pp = ProjectPath.getInstance();
			//echo("data:"+data);
			pp.SaveFile(name, data.getBytes(), true);
			
			if(n==len-1){
				tmp = pp.getFile(name);
				
				tmp = tmp.substring(22, tmp.length());
				baseImg = Base64.decodeFast(tmp);
				pp.SaveFile(name, baseImg, false);

			}
				
			super.setHtml(n+"");
			
			
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
		String ImgName = porg.getKey("ImgName");
		ProjectPath pp = ProjectPath.getInstance();
		boolean  re = pp.findFile(ImgName);
		if(re==true){
			super.setHtml("1");
		}else {
			super.setHtml("0");
		}
	}

}
