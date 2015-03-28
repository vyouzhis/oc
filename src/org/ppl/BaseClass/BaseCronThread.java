package org.ppl.BaseClass;

import org.ppl.db.DBSQL;

public abstract class BaseCronThread extends DBSQL{
	public BaseCronThread() {
		// TODO Auto-generated constructor stub
		ThreadSetCon();
	}
	public abstract int minute();
	public abstract int hour();
	public abstract int day();
	public abstract void Run();
	public abstract boolean isStop();
}
