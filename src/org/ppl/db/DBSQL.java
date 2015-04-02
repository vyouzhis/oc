package org.ppl.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BaseLang;
import org.ppl.etc.globale_config;

public class DBSQL extends BaseLang {

	public static DBSQL dataSource = null;
	// private Connection ConDB = null;
	// private int ConId = -1;
	private Statement stmt = null;
	protected String DB_NAME = mConfig.GetValue("db.name");
	protected String DB_PRE = mConfig.GetValue("db.rule.ext");
	protected String DB_HOR_PRE = mConfig.GetValue("db.hor.ext");
	protected String DB_WEB_PRE = mConfig.GetValue("db.web.ext");
	
	public DBSQL() {

	}

	public void SetCon() {

	}

	public void end() {
		try {
			stmt.close();
			// c.commit();
			// c.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void free() {
		HikariConnectionPool hcp = HikariConnectionPool.getInstance();
		hcp.free();
	}

	public void rollback() {
		long tid = myThreadId();
		Connection ConDB = globale_config.GDB.get(tid);
		try {
			if (ConDB != null) {
				ConDB.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Map<String, Object>> map(ResultSet rs) throws SQLException {
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		Object value = null;
		try {
			if (rs != null) {
				ResultSetMetaData meta = rs.getMetaData();
				int numColumns = meta.getColumnCount();
				while (rs.next()) {
					Map<String, Object> row = new HashMap<String, Object>();
					for (int i = 1; i <= numColumns; ++i) {
						String name = meta.getColumnName(i);

						if (meta.getColumnTypeName(i).equals("TINYINT")) {
							value = rs.getInt(i);

						} else {
							value = rs.getObject(i);
						}

						row.put(name, value);
					}
					results.add(row);
				}
			}
		} finally {
			// close(rs);
		}

		return results;
	}

	public List<Map<String, Object>> query(String sql) throws SQLException {
		List<Map<String, Object>> results = null;
		long tid = myThreadId();
		String clearSQL = sql;
		if(myConfig.GetValue("database.driverClassName").equals("org.postgresql.Driver")){
			clearSQL = sql.replace("`", "");
		}
		Connection ConDB = globale_config.GDB.get(tid);

		if (ConDB == null) {
			echo("con sql:" + clearSQL);
			return null;
		}
		
		
		ResultSet rs = null;
		stmt = ConDB.createStatement();
		rs = stmt.executeQuery(clearSQL);
		results = map(rs);
		rs.close();
		stmt.close();

		return results;
	}

	public List<Map<String, Object>> FetchAll(String sql) throws SQLException {
		return query(sql);
	}

	public Map<String, Object> FetchOne(String sql) {
		Map<String, Object> results = null;
		List<Map<String, Object>> fetlist = null;

		try {
			fetlist = query(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (fetlist != null && fetlist.size() > 0) {
			results = fetlist.get(0);
		}

		return results;
	}

	public long update(String sql) throws SQLException {
		long numRowsUpdated = 0;
		long tid = myThreadId();
		String clearSQL = sql;
		if(myConfig.GetValue("database.driverClassName").equals("org.postgresql.Driver")){
			clearSQL = sql.replace("`", "");
		}
		
		Connection ConDB = globale_config.GDB.get(tid);
		if (ConDB == null) {
			echo("con sql:" + clearSQL);
			return -1;
		}
		
		stmt = ConDB.createStatement();
		numRowsUpdated = stmt.executeUpdate(clearSQL,
				Statement.RETURN_GENERATED_KEYS);

		return numRowsUpdated;
	}

	public void CommitDB() {
		long tid = myThreadId();
		Connection ConDB = globale_config.GDB.get(tid);
		try {
			ConDB.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
