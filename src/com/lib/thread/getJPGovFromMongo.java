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
import com.mysql.jdbc.log.Log;

public class getJPGovFromMongo extends SimpleQuartz implements Job {
	private String statsField = "06";

	private long pid;
	private List<Map<String, Object>> cList;

	public getJPGovFromMongo() {
		// TODO Auto-generated method stub
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
	}

	@SuppressWarnings("unchecked")
	private void Classify() {
		MGDB mgdb = new MGDB();
		String Col = "getMetaInfo_" + statsField;
		mgdb.SetCollection(Col);
		int offset = 0;
		boolean isLoop = true;
		long mainpid = 0;
		String mainCode = "", subCode = "";

		Map<String, Object> jMap = new HashMap<String, Object>();
		jMap.put("GET_META_INFO.METADATA_INF.TABLE_INF", 1);
		String json = JSON.toJSONString(jMap);
		mgdb.JsonColumn(json);

		while (isLoop) {

			mgdb.setDBOffset(offset);
			mgdb.setLimit(500);
			offset += 500;
			isLoop = mgdb.FetchList();
			if (isLoop == false)
				break;

			while (true) {
				Map<String, Object> map = mgdb.GetValueLoop();
				if (map == null)
					break;
				Map<String, Object> GET_META_INFO = (Map<String, Object>) map
						.get("GET_META_INFO");
				Map<String, Object> METADATA_INF = (Map<String, Object>) GET_META_INFO
						.get("METADATA_INF");

				if (!METADATA_INF.containsKey("TABLE_INF")) {
					echo(map);
					continue;
				}

				Map<String, Object> TABLE_INF = (Map<String, Object>) METADATA_INF
						.get("TABLE_INF");
				// Map<String, Object> TITLE;

				if (!TABLE_INF.containsKey("TITLE")) {
					echo(map);
					continue;
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
					UpdateCategory(mainpid, SUB_CATEGORY.get("volume")
							.toString(), subCode);
				} else {
					echo(SUB_CATEGORY.get("volume") + " mainpid:" + mainpid
							+ " subcode:" + subCode);
				}

			}

		}

	}

	private void getMetaInfo() {

		int offset = 0, limit = 1;
		boolean isLoop = true;
		cList = getClassIfy();

		while (isLoop) {
			isLoop = MetaInfoMogo(offset);
			offset += limit;
		}
		cList = null;
	}

	private boolean MetaInfoMogo(int offset) {
		int limit = 1;
		MGDB mgdb = new MGDB();

		String Col = "getMetaInfo_" + statsField;
		mgdb.SetCollection(Col);

		Map<String, Object> jMap = new HashMap<String, Object>();
		jMap.put("GET_META_INFO", 1);
		String json = JSON.toJSONString(jMap);
		mgdb.JsonColumn(json);

		mgdb.setDBOffset(offset);
		mgdb.setLimit(limit);
		boolean isLoop = true;
		isLoop = mgdb.FetchList();
		if (isLoop) {

			while (true) {
				Map<String, Object> rmap = mgdb.GetValueLoop();
				if (rmap == null)
					break;
				ViewMetaInfo(rmap);
				rmap = null;
			}
		}
		mgdb = null;
		return isLoop;

	}

