package com.lib.manager.analysis;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.common.Escape;
import org.ppl.etc.UrlClassList;
import org.ppl.etc.globale_config;
import org.ppl.plug.R.Rlan;
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
		setRoot("action_url", ucl.create(SliceName(stdClass)));
		setRoot("search_url", ucl.search(SliceName(stdClass)));
		setRoot("script_url", ucl.read("RAction"));
		setRoot("file_url", ucl.create("RAction"));
		
		Rlan rlan = Rlan.getInstance();
		String[] sR = rlan.ls();

		String json = JSON.toJSONString(sR);
		// echo(json);
		setRoot("r_key_list", json);
		
		listPid();
		
		int id = toInt(porg.getKey("id"));
		if(id>0){
			String format = "select * from "+DB_HOR_PRE+"rlanguage where "+UserPermi()+"  and id=%d limit 1; ";
			String sql = String.format(format, id);
			Map<String, Object> res;
			
			res = FetchOne(sql);
			if(res!=null){
				//res.put("rcode", Escape.unescape(res.get("rcode").toString()));
				setRoot("rlang", res);
				setRoot("REdit", res.get("rcode").toString());
			}
		}
		listFile();	
	}
	
	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		SaveRlan();
	}

	@Override
	public void edit(Object arg) {
		// TODO Auto-generated method stub
		SaveRlan();
	}
	
	private void SaveRlan() {
		String title = porg.getKey("title");
		int cid = toInt(porg.getKey("cid_list"));
		int day = toInt(porg.getKey("loopday"));
		int hour = toInt(porg.getKey("loophour"));
		int minu = toInt(porg.getKey("loopminu"));
		String rdesc = porg.getKey("rdesc");
		String rcode = porg.getKey("rcode");
		int isshow=0;
		String showchart = porg.getKey("showchart");
		if(showchart!=null){
			isshow = 1;			
		}
		
		UrlClassList ucl = UrlClassList.getInstance();
		
		
		if(title == null || cid==0 || rdesc == null || rcode == null){

			TipMessage(ucl.read("RList"), _CLang("error_null"));
			return ;
		}
		
		int id = toInt(porg.getKey("id"));
		int now = time();
				
		String format = "", sql="";
		
		rcode = Escape.escape(rcode);
		if(id==0){
			format ="insert into "+DB_HOR_PRE+"rlanguage ( title,cid,day,hour,minu,rdesc,rcode,uid,isshare, ctime,etime,ishow)values('%s',%d,%d,%d,%d,'%s','%s',%d,%d, %d, %d, %d);";
			sql = String.format(format, title,cid,day,hour,minu, rdesc,rcode,aclGetUid(), 0, now,now, isshow);
		}else {
			format = "update "+DB_HOR_PRE+"rlanguage SET title='%s',cid=%d,day=%d,hour=%d,minu=%d,rdesc='%s',rcode='%s',etime = %d, ishow=%d  where id=%d";
			sql = String.format(format, title, cid, day,hour, minu, rdesc, rcode, now, isshow, id);
		}
		String msg = ""; 
		try {
			insert(sql);
			msg = _CLang("ok_save");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			
			msg = _CLang(getErrorMsg());
		}
		
		TipMessage(ucl.read("RList"), msg);
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
		if (key == null || key.length() == 0)
			return;

		Rlan rcoonnect = Rlan.getInstance();
		try {
			rcoonnect.connection().voidEval(String.format(format, key));
			rcoonnect.connection().voidEval("c<-as.character(a)");
			String[] my;

			my = rcoonnect.connection().eval("c").asStrings();
			if (my.length > 1) {
				for (int i = 1; i < my.length; i++) {
					asy += my[i] + "<br/>";
				}
			}
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rcoonnect.close();
		asy = asy.replace(" ", "&nbsp;");
		asy = asy.replace("<br/>", "<br />");
		asy = asy.replace("\n", "<br />");
		super.setHtml(asy);

	}
	
	private void listPid() {

		int pid = toInt(porg.getKey("pid"));
		setRoot("pid", pid);

		String sql = "select id,name from " + DB_HOR_PRE
				+ "classify where "+UserPermi()+" order by id desc";
		List<Map<String, Object>> res;

		try {
			res = FetchAll(sql);
			if (res != null) {
				setRoot("pid_list", res);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void listFile() {
		String sql = "select * from "+DB_HOR_PRE+"rexcel where "+UserPermi()+" order by id desc";
		List<Map<String, Object>> res = null;
		
		try {
			res = FetchAll(sql);
			if(res!=null){
				setRoot("ListFile", res);				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
