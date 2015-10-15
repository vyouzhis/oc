package com.lib.thread;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BaseRapidThread;
import org.ppl.net.cUrl;

import com.alibaba.fastjson.JSON;

public class getGovData extends BaseRapidThread {
	cUrl curl;
	String SearchUrl = "http://data.stats.gov.cn/easyquery.htm";
	String LoginUrl = "http://data.stats.gov.cn/login.htm?m=login";

	@Override
	public void Run() {
		// TODO Auto-generated method stub
		echo("getGovData start ....");
		govFetch();
		echo("getGovData end ....");
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
	public void govFetch() {
		curl = new cUrl();
		List<Map<String, Object>> govJson, TreeJson, SubTreeJson;

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
		// echo(res);

		govJson = JSON.parseObject(res, List.class); // 获取所有的主树
		// echo(govJson);

		for (Map<String, Object> key : govJson) {
			curl.clearParams();
			curl.addParams("dbcode", "hgyd");
			curl.addParams("id", key.get("id").toString());
			curl.addParams("m", "getTree");
			curl.addParams("wdcode", "zb");
			res = curl.httpPost(SearchUrl);

			TreeJson = JSON.parseObject(res, List.class); // 获取某一个子节点

			for (Map<String, Object> tj : TreeJson) {
				if (tj.get("name").toString().indexOf("2003") > 0
						|| tj.get("name").toString().indexOf("2004") > 0
						|| tj.get("name").toString().indexOf("2001") > 0)
					continue;
				echo(tj.get("name").toString());
				if (((boolean) tj.get("isParent")) == true) { // 可能还有更深的子节点
					curl.clearParams();
					curl.addParams("dbcode", "hgyd");
					curl.addParams("id", key.get("id").toString());
					curl.addParams("m", "getTree");
					curl.addParams("wdcode", "zb");
					String subRes = curl.httpPost(SearchUrl);

					SubTreeJson = JSON.parseObject(subRes, List.class); // 获得二次的子节点

					for (Map<String, Object> sj : SubTreeJson) {
						if (sj.get("name").toString().indexOf("2003") > 0
								|| sj.get("name").toString().indexOf("2004") > 0
								|| sj.get("name").toString().indexOf("2001") > 0)
							continue;
						DataSave(sj.get("id").toString()); // 保存数据
					}
				} else {
					DataSave(tj.get("id").toString()); // 保存数据

				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	private void DataSave(String id) {
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

		dfwds = "[{\"wdcode\":\"sj\",\"valuecode\":\"LAST36\"}]";
		curl.clearParams();
		curl.addParams("colcode", "sj");
		curl.addParams("dbcode", "hgyd");
		curl.addParams("dfwds", dfwds);
		curl.addParams("k1", "1443084595325");
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
		// Map<String, String> viewList = new HashMap<>();

		if (datanodes.size() == 0)
			return;

		for (Map<String, Object> map : nodes) {
			// echo(map.get("name")+"--"+map.get("code")+"--"+map.get("memo"));

			String sqlI = String.format(formatI, map.get("name"),
					map.get("code"), map.get("memo"), now);
			// echo(map.get("name"));

			classList.put(map.get("code").toString(), sqlI);

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
			if (!dnameo.equals(dnamet)) {
				dnamet = dnameo;
				try {

					long cidt = insert(classList.get(dname), true);
					if (cidt != -1) {
						cid = cidt;
						// echo(cid);
						CommitDB();

						String vsql = String.format(viewformat,
								dname.toLowerCase(), view, (int) cid);
						// echo(sqlI);
						dbcreate(vsql);
					} else {
						continue;
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			sql = String.format(format, cid, val, dtime, UnitList.get(dname));

			// echo(sql);
			try {
				insert(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public String title() {
		// TODO Auto-generated method stub
		String className = this.getClass().getCanonicalName();

		return _CLang(SliceName(className));
	}

}
