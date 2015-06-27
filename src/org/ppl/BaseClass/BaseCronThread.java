package org.ppl.BaseClass;

import org.ppl.db.DBSQL;
import org.ppl.db.HikariConnectionPool;

public abstract class BaseCronThread extends DBSQL{
	public BaseCronThread() {
		// TODO Auto-generated constructor stub
		HikariConnectionPool hcp = HikariConnectionPool.getInstance();
		hcp.GetCon();
	}
	public abstract int minute();
	public abstract int hour();
	public abstract int day();
	public abstract void Run();
	public abstract boolean isStop();
	public boolean getStop() {
		return false;
//		String format = "select * from role_thread where name='%s'";
//		String sql = String.format(format, SliceName(stdClass));
//		
//		Map<String, Object> res;
//		
//		res = FetchOne(sql);
//		if(res!=null){
//			if (Integer.valueOf(res.get("isstop").toString()) == 1) {
//				return false;
//			}
//		}
//		return true;
	}
}
