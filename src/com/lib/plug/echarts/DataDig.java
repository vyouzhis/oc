package com.lib.plug.echarts;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.db.DBSQL;
import org.ppl.db.MGDB;

import com.alibaba.fastjson.JSON;

public class DataDig extends DBSQL {
	static DataDig source;

	public static DataDig getInstance() {
		if (source == null) {
			source = new DataDig();
		}

		return source;
	}

	@SuppressWarnings("unchecked")
	public void Operation(Map<String, Object> o) {
		if (o == null) {
			return;
		}

		int rule = Integer.valueOf(o.get("id").toString());
		//int now = time();
		int now=1421025243; //test end time
		int start = Integer.valueOf(o.get("etime").toString());

		// Greater than 1 hour
		if ((start + 3600) >= now) {
			//echo("now time");
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
			echo(m);
			return;
		}
		if (jo == null)
			return;

		Map<String, Integer> add = new HashMap<>();
		int lt = start;
		String dFormat = "";
		String dVal = "";
		
		if(o.containsKey("distinct")){
			dFormat =",val";
			dVal = ",'"+o.get("distinct").toString()+"'";
		}
		String format = "INSERT INTO " + DB_HOR_PRE
				+ "webvisitcount( rule, volume, dial "+dFormat+")VALUES";
		String sql_format = "(%d, %d, %d "+dVal+"),";

		Map<String, Integer> sql_val = new HashMap<>();

		while ((lt + 3600) < now) {
			add.put("$gte", start);
			// 3600 1 hour
			lt = start + 3600;
			add.put("$lt", lt);
			jo.put("SERVER.REQUEST_TIME", add);
			// echo(jo);
			String json = JSON.toJSONString(jo);
			mgdb.JsonWhere(json);

			int count = mgdb.FetchCont();

			String date = "";

			for (int i = 0; i < date_format.size(); i++) {
				date = DateFormat((long) lt, date_format.get(i));
				if (sql_val.containsKey(date)) {
					int val = sql_val.get(date);
					val += count;
					sql_val.put(date, val);
				} else {
					sql_val.put(date, count);
				}
			}
			start = lt;

		}
		// echo(sql_val);
		String val_link = "";
		for (String mk : sql_val.keySet()) {
			val_link += String.format(sql_format, rule, sql_val.get(mk),
					Integer.valueOf(mk));
		}

		String sql = format + val_link.substring(0, val_link.length() - 1);
		echo(sql);

		try {
			update(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sql = "update " + DB_HOR_PRE + "mongodbrule SET etime = " + lt
				+ " where id=" + rule;

		try {
			update(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
