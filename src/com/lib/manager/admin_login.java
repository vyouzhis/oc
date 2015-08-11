package com.lib.manager;

import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;
import org.ppl.etc.globale_config;


public class admin_login extends Permission {

	public admin_login() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub

		//callRJava();
						
		UrlClassList ucl = UrlClassList.getInstance();

		setRoot("admin_login_action_uri",
				ucl.BuildUrl("admin_login_action", ""));
		setRoot("salt", getSalt());
		setRoot("input_tips", _CLang("input_tips"));
		super.View();
	}

	public void callRJava() {
		System.loadLibrary("jri");
		echo("new R");
		// Rengine re = new Rengine(new String[] { "--no-save" }, false, null);
		// Rengine re = Rengine.getMainEngine();
		// if(re == null){
		// re = new Rengine(new String[] {"--vanilla"}, false, null);
		// echo("is null");
		// }else{
		// echo("is not null");
		// }
		String newargs1[] = { "--no-save" };
		//Rengine re = Rengine.getMainEngine();
//		if (re == null) {
//			re = new Rengine(newargs1, false, null);
//		}else {
//			
//		}

		//echo("ok R");
		if (!globale_config.RengineJava.waitForR()) {
			echo("Cannot load R");
			return;
		}
		// 打印变量
		String version = globale_config.RengineJava.eval("R.version.string").asString();
		//echo(version);

		// 循环打印数组
		double[] arr = globale_config.RengineJava.eval("rnorm(10)").asDoubleArray();
		for (double a : arr) {
			echo(a + ",");
		}

		//globale_config.RengineJava.end();
		//re.destroy();
		//echo("end!!!");
	}
}
