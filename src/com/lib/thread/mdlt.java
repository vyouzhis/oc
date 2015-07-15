package com.lib.thread;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BaseCronThread;
import org.ppl.db.MGDB;

import com.lib.plug.echarts.DataDig;

/**
 * 
 * @since mongodb distinct loop thread
 *
 */
public class mdlt extends BaseCronThread {
	
	public mdlt() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
	}

	@Override
	public int minute() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public int hour() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int day() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void Run() {
		// TODO Auto-generated method stub
		int offset = 0;
		String sql = "select id,collention,query, etime, field, istop from "+DB_HOR_PRE+"mongodbrule where qaction=2 order by id limit 10 offset "
				+ offset;
		List<Map<String, Object>> res;
		
		try {
			res = FetchAll(sql);
			if (res != null) {
				for (int i = 0; i < res.size(); i++) {					
					Operation(res.get(i));
				}				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void Operation(Map<String, Object> o) {
		if (o == null) {
			return;
		}
		Map<String, Object> m=null;
		
		MGDB mgdb = new MGDB();
		
		String col = o.get("collention").toString();
		mgdb.SetCollection(col);
		if(o.get("field").toString().length()>3){
			mgdb.JsonWhere(o.get("field").toString());
		}
		List<Object> res = mgdb.Distinct(o.get("query").toString());
		
		if(res==null || res.size()==0){ 
			echo(mgdb.getErrorMsg());
			mgdb.Close();
			return;		
		}
		mgdb.Close();
		m = o;
		String format = "{%s:{\"$regex\":\"%s\"}}";
		String query = "";
		String oq = o.get("query").toString();
		DataDig dd = DataDig.getInstance();
		for (Object ob:res) {
			
			query = String.format(format, oq , ob.toString());
			m.put("query", query);
			m.put("distinct", ob.toString().toString());
			
			dd.Operation(m);
			m.remove("query");
			m.remove("distinct");
			
		}
		
	}

	@Override
	public boolean isStop() {
		// TODO Auto-generated method stub
		return true;
	}

}
