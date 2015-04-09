package com.lib.thread;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BaseCronThread;
import org.ppl.db.MGDB;

import com.alibaba.fastjson.JSON;

/**
 * 
 * @since mongodb count loop thread
 * 
 */
public class mclt extends BaseCronThread {

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
		String sql = "select id,collention,query, etime,istop from "+DB_HOR_PRE+"mongodbrule SET where qaction=3 order by id limit 10 offset "
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

	@SuppressWarnings("unchecked")
	private void Operation(Map<String, Object> o) {
		if (o == null) {
			return;
		}
		
		int rule = Integer.valueOf(o.get("id").toString());
		//int now = time();
		int now=1420991960;
		int start = Integer.valueOf(o.get("etime").toString());
		
		// Greater than 1 hour
		if((start+3600) >= now) {
			echo("now time");
			return;
		}
		
		List<String> date_format = new ArrayList<>();
		date_format.add("yyyyMM");
		date_format.add("yyyyMMdd");
		date_format.add("yyyyMMddHH");
				
		String m = "";
		MGDB mgdb;
		mgdb = new MGDB();
		mgdb.SetCollection(o.get("collention").toString());
		if (o.get("query").toString().length() < 2) {
			return;
		}
		
		m = o.get("query").toString();
		Map<String, Object> jo = null;
		try {
			jo = (Map<String, Object>) JSON.parse(m);	
		} catch (Exception e) {
			// TODO: handle exception
			return;
		}
		if(jo == null) return;
		
		Map<String, Integer> add = new HashMap<>();
		int lt = start;
		String format = "INSERT INTO "+DB_HOR_PRE+"mongodbcount( rule, volume, dial)VALUES" ;
		String sql_format = "(%d, %d, %d),";
		
		Map<String, Integer>  sql_val = new HashMap<>();

		
		while ((lt+3600)<now) {
			add.put("$gte", start);
			//3600 1 hour
			lt = start + 3600;
			add.put("$lt", lt);
			jo.put("SERVER.REQUEST_TIME", add);
			//echo(jo);
			String json = JSON.toJSONString(jo);
			mgdb.JsonWhere(json);

			int count = mgdb.FetchCont();
			
			String date = "";
			
			for(int i=0; i<date_format.size(); i++){
				date = DateFormat((long)lt, date_format.get(i));
				if(sql_val.containsKey(date)){
					int val = sql_val.get(date);
					val += count;
					sql_val.put(date, val);
				}else{
					sql_val.put(date, count);
				}
			}
			start = lt;
					
		}
		//echo(sql_val);
		String val_link = "";
		for(String mk:sql_val.keySet()){
			val_link += String.format(sql_format, rule, sql_val.get(mk), Integer.valueOf(mk));
		}
		
		String sql = format + val_link.substring(0, val_link.length()-1);
		echo(sql);
		
		try {
			update(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sql = "update "+DB_HOR_PRE+"mongodbrule SET etime = "+lt+" where id="+rule;
		
		try {
			update(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public boolean isStop() {
		// TODO Auto-generated method stub
		return true;
	}

}
