package com.lib.thread;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BaseCronThread;
import org.ppl.etc.globale_config;

import com.lib.plug.echarts.DataDig;

/**
 * 
 * @since mongodb find snap = 1 loop thread
 *
 */
public class mflt extends BaseCronThread  {
	private String className;
	
	public mflt() {
		// TODO Auto-generated constructor stub
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
		globale_config.CronListQueue.put(SliceName(className), 1);
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
		String sql = "select id,collention,query,field, etime,istop from "+DB_HOR_PRE+"mongodbrule where qaction=0 order by id limit 10 offset "
				+ offset;
		List<Map<String, Object>> res;
		DataDig dd = DataDig.getInstance();
		//echo("mflt loop");
		try {
			res = FetchAll(sql);
			if (res != null) {
				for (int i = 0; i < res.size(); i++) {					
					dd.Snap(res.get(i));
				}				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		globale_config.CronListQueue.put(SliceName(className), 0);
	}

	@Override
	public boolean isStop() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String title() {
		// TODO Auto-generated method stub
		return "mflt_title";
	}

}
