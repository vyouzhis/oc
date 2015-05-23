package com.lib.thread;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BaseCronThread;

import com.lib.plug.echarts.GroupDataDig;


/**
 * 
 * @since mongodb count loop thread
 * 
 */
public class melt extends BaseCronThread {

	public melt() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
	}

	@Override
	public int minute() {
		// TODO Auto-generated method stub
		return 1;
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
		List<Map<String, Object>> res;
		GroupDataDig dd = GroupDataDig.getInstance();

		String sql = "select id,collention,query, etime,istop from "
				+ DB_HOR_PRE
				+ "mongodbrule where qaction=4 order by id limit 10 offset ";
		
		boolean  f = true;
		while (f) {
			sql += offset;
			try {
				res = FetchAll(sql);
				if (res != null && res.size()>0) {
					for (int i = 0; i < res.size(); i++) {
						dd.Operation(res.get(i));
					}
					offset += 10;
				} else {
					f=false;
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public boolean isStop() {
		// TODO Auto-generated method stub
		return true;
	}

}
