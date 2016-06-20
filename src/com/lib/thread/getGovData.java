package com.lib.thread;

import java.awt.Stroke;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.db.HikariConnectionPool;
import org.ppl.net.cUrl;
import org.ppl.plug.Quartz.SimpleQuartz;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSON;


public class getGovData extends SimpleQuartz implements Job {
	cUrl curl;
	String SearchUrl = "http://data.stats.gov.cn/easyquery.htm";
	String LoginUrl = "http://data.stats.gov.cn/login.htm?m=login";
	
	public getGovData() {
		// TODO Auto-generated constructor stub
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
		
	}

	@SuppressWarnings("unchecked")
	public void govFetch() {
		curl = new cUrl();
		long pid = mConfig.GetInt("ggd.pid");
		List<Map<String, Object>> govJson;

		String user = "929398015@qq.com";
		String pwd = "asdQWE!@#";
		curl.addParams("username", user);
		curl.addParams("keyp", pwd);

		String res = curl.httpPost(LoginUrl);

		curl.clearParams();
		curl.addParams("dbcode", "hgyd");
		curl.addParams("id", "zb");
		curl.addParams("m", "getTree");
		curl.addParams("wdcode", "zb");

		res = curl.httpPost(SearchUrl);

		govJson = JSON.parseObject(res, List.class); // 获取所有的主树
		//echo(govJson);
		//echo("pid:"+pid);
		for (Map<String, Object> key : govJson) {
			subLoop(pid, key.get("id").toString(), key.get("name").toString(),
					(boolean) key.get("isParent"));
		}

	}

	@SuppressWarnings("unchecked")
	private void subLoop(long pid, String dataId, String name, boolean isParent) {
		long ucid = 0, subpid = 0;

		if (name.indexOf("2003") > 0 || name.indexOf("2004") > 0
				|| name.indexOf("2001") > 0)
			return;

		subpid = CreateClassify(pid, name);
		//echo("subpid:"+subpid);
		if (isParent == false) {

			ucid = CreateUserSQL(subpid, name);
			//echo("ucid:"+ucid);
			DataSave(dataId, ucid, name); // 保存数据

		} else {

			curl.clearParams();
			curl.addParams("dbcode", "hgyd");
			curl.addParams("id", dataId);
			curl.addParams("m", "getTree");
			curl.addParams("wdcode", "zb");
			String subRes = curl.httpPost(SearchUrl);

			List<Map<String, Object>> SubTreeJson = JSON.parseObject(subRes,
					List.class); // 获得二次的子节点

			for (Map<String, Object> sj : SubTreeJson) {
				subLoop(subpid, sj.get("id").toString(), sj.get("name")
						.toString(), (boolean) sj.get("isParent"));
			}
		}
	}

	private long CreateClassify(long pid, String name) {
		long tpid = 0;
		String format = "insert INTO " + DB_HOR_PRE + "classify "
				+ "(pid ,name,ctime, uid, isshare)"
				+ "values(%d,'%s', %d, %d, %d);";
		String sql = "";
		sql = String.format(format, pid, name, time(), 1, 1);
		String checkSQLtmp = "select id from "+DB_HOR_PRE+"classify where name='%s' and pid=%d; ";
		String checkSQL = String.format(checkSQLtmp, name, pid);
		Map<String, Object> cMap = FetchOne(checkSQL);
		if(cMap!=null){
			return Long.valueOf(cMap.get("id").toString());
		}
		try {
		
			tpid = insert(sql, true);
			//echo("tpid:"+tpid+" sql:"+sql);
			CommitDB();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block

		}
		
		return tpid;
	}

	private long CreateUserSQL(long cid, String name) {
		String usql = "SELECT month AS dial,  data AS volume  FROM @arg0@  ORDER BY  month";
		String jsonTmp = "[[\"arg0\",\"a01010101\",\"TEXT\",\"字段表名\"]]";
		String format = " insert INTO "
				+ DB_HOR_PRE
				+ "usersql (name,usql, dtype, sql_type, sqltmp, input_data, uview,cid, uid)values('%s','%s', %d, %d, '%s', %d, '%s' ,%d, %d);";
		String sql = String.format(format, name, usql, 0, 1, jsonTmp, 0, "",
				cid, 1);
		long tpid = 0;	
		
		String checkSQLtmp="select id from "+DB_HOR_PRE+"usersql where cid=%d and name='%s'";
		String checkSQL = String.format(checkSQLtmp, cid, name);
		Map<String, Object> cMap = FetchOne(checkSQL);
		if(cMap!=null){
			echo("===id:"+cMap.get("id"));
			return Long.valueOf(cMap.get("id").toString());
		}
		try {
			tpid = insert(sql, true);
			CommitDB();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block

		}

		return tpid;
	}

