package com.lib.manager.datasource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;


public class csvDb extends Permission implements BasePerminterface {
	private List<String> rmc;

	public csvDb() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		setRoot("name", _MLang("name"));
		//InAction();
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
			break;
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
		setRoot("action_url", ucl.create(SliceName(stdClass)));

	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		UrlClassList ucl = UrlClassList.getInstance();
		//setRoot("action_url", ucl.search(SliceName(stdClass)));

		//Map<String, String> name = porg.getUpload_name();
		
		CreateInfo();
		
		TipMessage(ucl.read("viewDb_List"), _CLang("ok_save"));
	}

	private void CreateView(long rule) {
				
		Map<String, byte[]> file = porg.getUpload_string();

		Map<String, Object> ThreadMail = new HashMap<String, Object>();
		ThreadMail.put("view_name", porg.getKey("view_name"));
		ThreadMail.put("csv_file", file);
		ThreadMail.put("rule", rule);
		//echo(ThreadMail);
		TellPostMan("updateCSVData", ThreadMail);
	}

	private void CreateInfo() {
		String title = porg.getKey("title");
		String view_name = porg.getKey("view_name");
		String idesc = porg.getKey("idesc");
		int eid = toInt(porg.getKey("id"));
		
		int now = time();
		if (title == null || view_name == null || idesc == null)
			return;

		String format = "INSERT INTO "
				+ DB_HOR_PRE
				+ "classinfo (title,view_name,idesc,ctime)values ('%s','%s','%s', %d)";
		String sql = String.format(format, title, view_name, idesc, now);

		try {
			long id = eid;
			if(eid==0){
				id = insert(sql, true);
				//echo(id);
				CommitDB();
			}

			if (id != -1 && id!=0) {
				CreateView(id);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void edit(Object arg) {
		// TODO Auto-generated method stub
		//setRoot("action_url", ucl.create(SliceName(stdClass)));
		setRoot("isedit", "1");
		int id = toInt(porg.getKey("id"));

		String sql = "select * from "
				+ DB_HOR_PRE
				+ "classinfo where id="+id;
		Map<String, Object> res;
		
		res = FetchOne(sql);
		if(res!=null){
			setRoot("view_name", res.get("view_name").toString());
			setRoot("title", res.get("title").toString());
			setRoot("idesc", res.get("idesc").toString());
		}
		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("action_url", ucl.create(SliceName(stdClass)));
		setRoot("editid", "?id="+id);
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
