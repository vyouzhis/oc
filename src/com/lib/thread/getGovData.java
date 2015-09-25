package com.lib.thread;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.expr.NewArray;

import org.apache.commons.lang3.text.StrBuilder;
import org.ppl.BaseClass.BaseRapidThread;
import org.ppl.net.cUrl;

import com.alibaba.fastjson.JSON;

public class getGovData extends BaseRapidThread{
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
		//echo(res);

		govJson = JSON.parseObject(res, List.class); // 获取所有的主树
		//echo(govJson);
		int idk = 1;

		for (Map<String, Object> key : govJson) {
			curl.clearParams();
			curl.addParams("dbcode", "hgyd");
			curl.addParams("id", key.get("id").toString());
			curl.addParams("m", "getTree");
			curl.addParams("wdcode", "zb");
			res = curl.httpPost(SearchUrl);
			
			TreeJson = JSON.parseObject(res, List.class); // 获取某一个子节点
			//echo(TreeJson);
			idk = 1;

			for (Map<String, Object> tj : TreeJson) {
				if (((boolean) tj.get("isParent")) == true) { // 可能还有更深的子节点
					curl.clearParams();
					curl.addParams("dbcode", "hgyd");
					curl.addParams("id", key.get("id").toString());
					curl.addParams("m", "getTree");
					curl.addParams("wdcode", "zb");
					String subRes = curl.httpPost(SearchUrl);
					
					SubTreeJson = JSON.parseObject(subRes, List.class); // 获得二次的子节点

					for (Map<String, Object> sj : SubTreeJson) {
						DataSave(sj.get("id").toString(), idk); // 保存数据
						idk++;
					}
				} else {
					DataSave(tj.get("id").toString(), idk); // 保存数据
					idk++;
				}
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private  void DataSave(String id, int i) {
		Map<String, Object> resJson;
		curl.clearParams();
		curl.addParams("colcode", "zb");
		curl.addParams("dbcode", "hgyd");
		curl.addParams("dfwds", "[{\"wdcode\":\"zb\",\"valuecode\":\"" + id
				+ String.format("%02d", i) + "\"}]");
		curl.addParams("k1", "1443084595325");
		curl.addParams("m", "QueryData");
		curl.addParams("rowcode", "sj");
		curl.addParams("wds", "[]");
		String res = curl.httpPost(SearchUrl);
		
		String format = " insert INTO " + DB_HOR_PRE
							+ "class (rule,act_v0,act_v1)values(%d, '%s','%s')";
		String sql ="";
		resJson = JSON.parseObject(res, Map.class);
		
		if(resJson.get("returndata").toString().length()<21)return;
		
		Map<String, Object> returndata = JSON.parseObject(resJson.get("returndata").toString(),Map.class);
		List<Object> datanodes = (List<Object>) returndata.get("datanodes");
		List<Object> wdnodes = (List<Object>) returndata.get("wdnodes");

		int now = 1;
		
		Map<String, Object> mapNodes = (Map<String, Object>) wdnodes.get(0);
		List<Map<String, Object>> nodes = (List<Map<String, Object>>) mapNodes.get("nodes");
		Map<String, Object> NameList = new HashMap<String, Object>();
		
		for (Map<String, Object> map : nodes) {
			//echo(map.get("name")+"--"+map.get("code")+"--"+map.get("memo"));
			String formatI = "INSERT INTO " + DB_HOR_PRE
							+ "classinfo (title,view_name,idesc,ctime)values ('%s','%s','%s', %d)";
			String sqlI = String.format(formatI, map.get("name"), map.get("code"), map.get("memo"), now);
			//echo(map.get("name"));
			try {
				long cid;
				cid = insert(sqlI, true);
				echo(cid);
				CommitDB();	
				String view =  "act_v0 as data, act_v1 as month";
				formatI = "CREATE VIEW %s AS SELECT %s FROM " + DB_HOR_PRE
							+ "class WHERE rule=%d";
				sqlI = String.format(formatI, map.get("code").toString(), view, (int)cid);
				//echo(sqlI);
				dbcreate(sqlI);
				NameList.put(map.get("code").toString(), cid);
			}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for (int j = 0; j < datanodes.size(); j++) {
			Map<String, Object> listMap = (Map<String, Object>) datanodes.get(j);
			Map<String, Object> data = (Map<String, Object>) listMap.get("data");
			String val = "";
			if(data.get("data").toString().length()>0){
				val =	String.format("%.2f", toFloat(data.get("data")));
			}
			String code = listMap.get("code").toString();
			String[] nt = code.split("_");
			String dname = nt[0].substring(3);
			String dtime = nt[1].substring(3);
			dtime = dtime.substring(0,4)+"-"+dtime.substring(4);
			
			sql = String.format(format, NameList.get(dname),val,dtime);
			
			//echo(sql);
			try {
				insert(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
