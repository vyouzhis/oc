package com.lib.thread;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.core.IsAnything;
import org.ppl.BaseClass.BaseRapidThread;
import org.ppl.db.MGDB;

import com.alibaba.fastjson.JSON;

public class getJPGovFromMongo extends BaseRapidThread {
	private String statsField = "";

	private long pid;
	private List<Map<String, Object>> cList;

	@Override
	public String title() {
		// TODO Auto-generated method stub
		String className = this.getClass().getCanonicalName();

		return _CLang(SliceName(className));
	}

	@Override
	public void Run() {
		// TODO Auto-generated method stub
		pid = mConfig.GetInt("jp.pid");
		Classify();
		echo("1");
		getMetaInfo();
		echo("2");
		clazz();
	}

	@Override
	public boolean isRun() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean Stop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void mailbox(Object o) {
		// TODO Auto-generated method stub
		statsField = (String) o;
	}

	@SuppressWarnings("unchecked")
	private void Classify() {
		MGDB mgdb = new MGDB();
		String Col = "getMetaInfo_" + statsField;
		mgdb.SetCollection(Col);
		int offset = 0;
		boolean isLoop = true;
		long mainpid = 0;
		String title = "", mainCode = "", subCode = "";

		Map<String, Object> jMap = new HashMap<String, Object>();
		jMap.put("GET_META_INFO.METADATA_INF.TABLE_INF", 1);
		String json = JSON.toJSONString(jMap);
		mgdb.JsonColumn(json);

		while (isLoop) {

			mgdb.setDBOffset(offset);
			mgdb.setLimit(500);
			offset += 500;
			isLoop = mgdb.FetchList();
			if (isLoop) {

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
						mainpid = UpdateCategory(pid,
								MAIN_CATEGORY.get("volume").toString(),
								mainCode);
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
					}

				}
			} else {
				break;
			}
		}

	}

	private void getMetaInfo() {
		MGDB mgdb = new MGDB();
		String Col = "getMetaInfo_" + statsField;
		mgdb.SetCollection(Col);
		int offset = 0;
		boolean isLoop = true;
		cList = getClassIfy();

		Map<String, Object> jMap = new HashMap<String, Object>();
		jMap.put("GET_META_INFO", 1);
		String json = JSON.toJSONString(jMap);
		mgdb.JsonColumn(json);

		while (isLoop) {

			mgdb.setDBOffset(offset);
			mgdb.setLimit(500);
			offset += 500;
			isLoop = mgdb.FetchList();
			if (isLoop) {

				while (true) {
					Map<String, Object> rmap = mgdb.GetValueLoop();
					if (rmap == null)
						break;
					ViewMetaInfo(rmap);
				}
			} else {
				break;
			}
		}

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
		for (Map<String, Object> map : CLASS_OBJ) {
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

		int offset = 0;
		boolean isLoop = true, r = true;
		cList = getClassIfy();

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

		while (isLoop) {

			mgdb.setDBOffset(offset);
			mgdb.setLimit(500);
			offset += 500;
			isLoop = mgdb.FetchList();
			if (isLoop) {
				while (r) {
					Map<String, Object> rmap = mgdb.GetValueLoop();
					if (rmap == null)
						break;
					r = clazzPar(rmap);
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	private boolean clazzPar(Map<String, Object> rmap) {
		int L = 0;
		String formatInfo = "select id from " + DB_HOR_PRE
				+ "classinfo where view_name='j%s'  limit 1 ";
		String formatClazz = "(%s, %s)";
		String formatView = "";
		String ValueClazz = "";
		String sqlView = "", sqlInfo = "", act = "", values = "", fields = "";
		String views = "";

		ValueClazz = "";
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

		ValueClazz = "";
		views = "";
		int m = 0;
		for (Map<String, Object> map : VALUE) {
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
			m = 1;
			ValueClazz += String.format(formatClazz, rule, values) + ",";
		}

		echo("view: " + id + " rule:" + rule);
		if (ValueClazz.length() > 0) {
			fields = "";
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
			// echo(ValueClazz);
			String formatI = " insert INTO " + DB_HOR_PRE
					+ "class (rule,%s)values %s;";
			String sqlI = String.format(formatI, fields, ValueClazz);
			// echo(sql);
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
		return true;
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
}
