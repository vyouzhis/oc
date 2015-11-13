package com.lib.thread;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BaseRapidThread;
import org.ppl.net.cUrl;

import com.alibaba.fastjson.JSON;


///select  (select name from i0000030001 where objid='area' and j.area=code limit 1 ) as area, area, j.volume from j0000030001 j where j.cat01='00700' and j.cat02='000' and j.cat03='000' and j.area!='00000'  order by j.area  ; 
public class getJPGovData extends BaseRapidThread {
	private cUrl curl;
	private String url;
	private static String Ver = "2.0";
	private static String appId = "abb68400ed0dd8e8828b6d8b3e32154c111561b4";
	// private static String lang = "E";
	private static int limit = 1000;
	private long pid;
	private String statsField = "";
	private int tolNumber=0;
	private List<String> KeyList;

	@Override
	public String title() {
		// TODO Auto-generated method stub
		String className = this.getClass().getCanonicalName();

		return _CLang(SliceName(className));
	}

	@Override
	public void Run() {
		// TODO Auto-generated method stub
		curl = new cUrl();
		pid = mConfig.GetInt("jp.pid");
		int startPosition = 0;
		KeyList = new ArrayList<>();
		boolean StatsList = true;
		while (StatsList) {
			StatsList = getStatsList(startPosition);
			startPosition += limit;
		}

		echo("getJPGovData end... ");
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
	private boolean getStatsList(int startPosition) {

		url = "http://api.e-stat.go.jp/rest/" + Ver
				+ "/app/json/getStatsList?appId=" + appId + "&limit=" + limit
				+ "&startPosition=" + startPosition + "&statsField="
				+ statsField;
		// if(startPosition > 100) return; // ===========================
		String res = "";
		int loopTime=0;
		while (true) {
			res = curl.httpGet(url);
			if (res == null || res.length() < 10) {
				echo("url:" + url);
				if(startPosition>0 && tolNumber <= startPosition) return false;
			}else {
				break;
			}
			if(loopTime>5) return false;
			loopTime++;
		}
		
		Map<String, Object> json = JSON.parseObject(res, Map.class);
		Map<String, Object> GET_STATS_LIST = (Map<String, Object>) json
				.get("GET_STATS_LIST");
		Map<String, Object> RESULT = (Map<String, Object>) GET_STATS_LIST
				.get("RESULT");
		if (toInt(RESULT.get("STATUS")) != 0) {
			echo("STATUS:" + RESULT.get("STATUS"));
			echo("error:" + RESULT.get("ERROR_MSG"));
			echo("getStatsList startPosition:" + startPosition);
			return false;
		}

		Map<String, Object> DATALIST_INF = (Map<String, Object>) GET_STATS_LIST
				.get("DATALIST_INF");

		if(tolNumber==0){
			tolNumber = toInt(DATALIST_INF.get("NUMBER"));
		}
		 
//		Map<String, Object> RESULT_INF = (Map<String, Object>) DATALIST_INF
//				.get("RESULT_INF");
//		if (!RESULT_INF.containsKey("NEXT_KEY")) {
//			echo("nextkey url:" + url);
//			return false;
//		}
		// startPosition += limit;
		if(!DATALIST_INF.containsKey("TABLE_INF")) return false;
		
		List<Map<String, Object>> TABLE_INF = (List<Map<String, Object>>) DATALIST_INF
				.get("TABLE_INF");
		if(TABLE_INF==null || TABLE_INF.size()==0) return false;
		
		String mainCode = "", subCode = "";
		long mainpid = 0, subpid = 0;
		// int n=0, L=TABLE_INF.size();
		String title = "";
		Map<String, Object> TITLE = null;
		for (Map<String, Object> map : TABLE_INF) {
			// echo("n:"+n+" size:"+L);
			// n++;
			if (!map.containsKey("TITLE"))
				continue;

			try {
				TITLE = (Map<String, Object>) map.get("TITLE");
				title = TITLE.get("$").toString();
			} catch (ClassCastException e) {
				// TODO: handle exception
				// echo("error statsField:"+statsField+" startPosition:"+startPosition);
				// continue;
				title = map.get("TITLE").toString();
			}

			Map<String, Object> MAIN_CATEGORY = (Map<String, Object>) map
					.get("MAIN_CATEGORY");

			if (!mainCode.equals(MAIN_CATEGORY.get("@code").toString())
					|| mainpid == 0) {
				mainCode = MAIN_CATEGORY.get("@code").toString();
				String cid = CheckCategory(mainCode, pid);
				if (cid == null) {
					mainpid = UpdateCategory(pid, MAIN_CATEGORY.get("$")
							.toString(), mainCode);
				} else {
					mainpid = Long.valueOf(cid);
				}
			}

			Map<String, Object> SUB_CATEGORY = (Map<String, Object>) map
					.get("SUB_CATEGORY");
			if (!subCode.equals(SUB_CATEGORY.get("@code").toString())
					|| subpid == 0) {
				subCode = SUB_CATEGORY.get("@code").toString();
				String subcid = CheckCategory(subCode, mainpid);

				if (subcid == null) {
					subpid = UpdateCategory(mainpid, SUB_CATEGORY.get("$")
							.toString(), subCode);
				} else {
					subpid = Long.valueOf(subcid);
				}
			}
			String id = map.get("@id").toString();
			KeyList.clear();
			long rule=getMetaInfo(id, subpid);
			
			boolean StatsData = true;
			long StatsData_startPosition = getNowStatsDataID(id);
			//long StatsData_startPosition = 0;
			//echo("StatsData_startPosition:"+StatsData_startPosition+" id:"+id);
			while (StatsData) {
				StatsData = getStatsData(StatsData_startPosition, id, title, rule);
				StatsData_startPosition += limit;
			}

		}
		echo("getStatsList new startPosition:" + startPosition + " statsField:"
				+ statsField);

		return true;
	}

	@SuppressWarnings("unchecked")
	private boolean getStatsData(long startPosition, String statsDataId,
			String title, long rule) {

		// echo("getStatsData m:"+m);
		long id = startPosition;
		String url = "http://api.e-stat.go.jp/rest/" + Ver
				+ "/app/json/getStatsData?appId=" + appId + "&statsDataId="
				+ statsDataId + "&metaGetFlg=N&limit=" + limit
				+ "&startPosition="+startPosition;
		//echo(url);
		// if(startPosition > 10) return; // ===========================
		String res = "";
		// echo("getStatsData statsDataId:"+statsDataId);		
		int loopTime=0;
		while (true) {
			res = curl.httpGet(url);
			if (res == null || res.length() < 10) {
				echo("url:" + url);				
			}else {
				break;
			}
			if(loopTime>5){
				echo("i am go out loopTime no."+statsDataId);
				return false;
			}
			loopTime++;
		}
		
		Map<String, Object> json = JSON.parseObject(res, Map.class);
		Map<String, Object> GET_STATS_DATA = (Map<String, Object>) json
				.get("GET_STATS_DATA");

		Map<String, Object> RESULT = (Map<String, Object>) GET_STATS_DATA
				.get("RESULT");

		if (toInt(RESULT.get("STATUS")) != 0) {
			echo("STATUS:" + RESULT.get("STATUS"));
			echo("error:" + RESULT.get("ERROR_MSG"));
			echo("getStatsData startPosition:" + startPosition
					+ " statsDataId:" + statsDataId);
			return false;
		}

		Map<String, Object> STATISTICAL_DATA = (Map<String, Object>) GET_STATS_DATA
				.get("STATISTICAL_DATA");

		

		Map<String, Object> DATA_INF = (Map<String, Object>) STATISTICAL_DATA
				.get("DATA_INF");
		List<Map<String, Object>> VALUE = null;
		if(!(DATA_INF.get("VALUE") instanceof List)){
			echo("i am go out VALUE no."+statsDataId);
			return false;
		}
		
		VALUE = (List<Map<String, Object>>) DATA_INF.get("VALUE");
		
		String fields = "",  values = "", act="";
		int L = 0;
		
		String sameValue = "", format = "(%d, %s)";
		// echo("VALUE:"+VALUE.size());
		
		for (Map<String, Object> map : VALUE) {
			values = "";
			L = 0;
			for (String key : KeyList) {				
				values += "'" + map.get(key) + "',";				
				L++;				
			}

			values = values + "'"+id+"','L1'";
			id++;
			sameValue += String.format(format, rule, values) + ",";
		}
		if (sameValue.length() > 0) {
			for (int i = 0; i < L; i++) {
				act = "act_v" + Integer.toHexString(i); 
				fields += act + ",";
			}
			fields = fields+"act_v"+Integer.toHexString(L)+","+"act_v"+Integer.toHexString(L+1);
			sameValue = sameValue.substring(0, sameValue.length()-1);
			clazz(fields, sameValue);
		}

//		if (VALUE.size() < limit){
//			echo("i am go out limit no."+statsDataId);
//			return false;
//		}
		
		Map<String, Object> RESULT_INF = (Map<String, Object>) STATISTICAL_DATA
				.get("RESULT_INF");
		if (!RESULT_INF.containsKey("NEXT_KEY"))
			return false;

		return true;
	}

	private long getNowStatsDataID(String statsDataId) {

		String format = "select id from j%s order by id::int desc limit 1;";
		String sql = String.format(format, statsDataId);
		Map<String, Object> res;

		res = FetchOne(sql);
		if (res != null && res.size() == 1) {
			long id = Long.valueOf(res.get("id").toString());
			return id;
		}

		return 0;
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
		//String mView = views.substring(0, views.length()-1);
		format = "CREATE OR REPLACE VIEW j%s AS SELECT %s FROM " + DB_HOR_PRE
				+ "class WHERE rule=%d and act_v%d='L1'";
		sql = String.format(format, view, views, tpid, views.split(",").length);
		// echo(sql);
		try {
			dbcreate(sql);
			
			CommitDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tpid;
	}

	private void clazz(String fields, String values) {
		String format = " insert INTO " + DB_HOR_PRE
				+ "class (rule,%s)values %s;";
		String sql = String.format(format, fields, values);
		// echo(sql);
		try {
			insert(sql);
			CommitDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private long getMetaInfo(String statsDataId, long cid) {
		String url = "http://api.e-stat.go.jp/rest/" + Ver
				+ "/app/json/getMetaInfo?appId=" + appId + "&statsDataId="
				+ statsDataId;
		// echo(url);

		String res = "";

		int loopTime=0;
		while (true) {
			res = curl.httpGet(url);
			if (res == null || res.length() < 10) {
				echo("url:" + url);				
			}else {
				
				break;
			}
			if(loopTime>5) return -1;
			loopTime++;
		}
		
		Map<String, Object> json = JSON.parseObject(res, Map.class);

		// echo(json);
		Map<String, Object> GET_META_INFO = (Map<String, Object>) json
				.get("GET_META_INFO");
		// echo(GET_STATS_DATA);
		Map<String, Object> RESULT = (Map<String, Object>) GET_META_INFO
				.get("RESULT");

		if (toInt(RESULT.get("STATUS")) != 0) {
			echo("STATUS:" + RESULT.get("STATUS"));
			echo("error:" + RESULT.get("ERROR_MSG"));

			return -1;
		}

		Map<String, Object> METADATA_INF = (Map<String, Object>) GET_META_INFO
				.get("METADATA_INF");

		Map<String, Object> TABLE_INF = (Map<String, Object>) METADATA_INF
				.get("TABLE_INF");

		Map<String, Object> TITLE = null;
		String name = "";

		try {
			TITLE = (Map<String, Object>) TABLE_INF.get("TITLE");
			name = TITLE.get("$").toString();
		} catch (ClassCastException e) {
			// TODO: handle exception
			// echo("error statsField:"+statsField+" startPosition:"+startPosition);
			// continue;
			name = TABLE_INF.get("TITLE").toString();
		}

		String usqlFormat = "SELECT "+
						    "(SELECT name "+
						    "FROM i@arg0@ "+
						    "WHERE objid='area' "+
						    "        AND j.area=code limit 1 ) AS dial, j.volume "+
							"FROM j@arg0@ j "+
							"WHERE j.cat01='00700' "+
							 "       AND j.cat02='000' "+
							  "      AND j.cat03='000' "+
							   "     AND j.area!='00000' "+
							"ORDER BY  j.area ;";
		// String jsonTmp = "[[\"arg0\",\"a01010101\",\"TEXT\",\"字段表名\"]]";
		List<Object> jsonTmp = new ArrayList<>();
		String where = "";

		String code = "", cname = "", level="", parentCode="", unit="";
		int m = 0, L=0;
		String objid = "";
		String objname = "";
		String views = "", act="", ViewClazz = "";
		String ClazzFormat="(%s, '%s','%s','%s','%s','%s','%s','%s','%s')";
		Map<String, Object> CLASS_INF = (Map<String, Object>) METADATA_INF
				.get("CLASS_INF");
		List<Map<String, Object>> CLASS_OBJ = (List<Map<String, Object>>) CLASS_INF
				.get("CLASS_OBJ");
		for (Map<String, Object> map : CLASS_OBJ) {
			objid = map.get("@id").toString();
			KeyList.add("@"+objid);
			objname = map.get("@name").toString();
			act = "act_v" + Integer.toHexString(L);
			views += act + " as " + objid + ",";
			L++;
			if (map.get("CLASS") instanceof List) {
				List<Map<String, Object>> CLAZZL = (List<Map<String, Object>>) map
						.get("CLASS");
				
				for (int i = 0; i < CLAZZL.size(); i++) {
					level = "";
					parentCode = "";
					code = CLAZZL.get(i).get("@code").toString();
					cname = CLAZZL.get(i).get("@name").toString();
					if(CLAZZL.get(i).containsKey("@level")){
						level = CLAZZL.get(i).get("@level").toString();
					}
					if(CLAZZL.get(i).containsKey("@parentCodeString")){
						parentCode = CLAZZL.get(i).get("@parentCodeString").toString();
					}
					if(CLAZZL.get(i).containsKey("@unit")){
						unit = CLAZZL.get(i).get("@unit").toString();
					}
					
					//ViewClazz = "rule,objid,objname,code,name,level,parentcode,otype";
					ViewClazz += String.format(ClazzFormat, "RULE", objid,objname,code,cname,level,parentCode,unit, "L0")+",";					
				}
				code = CLAZZL.get(0).get("@code").toString();
				cname = CLAZZL.get(0).get("@name").toString();
			} else {
				level = "";
				parentCode = "";
				Map<String, Object> CLAZZM = (Map<String, Object>) map
						.get("CLASS");
				code = CLAZZM.get("@code").toString();
				cname = CLAZZM.get("@name").toString();
				if(CLAZZM.containsKey("@level")){
					level = CLAZZM.get("@level").toString();
				}
				if(CLAZZM.containsKey("@parentCodeString")){
					parentCode = CLAZZM.get("@parentCodeString").toString();
				}
				if(CLAZZM.containsKey("@unit")){
					unit = CLAZZM.get("@unit").toString();
				}
				//ViewClazz = "rule,objid,objname,code,name,level,parentcode,otype";
				ViewClazz += String.format(ClazzFormat, "RULE", objid,objname,code,cname,level,parentCode,unit, "L0")+",";
				
			}
			
			if (m == 0) {
				List<Object> tmp = new ArrayList<>();
				tmp.add("arg" + m);
				tmp.add(map.get("@id"));
				tmp.add("VARCHAR");
				tmp.add(cname);
				jsonTmp.add(tmp);
				m++;
			}

			List<Object> tmp = new ArrayList<>();
			tmp.add("arg" + m);
			tmp.add(code);
			tmp.add("VARCHAR");
			tmp.add(cname);
			jsonTmp.add(tmp);

			where +=  objid + " = &apos;@arg" + m + "@&apos;";
			m++;
			if (m <= CLASS_OBJ.size()) {
				where += " and ";
			}
		}
		KeyList.add("$");
		String TmpJson = JSON.toJSONString(jsonTmp);
		String usql = String.format(usqlFormat, statsDataId, where);
		String format = " insert INTO "
				+ DB_HOR_PRE
				+ "usersql (name,sql, dtype, sql_type, sqltmp, input_data, uview,cid, uid)values('%s','%s', %d, %d, '%s', %d, '%s' ,%d, %d);";
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
		act = "act_v" + Integer.toHexString(L+1);
		views += act + " as id";
		
		long rule = classInfo(name, statsDataId, views);
		ViewClazz = ViewClazz.replaceAll("RULE", rule+"");
		ViewClazz = ViewClazz.substring(0, ViewClazz.length()-1);
		getMetaInfoClazz( rule,  statsDataId,  ViewClazz);
		return rule;
	}
	
	private void getMetaInfoClazz(long rule, String statsDataId, String ViewClazz) {
		String format = "insert INTO hor_class (rule,act_v0,act_v1,act_v2,act_v3,act_v4,act_v5,act_v6,act_v7) values %s";
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

}
