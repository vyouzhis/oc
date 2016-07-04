package com.lib.thread;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.db.HikariConnectionPool;
import org.ppl.db.MGDB;
import org.ppl.plug.Quartz.SimpleQuartz;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSON;

public class getJPGovFromMongo extends SimpleQuartz implements Job {
	private String statsField = "06";

	private long pid;

	public getJPGovFromMongo() {
		// TODO Auto-generated method stub
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
	}

	
	private void FindJPJson() {
		int limit = 100, offset = 0;
		boolean isLoop = true;
		String Col = "getStatsData_" + statsField;

		Map<String, Object> jMap = new HashMap<String, Object>();
		jMap.put("GET_STATS_DATA.STATISTICAL_DATA.TABLE_INF", 1);
		jMap.put("GET_STATS_DATA.STATISTICAL_DATA.DATA_INF.VALUE", 1);

		String json = JSON.toJSONString(jMap);
		MGDB mgdb = new MGDB();

		Map<String, Object> rmap;

		while (isLoop) {
			mgdb.SetCollection(Col);
			mgdb.JsonColumn(json);

			mgdb.setDBOffset(offset);
			mgdb.setLimit(limit);

			isLoop = mgdb.FetchList();
			while (isLoop) {
				rmap = mgdb.GetValueLoop();
				if (rmap == null)
					break;
				if (ParserJPJson(rmap) == false)
					continue;
			}
			offset += limit;
			//echo("offset: " + offset);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean ParserJPJson(Map<String, Object> pmap) {
		Map<String, Object> GET_STATS_DATA = (Map<String, Object>) pmap
				.get("GET_STATS_DATA");
		Map<String, Object> STATISTICAL_DATA = (Map<String, Object>) GET_STATS_DATA
				.get("STATISTICAL_DATA");
		Map<String, Object> TABLE_INF = (Map<String, Object>) STATISTICAL_DATA
				.get("TABLE_INF");
		Map<String, Object> DATA_INF = (Map<String, Object>) STATISTICAL_DATA
				.get("DATA_INF");

		if (!(DATA_INF.get("VALUE") instanceof List)) {
			echo("ramp value is empty:" + pmap);
			return false;
		}
		List<Map<String, Object>> VALUE = (List<Map<String, Object>>) DATA_INF
				.get("VALUE");

		// Category
		//echo("start Category");
		long valId = Category(TABLE_INF);
		//echo("end Category valId:" + valId);
		if (valId == 0)
			return false;

		String name = TABLE_INF.get("STATISTICS_NAME").toString();
		String id = TABLE_INF.get("id").toString();
		
		Map<String, String>  FieldS = setWhere(id);
		if(FieldS.size()==0){
			echo("error id:"+id);
			return false;
		}
		long sid = CreateUserSQL(valId, name, FieldS);
		long infoId = CreateClassInfo(TABLE_INF);

		String unit = "";
		if (VALUE.size() > 0) {
			if (VALUE.get(0).containsKey("unit")) {
				unit = VALUE.get(0).get("unit").toString();
			}
		}

		CreateSQLTmp(sid, infoId, name, unit);

		CreateClass(VALUE, FieldS, infoId);
		
		return true;
	}

	private void CreateClass(List<Map<String, Object>> values, Map<String, String>  FieldS, long rule) {
		if(values.size()==0) return;
		String field = "(";
		String SQLvalue = "";
		String sql = " insert INTO " + DB_HOR_PRE
				+ "class ";
		
		
		//valList = String.format(ValFormat, args)
		int L=2;
		for (Map<String, Object> vmap: values ) {
			SQLvalue += "(";
			for (String key : FieldS.keySet()) {
				if(key.equals("area")) continue;
				if(L-2 < FieldS.size()){
					field += "act_v" + L +",";
					L++;
				}
				SQLvalue += "'"+vmap.get(key)+"',";
			}
			SQLvalue += "'"+vmap.get("volume")+"','"+vmap.get("area")+"', "+rule+"),";
		}
		
		field += "act_v0, act_v1, rule)";
		sql += field +"values " + SQLvalue.substring(0, SQLvalue.length()-1);
		//echo(sql);
		try {
			insert(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void CreateSQLTmp(long sid, long ifid, String name, String unit) {
		String sqltmp = "insert INTO "
				+ DB_HOR_PRE
				+ "sqltmp  (sid,name,units, sqltmp) values(%d, '%s','%s','{\"arg0\":\"%d\"}')";

		String sqltmpSQL = String.format(sqltmp, sid, name, unit, ifid);
		try {
			insert(sqltmpSQL);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private long CreateClassInfo(Map<String, Object> TABLE_INF) {
		String formatI = "INSERT INTO "
				+ DB_HOR_PRE
				+ "classinfo (title,view_name,idesc,ctime)values ('%s','%s','%s', %d)";

		long tpid = 0;
		int now = time();
		Map<String, Object> TITLE = null;
		String name = "";
		String id = TABLE_INF.get("id").toString();
		try {
			TITLE = (Map<String, Object>) TABLE_INF.get("TITLE");
			name = TITLE.get("volume").toString();
		} catch (ClassCastException e) {
			// TODO: handle exception
			name = TABLE_INF.get("TITLE").toString();
		}

		String sqlI = String.format(formatI, name, id, "", now);

		try {
			tpid = insert(sqlI, true);
			CommitDB();

		} catch (SQLException e) {
			// TODO Auto-generated catch block

		}

		return tpid;
	}

	private long CreateUserSQL(long cid, String name,Map<String, String> Fields) {
		
		String where = "";
		int L=2;
		String fielas = "";
		for (String key : Fields.keySet()) {
			
			if(key.equals("area")) continue;
			
			
			where += " and act_v"+L+" =&apos;"+Fields.get(key)+"&apos;";
			fielas += "act_v"+L+" as "+key+",";
			L++;
		}
		where = "and 1=1 "+where ;
		
		String usql = "SELECT "+fielas+" act_v0 as volume, act_v1 as dial  FROM "
				+ DB_HOR_PRE + "class WHERE rule=@arg0@  "+where+"  ORDER BY  dial";
		String jsonTmp = "[[\"arg0\",\"a01010101\",\"TEXT\",\"字段表名\"]]";
		String format = " insert INTO "
				+ DB_HOR_PRE
				+ "usersql (name,usql, dtype, sql_type, sqltmp, input_data, uview,cid, uid)values('%s','%s', %d, %d, '%s', %d, '%s' ,%d, %d);";
		String sql = String.format(format, name, usql, 0, 1, jsonTmp, 0, "",
				cid, 1);
		long tpid = 0;
				
		String checkSQLtmp = "select id from " + DB_HOR_PRE
				+ "usersql where cid=%d and name='%s'";
		String checkSQL = String.format(checkSQLtmp, cid, name);
		Map<String, Object> cMap = FetchOne(checkSQL);
		if (cMap != null) {
			//echo("===id:" + cMap.get("id"));
			return Long.valueOf(cMap.get("id").toString());
		}
		try {
			//echo(sql);
			tpid = insert(sql, true);
			CommitDB();

		} catch (SQLException e) {
			// TODO Auto-generated catch block

		}

		return tpid;
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> setWhere(String id) {
		boolean isLoop = true;
		Map<String, String> Field = new HashMap<>();
		String Col = "getMetaInfo_" + statsField;
		String DBwhere = "{\"GET_META_INFO.METADATA_INF.TABLE_INF.id\":{\"$eq\":\""
				+ id + "\"}}";
		//echo(DBwhere);
		Map<String, Object> jMap = new HashMap<String, Object>();
		jMap.put("GET_META_INFO.METADATA_INF.CLASS_INF.CLASS_OBJ", 1);

		String json = JSON.toJSONString(jMap);
		MGDB mgdb = new MGDB();

		String cid="";
		mgdb.JsonWhere(DBwhere);

		mgdb.SetCollection(Col);
		mgdb.JsonColumn(json);

		mgdb.setLimit(1);

		isLoop = mgdb.FetchList();
		if (isLoop) {
			Map<String, Object> map = mgdb.GetValueLoop();
			if (map == null)
				return null;
			Map<String, Object> GET_META_INFO = (Map<String, Object>) map
					.get("GET_META_INFO");
			Map<String, Object> METADATA_INF = (Map<String, Object>) GET_META_INFO
					.get("METADATA_INF");

			if (!METADATA_INF.containsKey("CLASS_INF")) {
				echo(map);
				return null;
			}
			
			Map<String, Object> CLASS_INF = (Map<String, Object>) METADATA_INF
					.get("CLASS_INF");
			List<Map<String, Object>> CLASS_OBJ = (List<Map<String, Object>>) CLASS_INF
					.get("CLASS_OBJ");
			List<Map<String, Object>> CLAZZ ;
			for (Map<String, Object> Cmap : CLASS_OBJ) {
				cid = Cmap.get("id").toString();
				if(cid.equals("area")) continue;
				if(!Cmap.containsKey("CLASS")) continue;
				
				
				try {
					CLAZZ = (List<Map<String, Object>>) Cmap.get("CLASS");
				} catch (ClassCastException e) {
					// TODO: handle exception
					CLAZZ = new ArrayList<>();
					Map<String, Object> nMap= (Map<String, Object>) Cmap.get("CLASS");
					CLAZZ.add(nMap);
				}

				Field.put(cid, CLAZZ.get(0).get("code").toString());
				
				//Where = " and "+cid+" ='"+CLAZZ.get(0).get("code")+"'";
			}
		}

		//return "and 1=1 "+Where ;
		return Field;
	
	}

	@SuppressWarnings("unchecked")
	private long Category(Map<String, Object> TABLE_INF) {
		long mainpid = 0, subpid, statpid;
		String mainCode = "", subCode = "", static_name;
		if (!TABLE_INF.containsKey("TITLE")) {

			return 0;
		}

		Map<String, Object> MAIN_CATEGORY = (Map<String, Object>) TABLE_INF
				.get("MAIN_CATEGORY");

		mainCode = MAIN_CATEGORY.get("code").toString();
		String cid = CheckCategory(mainCode, pid);
		if (cid == null) {
			mainpid = UpdateCategory(pid, MAIN_CATEGORY.get("volume")
					.toString(), mainCode);
		} else {
			mainpid = Long.valueOf(cid);
		}

		Map<String, Object> SUB_CATEGORY = (Map<String, Object>) TABLE_INF
				.get("SUB_CATEGORY");

		subCode = SUB_CATEGORY.get("code").toString();
		String subcid = CheckCategory(subCode, mainpid);

		if (subcid == null) {
			subpid = UpdateCategory(mainpid, SUB_CATEGORY.get("volume")
					.toString(), subCode);
		} else {
			subpid = Long.valueOf(subcid);
		}

		static_name = TABLE_INF.get("STATISTICS_NAME").toString();

		String statcid = CheckCategory(String.valueOf(static_name.hashCode()),
				subpid);

		if (statcid == null) {
			statpid = UpdateCategory(subpid, static_name,
					String.valueOf(static_name.hashCode()));
			return statpid;
		} else {
			return Long.valueOf(statcid);
		}
	}

	private String CheckCategory(String desc, long id) {
		String format = "select id from " + DB_HOR_PRE
				+ "classify where idesc='%s' and pid=%d limit 1";
		String sql = String.format(format, desc, id);
		Map<String, Object> res;
		res = FetchOne(sql);
		if (res != null && res.size() == 1)
			return res.get("id").toString();

		return null;
	}

	private long UpdateCategory(long id, String name, String desc) {
		long tpid = 0;
		String format = "insert INTO " + DB_HOR_PRE + "classify "
				+ "(pid ,name,ctime, uid, isshare, idesc)"
				+ "values(%d,'%s', %d, %d, %d, '%s');";
		String sql = "";
		sql = String.format(format, id, name, time(), 1, 1, desc);

		try {
			tpid = insert(sql, true);
			CommitDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block

		}

		return tpid;
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// TODO Auto-generated method stub

		HikariConnectionPool hcp = HikariConnectionPool.getInstance();
		hcp.GetCon();

		pid = mConfig.GetInt("jp.pid");
		echo("0");
		FindJPJson();
		echo("jp data end!");
	}

	@Override
	public String getGroup() {
		// TODO Auto-generated method stub
		return "Group_" + SliceName(stdClass);
	}

	@Override
	public int withRepeatCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int withIntervalInSeconds() {
		// TODO Auto-generated method stub
		return 0;
	}
}
