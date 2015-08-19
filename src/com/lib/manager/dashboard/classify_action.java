package com.lib.manager.dashboard;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;

public class classify_action extends Permission implements BasePerminterface{
	private List<String> rmc;
	public classify_action() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		setRoot("name", _MLang("name"));
		InAction(); // 设置只是动作
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
			break;
		case "create":
			create(null);
			return;
		default:
			Msg(_CLang("error_role"));
			return;
		}
		
		super.View();
	}
	
	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		int id = toInt(porg.getKey("id"));
		int pid=0;
		setRoot("pid", pid);
		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("action_url", ucl.create(SliceName(stdClass)));
		listPid();
	}
	
	/**
	 * i am lazy :(
	 */
	private void listPid() {
		String sql = "select id,name from "+DB_HOR_PRE+"classify where pid=0";
		List<Map<String, Object>> res;
				
		try {
			res = FetchAll(sql);
			if(res!=null){
				setRoot("pid_list", res);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		UrlClassList ucl = UrlClassList.getInstance();
		int pid = toInt(porg.getKey("pid_select"));
		String name = porg.getKey("name");
		if (name==null || name.length()==0) {
			TipMessage(ucl.create(SliceName(stdClass)), _CLang("error_null"));
			return;
		}
		
		String format = "insert INTO " + DB_HOR_PRE + "classify "
				+ "(pid ,name,ctime)"
				+ "values(%d,'%s', %d);";
		String sql = String.format(format, pid, name, time());

		try {
			insert(sql);
			TipMessage(ucl.read("classify_list"), _CLang("ok_save"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			TipMessage(ucl.create(SliceName(stdClass)), e.getMessage());
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
