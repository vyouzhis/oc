package com.lib.manager.analysis;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;

import com.alibaba.fastjson.JSON;

public class sqlTmp extends Permission implements BasePerminterface{
	private List<String> rmc;
	UrlClassList ucl = UrlClassList.getInstance();
	public sqlTmp() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
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
		
		setRoot("lid", 0);
		switch (rmc.get(1).toString()) {
		case "read":
			read(null);
			break;
		case "search":
			int id = toInt(porg.getKey("id"));
			if(id==0) {
				TipMessage(ucl.read(SliceName(stdClass)), _CLang("error_null"));
				return;
			}
			search(id);
			break;
		case "create":
			create(null);
			return;
		case "edit":
			edit(null);
			return;
		default:
			Msg(_CLang("error_role"));
			return;
		}

		
		setRoot("search_url", ucl.search(SliceName(stdClass)));
		
		super.View();
	}
	
	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		
		ListTmp();
		
	}
	
	private void ListTmp() {
		String sql = "select id,name from "+DB_HOR_PRE+"usersql where sql_type=1 order by id desc";
		List<Map<String, Object>> res;
		
		try {
			res = FetchAll(sql);
			setRoot("ListTmp", res);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		String name = porg.getKey("tmp_name");
		if(name == null){
			TipMessage(ucl.read(SliceName(stdClass)), _CLang("error_null"));
			return;
		}
		
		int id = toInt(porg.getKey("lid"));
		if(id==0){
			TipMessage(ucl.read(SliceName(stdClass)), _CLang("error_nothing"));
			return;
		}
		
		String sql = "select sqltmp from "+DB_HOR_PRE+"usersql where id="+id+" limit 1";
		Map<String, Object> t ;
		t = FetchOne(sql);
		
		String jsonStr = t.get("sqltmp").toString();
		
		List<List<String>> JsonList = JSON.parseObject(jsonStr, List.class);
		
		Map<String, String> tmpVal = new HashMap<>();
		for (List<String> list : JsonList) {
			tmpVal.put(list.get(0), porg.getKey(list.get(0)));
		}
		
		String varRes = JSON.toJSONString(tmpVal);
		//echo(varRes);
		
		String format  = "insert INTO "+DB_HOR_PRE+"sqltmp (sid,name,sqltmp)values(%d, '%s', '%s')";
		sql = String.format(format, id,name, varRes);
		
		try {
			insert(sql);
			TipMessage(ucl.search(SliceName(stdClass))+"?id="+id, _CLang("ok_save"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			TipMessage(ucl.read(SliceName(stdClass)), e.getMessage());
		}
		
		
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void edit(Object arg) {
		// TODO Auto-generated method stub
		int id = toInt(porg.getKey("id"));
		String format = "select sqltmp,sid from "+DB_HOR_PRE+"sqltmp where id=%d limit 1";
		String sql = String.format(format, id);
		
		Map<String, Object> res;
		res = FetchOne(sql);
		if(res==null){
			TipMessage(ucl.read(SliceName(stdClass)), _CLang("error_null"));
			return;
		}
		
		Map<String, Object> JsonTmp = JSON.parseObject(res.get("sqltmp").toString(), Map.class);
		for (String key: JsonTmp.keySet()) {
			if(porg.getKey(key)!=null){
				JsonTmp.put(key, porg.getKey(key));
			}
		}
		
		String TmpJson = JSON.toJSONString(JsonTmp);
		
		format = "update "+DB_HOR_PRE+"sqltmp SET sqltmp='%s', name='%s',etime=%d where id=%d;";
		sql = String.format(format, TmpJson,porg.getKey("name").toString(), time(), id);
		
		try {
			update(sql);
			TipMessage(ucl.search(SliceName(stdClass))+"?id="+res.get("sid").toString(), _CLang("ok_save"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			TipMessage(ucl.read(SliceName(stdClass)), e.getMessage());
		}
		
	}

	@Override
	public void remove(Object arg) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void search(Object arg) {
		ListTmp();
		// TODO Auto-generated method stub
		int id = (int) arg;
		
		String sql = "select * from "+DB_HOR_PRE+"sqltmp where sid="+id +" order by id desc";
		
		List<Map<String, Object>> res;
		
		try {
			res = FetchAll(sql);
			setRoot("tmpLists", res);
			setRoot("lid", id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sql = "select sqltmp from "+DB_HOR_PRE+"usersql where id="+id+" limit 1";
		Map<String, Object> t ;
		t = FetchOne(sql);
		
		String jsonStr = t.get("sqltmp").toString();
		
		List<List<String>> JsonList = JSON.parseObject(jsonStr, List.class);
		setRoot("JsonList", JsonList);
		
		setRoot("create_url", ucl.create(SliceName(stdClass)));
		setRoot("edit_url", ucl.edit(SliceName(stdClass)));
	}

}
