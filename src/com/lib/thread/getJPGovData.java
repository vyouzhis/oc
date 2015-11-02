package com.lib.thread;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter.Magenta;
import org.ppl.BaseClass.BaseRapidThread;
import org.ppl.net.cUrl;

import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.jdbc2.optional.MysqlXid;

public class getJPGovData extends BaseRapidThread {
	private cUrl curl;
	private String url;
	private static String Ver = "2.0";
	private static String appId = "abb68400ed0dd8e8828b6d8b3e32154c111561b4";
	//private static String lang = "E";
	private static int limit = 1000;
	private long pid;

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

		getStatsList(0);
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

	}

	@SuppressWarnings("unchecked")
	private void getStatsList(int m) {
		int startPosition = m;

		url = "http://api.e-stat.go.jp/rest/" + Ver
				+ "/app/json/getStatsList?appId=" + appId + "&limit=" + limit
				+ "&startPosition=" + startPosition;
		//if(startPosition > 100) return;  // ===========================
		String res = curl.httpGet(url);
		if (res == null || res.length() < 10)
			return;

		Map<String, Object> json = JSON.parseObject(res, Map.class);
		Map<String, Object> GET_STATS_LIST = (Map<String, Object>) json
				.get("GET_STATS_LIST");
		Map<String, Object> RESULT = (Map<String, Object>) GET_STATS_LIST
				.get("RESULT");
		if (toInt(RESULT.get("STATUS")) != 0) {
			echo("STATUS:" + RESULT.get("STATUS"));
			echo("error:" + RESULT.get("ERROR_MSG"));
			echo("getStatsList startPosition:" + startPosition);
			return;
		}

		Map<String, Object> DATALIST_INF = (Map<String, Object>) GET_STATS_LIST
				.get("DATALIST_INF");

		// int number = toInt(DATALIST_INF.get("NUMBER"));
		// echo("number:"+number);
		Map<String, Object> RESULT_INF = (Map<String, Object>) DATALIST_INF
				.get("RESULT_INF");
		if (!RESULT_INF.containsKey("NEXT_KEY"))
			return;
		startPosition += limit;

		List<Map<String, Object>> TABLE_INF = (List<Map<String, Object>>) DATALIST_INF
				.get("TABLE_INF");
		String mainCode = "", subCode = "";
		long mainpid = 0, subpid = 0;
		for (Map<String, Object> map : TABLE_INF) {
			// echo("STAT_NAME:"+map.get("STAT_NAME"));
			// echo("TITLE:"+map.get("TITLE"));
			
			// echo("id:"+id);

			Map<String, Object> TITLE = (Map<String, Object>) map.get("TITLE");
			// echo("TITLE:"+TITLE.get("$"));

			Map<String, Object> MAIN_CATEGORY = (Map<String, Object>) map
					.get("MAIN_CATEGORY");
			// echo("MAIN_CATEGORY:"+MAIN_CATEGORY.get("$"));
			// echo("MAIN_CATEGORY desc:"+MAIN_CATEGORY.get("@code"));

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
			// echo("SUB_CATEGORY:"+SUB_CATEGORY.get("$"));
			// echo("SUB_CATEGORY code:"+SUB_CATEGORY.get("@code"));
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
			//UpdateCategory(subpid, TITLE.get("$").toString(), id.toString());
			getMetaInfo(id, subpid);
			getStatsData(0, id, TITLE.get("$").toString() );
		}
		echo("startPosition:" + startPosition);
		getStatsList(startPosition);
	}
	
	@SuppressWarnings("unchecked")
	private void getStatsData(int m, String statsDataId, String title) {
		int startPosition=m;
		
		String url="http://api.e-stat.go.jp/rest/"+Ver+"/app/json/getStatsData?appId="+appId+ 
				"&statsDataId="+statsDataId+"&metaGetFlg=N&limit="+limit+"&startPosition="+startPosition;
		//echo(url);		
		//if(startPosition > 10) return; // ===========================
		String res = curl.httpGet(url);
		if(res==null || res.length() < 10) return ;
		
		Map<String, Object> json = JSON.parseObject(res, Map.class);
		Map<String, Object> GET_STATS_DATA = (Map<String, Object>) json.get("GET_STATS_DATA");
		
		Map<String, Object> RESULT = (Map<String, Object>) GET_STATS_DATA.get("RESULT");
		
		if (toInt(RESULT.get("STATUS")) != 0) {
			echo("STATUS:" + RESULT.get("STATUS"));
			echo("error:" + RESULT.get("ERROR_MSG"));
			echo("getStatsData startPosition:" + startPosition);
			return;
		}
		
		Map<String, Object> STATISTICAL_DATA = (Map<String, Object>) GET_STATS_DATA.get("STATISTICAL_DATA");
				
		Map<String, Object> RESULT_INF =  (Map<String, Object>) STATISTICAL_DATA.get("RESULT_INF");
		if(!RESULT_INF.containsKey("NEXT_KEY")) return ;
		startPosition += limit;
		
		Map<String, Object> DATA_INF = (Map<String, Object>) STATISTICAL_DATA.get("DATA_INF");
		List<Map<String, Object>> VALUE = (List<Map<String, Object>>) DATA_INF.get("VALUE");
		String fields = "", act="", views="", values="";
		int L=0;
		String asKey = "";
		for (Map<String, Object> map:VALUE) {
			fields = ""; act="";views=""; values="";
			L=0;
			for (String key:map.keySet()) {
				if(key.indexOf("$")!=-1){ 
					asKey="volume";
				}else {
					asKey = key.replace("@", "");
				}
								
				act = "act_v" + Integer.toHexString(L);				
				
				views += act +" as "+asKey+",";
				fields += act + ",";
				
				values += "'"+map.get(key)+"',";
				L++;
			}
			
			fields = fields.substring(0, fields.length()-1);
			views = views.substring(0, views.length()-1);
			values = values.substring(0, values.length()-1);
			
			long rule=classInfo(title, statsDataId, views);
			
			clazz(rule, views, statsDataId, fields, values);
		
		}
			
		getStatsData(startPosition, statsDataId, title);
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
		String format = "select id from "+DB_HOR_PRE+"classinfo where view_name ='j%s' limit 1";
		String sql = String.format(format, view);
		Map<String, Object> res;
		
		res = FetchOne(sql);
		if(res!=null && res.size()==1) return Long.valueOf(res.get("id").toString());

		format = "INSERT INTO "
				+ DB_HOR_PRE
				+ "classinfo (title,view_name,idesc,ctime)values ('%s','j%s','%s', %d)";
		sql = String.format(format, title , view, title, time());
		try {
			tpid = insert(sql, true);
			CommitDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
		}
		
		format = "CREATE OR REPLACE VIEW j%s AS SELECT %s FROM "
				+ DB_HOR_PRE + "class WHERE rule=%d";
		sql = String.format(format,view, views, tpid);
		//echo(sql);
		try {
			dbcreate(sql);
			//CommitDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tpid;
	}
	
	private void clazz(long rule, String view_name ,String views, String fields, String values) {
		String format = " insert INTO "
				+ DB_HOR_PRE
				+ "class (rule,%s)values(%d, %s)";
		String sql = String.format(format, fields, rule, values);
		//echo(sql);
		try {
			insert(sql);
			CommitDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("unchecked")
	private void getMetaInfo(String statsDataId, long cid) {
		String url="http://api.e-stat.go.jp/rest/"+Ver+"/app/json/getMetaInfo?appId="+appId+  "&statsDataId="+statsDataId;
		//echo(url);
	
		String res = curl.httpGet(url);

		Map<String, Object> json = JSON.parseObject(res, Map.class);
		
		//echo(json);
		Map<String, Object> GET_META_INFO = (Map<String, Object>) json.get("GET_META_INFO");
		//echo(GET_STATS_DATA);
		Map<String, Object> RESULT = (Map<String, Object>) GET_META_INFO.get("RESULT");
		
		if (toInt(RESULT.get("STATUS")) != 0) {
			echo("STATUS:" + RESULT.get("STATUS"));
			echo("error:" + RESULT.get("ERROR_MSG"));
			
			return;
		}
			
		Map<String, Object> METADATA_INF = (Map<String, Object>) GET_META_INFO.get("METADATA_INF");
				
		Map<String, Object> TABLE_INF = (Map<String, Object>) METADATA_INF.get("TABLE_INF");
		
		Map<String, Object> TITLE = (Map<String, Object>) TABLE_INF.get("TITLE");
		String name = TITLE.get("$").toString();
		
		String usqlFormat = "SELECT volume, @arg0@ AS dial FROM j%s WHERE %s ORDER BY  dial";
		//String jsonTmp = "[[\"arg0\",\"a01010101\",\"TEXT\",\"字段表名\"]]";
		List<Object> jsonTmp = new ArrayList<>();
		String where = "";
		
		String code="", cname="";
		int m=0;
		Map<String, Object> CLASS_INF = (Map<String, Object>) METADATA_INF.get("CLASS_INF");
		List<Map<String, Object>> CLASS_OBJ = (List<Map<String, Object>>) CLASS_INF.get("CLASS_OBJ");
		for (Map<String, Object> map : CLASS_OBJ) {
			
			if(map.get("CLASS") instanceof List){
				List<Map<String, Object>> CLAZZL = (List<Map<String, Object>>) map.get("CLASS");
				code = CLAZZL.get(0).get("@code").toString();
				cname = CLAZZL.get(0).get("@name").toString();
			}else {
				Map<String, Object> CLAZZM = (Map<String, Object>) map.get("CLASS");
				code = CLAZZM.get("@code").toString();
				cname = CLAZZM.get("@name").toString();
			}
			if(m==0){
				List<Object> tmp = new ArrayList<>();
				tmp.add("arg"+m);
				tmp.add(map.get("@id"));
				tmp.add("VARCHAR");
				tmp.add(cname);
				jsonTmp.add(tmp);
				m++;
			}
						
			List<Object> tmp = new ArrayList<>();
			tmp.add("arg"+m);
			tmp.add(code);
			tmp.add("VARCHAR");
			tmp.add(cname);
			jsonTmp.add(tmp);
			
			where += map.get("@id")+" = &apos;@arg"+m+"@&apos;";
			m++;			
			if(m <= CLASS_OBJ.size()){
				where += " and ";
			}
		}
		
		String TmpJson = JSON.toJSONString(jsonTmp);
		String usql = String.format(usqlFormat, statsDataId, where);
		String format = " insert INTO "
				+ DB_HOR_PRE
				+ "usersql (name,sql, dtype, sql_type, sqltmp, input_data, uview,cid, uid)values('%s','%s', %d, %d, '%s', %d, '%s' ,%d, %d);";
		String sql = String.format(format, name, usql, 0, 1, TmpJson, 0, "",
				cid, 1);
		
		try {
			insert(sql);
			//CommitDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block

		}
	}

}