	private List<Map<String, Object>> getClassIfy() {
		String sql = "select id,name  from " + DB_HOR_PRE
				+ "classify where idesc!=''";

		List<Map<String, Object>> res = null;

		try {
			res = FetchAll(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	@SuppressWarnings("unchecked")
	private void ViewMetaInfo(Map<String, Object> json) {

		// echo(json);
		Map<String, Object> GET_META_INFO = (Map<String, Object>) json
				.get("GET_META_INFO");
		// echo(GET_STATS_DATA);

		Map<String, Object> METADATA_INF = (Map<String, Object>) GET_META_INFO
				.get("METADATA_INF");

		Map<String, Object> TABLE_INF = (Map<String, Object>) METADATA_INF
				.get("TABLE_INF");
		String statsDataId = TABLE_INF.get("id").toString();

		Map<String, Object> TITLE = null;
		String name = "";

		try {
			TITLE = (Map<String, Object>) TABLE_INF.get("TITLE");
			name = TITLE.get("volume").toString();
		} catch (ClassCastException e) {
			// TODO: handle exception
			name = TABLE_INF.get("TITLE").toString();
		}

		String where = "";

		String code = "", cname = "", level = "", parentCode = "", unit = "";
		int L = 0;
		String objid = "";
		String objname = "";
		String views = "", act = "", ViewClazz = "";
		String ClazzFormat = "(%s, '%s','%s','%s','%s','%s','%s','%s','%s')";
		Map<String, Object> CLASS_INF = (Map<String, Object>) METADATA_INF
				.get("CLASS_INF");
		List<Map<String, Object>> CLASS_OBJ = (List<Map<String, Object>>) CLASS_INF
				.get("CLASS_OBJ");
		int size = CLASS_OBJ.size();
		Map<String, Object> map = null;
		for (int m = 0; m < size; m++) {
			map = CLASS_OBJ.get(m);
			objid = map.get("id").toString();
			// KeyList.add("" + objid);
			objname = map.get("name").toString();
			act = "act_v" + Integer.toHexString(L);
			views += act + " as " + objid + ",";
			L++;

			if (map.get("CLASS") instanceof List) {
				List<Map<String, Object>> CLAZZL = (List<Map<String, Object>>) map
						.get("CLASS");

				for (int i = 0; i < CLAZZL.size(); i++) {
					level = "";
					parentCode = "";
					code = CLAZZL.get(i).get("code").toString();
					cname = CLAZZL.get(i).get("name").toString();
					if (CLAZZL.get(i).containsKey("level")) {
						level = CLAZZL.get(i).get("level").toString();
					}
					if (CLAZZL.get(i).containsKey("parentCodeString")) {
						parentCode = CLAZZL.get(i).get("parentCodeString")
								.toString();
					}
					if (CLAZZL.get(i).containsKey("unit")) {
						unit = CLAZZL.get(i).get("unit").toString();
					}

					// ViewClazz =
					// "rule,objid,objname,code,name,level,parentcode,otype";
					ViewClazz += String
							.format(ClazzFormat, "RULE", objid, objname, code,
									cname, level, parentCode, unit, "L0")
							+ ",";
				}
				code = CLAZZL.get(0).get("code").toString();
				cname = CLAZZL.get(0).get("name").toString();
			} else {
				level = "";
				parentCode = "";

				Map<String, Object> CLAZZM = (Map<String, Object>) map
						.get("CLASS");
				if (CLAZZM == null) {
					CLAZZM = map;
					code = "";
				} else {
					code = CLAZZM.get("code").toString();
				}
				cname = CLAZZM.get("name").toString();
				if (CLAZZM.containsKey("level")) {
					level = CLAZZM.get("level").toString();
				}
				if (CLAZZM.containsKey("parentCodeString")) {
					parentCode = CLAZZM.get("parentCodeString").toString();
				}
				if (CLAZZM.containsKey("unit")) {
					unit = CLAZZM.get("unit").toString();
				}
				// ViewClazz =
				// "rule,objid,objname,code,name,level,parentcode,otype";
				ViewClazz += String.format(ClazzFormat, "RULE", objid, objname,
						code, cname, level, parentCode, unit, "L0") + ",";

			}
			if (!objid.equals("area")) {
				where += "j." + objid + "=&apos;" + code + "&apos; and ";
			}
		}

		String usqlFormat = "SELECT " + "(SELECT name " + "FROM i@arg0@ "
				+ "WHERE objid=&apos;area&apos; "
				+ "        AND j.area=code limit 1 ) AS dial, j.volume "
				+ "FROM j@arg0@ j " + "WHERE  %s " + "ORDER BY  j.area ;";
		// String jsonTmp = "[[\"arg0\",\"a01010101\",\"TEXT\",\"字段表名\"]]";
		List<Object> jsonTmp = new ArrayList<>();

		// KeyList.add("$");
		List<Object> tmp = new ArrayList<>();
		tmp.add("arg0");
		tmp.add(statsDataId);
		tmp.add("VARCHAR");
		tmp.add(name);
		jsonTmp.add(tmp);
		where += " j.area!=&apos;00000&apos;";

		Map<String, Object> SUB_CATEGORY = (Map<String, Object>) TABLE_INF
				.get("SUB_CATEGORY");

		String subvolume = SUB_CATEGORY.get("volume").toString();
		String cid = "";
		for (Map<String, Object> cmap : cList) {
			if (cmap.get("name").toString().equals(subvolume)) {
				cid = cmap.get("id").toString();
				break;
			}
		}

		String TmpJson = JSON.toJSONString(jsonTmp);
		String usql = String.format(usqlFormat, where);
		String format = " insert INTO "
				+ DB_HOR_PRE
				+ "usersql (name,sql, dtype, sql_type, sqltmp, input_data, uview,cid, uid)values('%s','%s', %d, %d, '%s', %d, '%s' ,%s, %d);";
		String sql = String.format(format, name, usql, 0, 1, TmpJson, 0, "",
				cid, 1);

		try {
			insert(sql);
			CommitDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block

		}
		act = "act_v" + Integer.toHexString(L);
		views += act + " as volume ,";
		act = "act_v" + Integer.toHexString(L + 1);
		views += act + " as id";

		long rule = classInfo(name, statsDataId, views);
		ViewClazz = ViewClazz.replaceAll("RULE", rule + "");
		ViewClazz = ViewClazz.substring(0, ViewClazz.length() - 1);
		getMetaInfoClazz(rule, statsDataId, ViewClazz);
	}

	private long classInfo(String title, String view, String views) {
		long tpid = 0;
		String format = "select id from " + DB_HOR_PRE
				+ "classinfo where view_name ='j%s' limit 1";
		String sql = String.format(format, view);
		Map<String, Object> res;

		res = FetchOne(sql);
		if (res != null && res.size() == 1)
			return Long.valueOf(res.get("id").toString());

		format = "INSERT INTO "
				+ DB_HOR_PRE
				+ "classinfo (title,view_name,idesc,ctime)values ('%s','j%s','%s', %d)";
		sql = String.format(format, title, view, title, time());
		try {
			tpid = insert(sql, true);
			CommitDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
		// String mView = views.substring(0, views.length()-1);

		return tpid;
	}

	private void getMetaInfoClazz(long rule, String statsDataId,
			String ViewClazz) {
		String format = "insert INTO "
				+ DB_HOR_PRE
				+ "class (rule,act_v0,act_v1,act_v2,act_v3,act_v4,act_v5,act_v6,act_v7) values %s";
		String sql = String.format(format, ViewClazz);

		try {
			insert(sql);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String views = "act_v0 as objid, act_v1 as objname, act_v2 as code, act_v3 as name, act_v4 as level, act_v5 as parentcode,act_v6 as unit";
		format = "CREATE OR REPLACE VIEW i%s AS SELECT %s FROM " + DB_HOR_PRE
				+ "class WHERE rule=%d and act_v7='L0'";
		sql = String.format(format, statsDataId, views, rule);
		// echo(sql);
		try {
			dbcreate(sql);
			CommitDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void clazz() {

		int offset = 0, limit = 1;
		boolean isLoop = true;

		while (isLoop) {
			isLoop = clazzLoop(offset);
			offset += limit;
		}

	}

	private boolean clazzLoop(int offset) {
		int limit = 1;
		boolean isLoop = true;
		Map<String, Object> jMap = new HashMap<String, Object>();
		jMap.put("GET_STATS_DATA.STATISTICAL_DATA.TABLE_INF.id", 1);
		jMap.put(
				"GET_STATS_DATA.STATISTICAL_DATA.TABLE_INF.SUB_CATEGORY.volume",
				1);
		jMap.put("GET_STATS_DATA.STATISTICAL_DATA.DATA_INF.VALUE", 1);
		String json = JSON.toJSONString(jMap);
		MGDB mgdb = new MGDB();

		String Col = "getStatsData_" + statsField;
		mgdb.SetCollection(Col);
		mgdb.JsonColumn(json);

		mgdb.setDBOffset(offset);
		mgdb.setLimit(limit);

		isLoop = mgdb.FetchList();
		Map<String, Object> rmap;
		// echo("offset: " + offset);
		if (isLoop) {
			while (true) {
				rmap = mgdb.GetValueLoop();
				if (rmap == null)
					break;
				if (clazzPar(rmap) == false)
					break;
			}
		}
		return isLoop;
	}

	@SuppressWarnings("unchecked")
	private boolean clazzPar(Map<String, Object> rmap) {
		int L = 0;
		String formatInfo = "select id from " + DB_HOR_PRE
				+ "classinfo where view_name='j%s'  limit 1 ";
		String formatClazz = "(%s, %s)";
		// String formatView = "";
		String ValueClazz = "";
		String sqlInfo = "", act = "", values = "";
		String views = "";

		Map<String, Object> GET_STATS_DATA = (Map<String, Object>) rmap
				.get("GET_STATS_DATA");
		Map<String, Object> STATISTICAL_DATA = (Map<String, Object>) GET_STATS_DATA
				.get("STATISTICAL_DATA");
		Map<String, Object> TABLE_INF = (Map<String, Object>) STATISTICAL_DATA
				.get("TABLE_INF");
		Map<String, Object> DATA_INF = (Map<String, Object>) STATISTICAL_DATA
				.get("DATA_INF");

		String id = TABLE_INF.get("id").toString();

		if (!(DATA_INF.get("VALUE") instanceof List)) {
			echo("ramp value is empty:" + rmap);
			return false;
		}
		List<Map<String, Object>> VALUE = (List<Map<String, Object>>) DATA_INF
				.get("VALUE");

		sqlInfo = String.format(formatInfo, id);
		Map<String, Object> res = FetchOne(sqlInfo);
		if (res == null) {
			echo("id:" + id);
			return false;
		}

		String rule = res.get("id").toString();

		views = "";
		int m = 0;
		echo("value size:" + VALUE.size() + " id:" + id);
		int size = VALUE.size();
		Map<String, Object> map = null;
		for (int l = 0; l < size; l++) {
			map = VALUE.get(l);
			L = 0;
			values = "";

			for (String key : map.keySet()) {
				if (key.equals("unit"))
					continue;
				values += "'" + map.get(key) + "',";
				act = "act_v" + Integer.toHexString(L);
				if (m == 0) {
					views += act + " AS " + key + ",";
				}
				L++;

			}
			values = values + "'L1'";
			m++;
			ValueClazz += String.format(formatClazz, rule, values) + ",";
			if (m == 50) {
				SaveClazz(ValueClazz, L, id, views, rule);
				m = 1;
				ValueClazz = "";
			}
		}

		ValueClazz = "";
		return true;
	}

	private void SaveClazz(String ValueClazz, int L, String id, String views,
			String rule) {
		String fields = "", act = "", formatView = "", sqlView;
		String formatI = " insert INTO " + DB_HOR_PRE
				+ "class (rule,%s)values %s;";
		String sqlI = "";
		if (ValueClazz.length() > 0) {

			for (int i = 0; i < L; i++) {
				act = "act_v" + Integer.toHexString(i);

				fields += act + ",";
			}

			formatView = "CREATE OR REPLACE VIEW j%s AS SELECT %s FROM "
					+ DB_HOR_PRE + "class WHERE rule=%s and act_v%d='L1'";
			sqlView = String.format(formatView, id,
					views.substring(0, views.length() - 1), rule, L);

			fields = fields + "act_v" + Integer.toHexString(L);

			ValueClazz = ValueClazz.substring(0, ValueClazz.length() - 1);

			sqlI = String.format(formatI, fields, ValueClazz);

			try {
				insert("DROP VIEW IF EXISTS j" + id);
				dbcreate(sqlView);
				insert(sqlI);
				CommitDB();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ValueClazz = "";
	}

	// //////////////////////////////////////////
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
				if(key.equals("time")) continue;
				if(L-2 < FieldS.size()){
					field += "act_v" + L +",";
					L++;
				}
				SQLvalue += "'"+vmap.get(key)+"',";
			}
			SQLvalue += "'"+vmap.get("volume")+"','"+vmap.get("time")+"', "+rule+"),";
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
			
			if(key.equals("time")) continue;
			
			
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
				if(cid.equals("time")) continue;
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
		// Classify();
		// echo("1");
		// getMetaInfo();
		// echo("2");
		// clazz();
		// echo("3");
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