	@SuppressWarnings("unchecked")
	private void DataSave(String id, long pid, String cname) {
		Map<String, Object> resJson;
		String dfwds = "[{\"wdcode\":\"zb\",\"valuecode\":\"" + id + "\"}]";

		curl.clearParams();
		curl.addParams("colcode", "sj");
		curl.addParams("dbcode", "hgyd");
		curl.addParams("dfwds", dfwds);
		curl.addParams("k1", "1443084595325");
		curl.addParams("m", "QueryData");
		curl.addParams("rowcode", "zb");
		curl.addParams("wds", "[]");
		curl.httpPost(SearchUrl);

		dfwds = "[{\"wdcode\":\"sj\",\"valuecode\":\""+mConfig.GetValue("ggd.valuecode")+"\"}]";
		curl.clearParams();
		curl.addParams("colcode", "sj");
		curl.addParams("dbcode", "hgyd");
		curl.addParams("dfwds", dfwds);
		curl.addParams("k1", time() + "");
		curl.addParams("m", "QueryData");
		curl.addParams("rowcode", "zb");
		curl.addParams("wds", "[]");
		String res = curl.httpPost(SearchUrl);

		String format = " insert INTO "
				+ DB_HOR_PRE
				+ "class (rule,act_v0,act_v1, act_v2)values(%d, '%s','%s', '%s')";
		String sql = "";
		String formatI = "INSERT INTO "
				+ DB_HOR_PRE
				+ "classinfo (title,view_name,idesc,ctime)values ('%s','%s','%s', %d)";
		String view = "act_v0 as data, act_v1 as month, act_v2 as unit ";
		String viewformat = "CREATE OR REPLACE VIEW %s AS SELECT %s FROM "
				+ DB_HOR_PRE + "class WHERE rule=%d";

		String sqltmp = "insert INTO "
				+ DB_HOR_PRE
				+ "sqltmp  (sid,name,sqltmp) values(%d, '%s', '{\"arg0\":\"%s\"}')";
		String sqltmpSQL = "";
		
		String checkSQLtmp = "select id from "+DB_HOR_PRE+"classinfo where view_name = '%s' limit 1;";
		String checkSQL = "";
		String checkClzztmp = "select rule from "+DB_HOR_PRE+"class where rule=%d and act_v1='%s'  limit 1;";
		String checkClzz = "";
		resJson = JSON.parseObject(res, Map.class);

		if (resJson.get("returndata").toString().length() < 21)
			return;

		Map<String, Object> returndata = JSON.parseObject(
				resJson.get("returndata").toString(), Map.class);
		List<Object> datanodes = (List<Object>) returndata.get("datanodes");
		List<Object> wdnodes = (List<Object>) returndata.get("wdnodes");

		int now = time();

		Map<String, Object> mapNodes = (Map<String, Object>) wdnodes.get(0);
		List<Map<String, Object>> nodes = (List<Map<String, Object>>) mapNodes
				.get("nodes");
		// Map<String, Object> NameList = new HashMap<String, Object>();
		Map<String, Object> UnitList = new HashMap<String, Object>();

		Map<String, String> classList = new HashMap<>();
		Map<String, String> nameList = new HashMap<>();

		if (datanodes.size() == 0)
			return;
		String tmpView = "";
		for (Map<String, Object> map : nodes) {
			// echo(map.get("name")+"--"+map.get("code")+"--"+map.get("memo"));
			if (!tmpView.equals(map.get("code").toString())) {
				String sqlI = String.format(formatI, map.get("name"),
						map.get("code"), map.get("memo"), now);
				// echo(map.get("name"));
				classList.put(map.get("code").toString(), sqlI);
				tmpView = map.get("code").toString();
			}

			nameList.put(map.get("code").toString(), map.get("name").toString());
			UnitList.put(map.get("code").toString(), map.get("unit").toString());

		}
		long cid = 0;
		String val = "";
		String dnameo = "", dnamet = "";

		for (int j = 0; j < datanodes.size(); j++) {

			Map<String, Object> listMap = (Map<String, Object>) datanodes
					.get(j);
			Map<String, Object> data = (Map<String, Object>) listMap
					.get("data");
			val = "";
			if (data.get("data").toString().length() > 0) {
				val = String.format("%.2f", toFloat(data.get("data")));
			}
			String code = listMap.get("code").toString();
			String[] nt = code.split("_");
			String dname = nt[0].substring(3);
			String dtime = nt[1].substring(3);
			dtime = dtime.substring(0, 4) + "-" + dtime.substring(4);
			dnameo = dname;
			//echo("dnameo:"+dnameo+" dnamet:"+dnamet);
			
			checkSQL = String.format(checkSQLtmp, dname);
			Map<String, Object> cMap = FetchOne(checkSQL);
			if(cMap!=null){
				checkClzz = String.format(checkClzztmp, cMap.get("id"), dtime);
				cMap = FetchOne(checkClzz);
				if(cMap!=null) {
					echo("rule:"+cMap.get("rule")+ " time:"+dtime);
					continue;
				}
			}
			if (!dnameo.equals(dnamet)) {
				dnamet = dnameo;
				try {
					sqltmpSQL = String.format(sqltmp, pid, nameList.get(dname),
							dnamet);
					// echo(sqltmpSQL);
					
					insert(sqltmpSQL);

					long cidt = insert(classList.get(dname), true);

					if (cidt != -1) {
						cid = cidt;
						// echo(cid);

						String vsql = String.format(viewformat,
								dname, view, (int) cid);
						//echo(vsql);
						dbcreate(vsql);

						CommitDB();
					} else {
						continue;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			sql = String.format(format, cid, val, dtime, UnitList.get(dname));
			//echo(sql);
			try {
				insert(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// TODO Auto-generated method stub
		HikariConnectionPool hcp = HikariConnectionPool.getInstance();
		hcp.GetCon();
		echo("getGovData start ....");
		govFetch();
		echo("getGovData end ....");
	}

	@Override
	public String getGroup() {
		// TODO Auto-generated method stub
		return "Group_"+SliceName(stdClass);
	}

	@Override
	public int withRepeatCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int withIntervalInSeconds() {
		// TODO Auto-generated method stub
		return 20;
	}

}
