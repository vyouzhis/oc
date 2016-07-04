package com.lib.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BaseRapidThread;
import org.ppl.db.MGDB;
import org.ppl.net.cUrl;
import org.ppl.plug.Quartz.SimpleQuartz;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSON;

public class getJPGovMongoDB extends SimpleQuartz implements Job {
	private cUrl curl;
	private String url;
	private static String Ver = "2.0";
	private static String appId = "abb68400ed0dd8e8828b6d8b3e32154c111561b4";
	// private static String lang = "E";
	private static int limit = 1000;
	private String statsField = "06";
	private int tolNumber = 0;
	private List<String> KeyList;
	MGDB mgdb = new MGDB();

	public getJPGovMongoDB() {
		// TODO Auto-generated method stub
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
	}

	@SuppressWarnings("unchecked")
	private boolean getStatsList(int startPosition) {

		url = "http://api.e-stat.go.jp/rest/" + Ver
				+ "/app/json/getStatsList?appId=" + appId + "&limit=" + limit
				+ "&startPosition=" + startPosition + "&statsField="
				+ statsField;
		// if(startPosition > 100) return; // ===========================
		//echo("url:"+url);
		String res = "";
		int loopTime = 0;
		while (true) {
			res = curl.httpGet(url);
			if (res == null || res.length() < 10) {
				echo("url:" + url);
				if (startPosition > 0 && tolNumber <= startPosition)
					return false;
			} else {
				break;
			}
			if (loopTime > 5)
				return false;
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

		res = res.replace("$", "volume");
		res = res.replace("@", "");
		// echo(res);
		mgdb.SetCollection("getStatsList_" + statsField);
		mgdb.Insert(res);

		Map<String, Object> DATALIST_INF = (Map<String, Object>) GET_STATS_LIST
				.get("DATALIST_INF");

		if (tolNumber == 0) {
			tolNumber = toInt(DATALIST_INF.get("NUMBER"));
		}

		if (!DATALIST_INF.containsKey("TABLE_INF"))
			return false;

		List<Map<String, Object>> TABLE_INF = (List<Map<String, Object>>) DATALIST_INF
				.get("TABLE_INF");
		if (TABLE_INF == null || TABLE_INF.size() == 0)
			return false;

		for (Map<String, Object> map : TABLE_INF) {
			// echo("n:"+n+" size:"+L);
			// n++;
			if (!map.containsKey("TITLE"))
				continue;

			String id = map.get("@id").toString();
			KeyList.clear();
			getMetaInfo(id);

			boolean StatsData = true;
			// long StatsData_startPosition = getNowStatsDataID(id);
			long StatsData_startPosition = 0;
			// echo("StatsData_startPosition:"+StatsData_startPosition+" id:"+id);
			int im = 0;
			mgdb.SetCollection("getStatsData_" + statsField);
			while (StatsData) {
				StatsData = getStatsData(StatsData_startPosition, id);
				StatsData_startPosition += limit;
				im++;
				if (im > 5)
					break;
			}

		}
		echo("getStatsList new startPosition:" + startPosition + " statsField:"
				+ statsField);

		return true;
	}

	@SuppressWarnings("unchecked")
	private void getMetaInfo(String statsDataId) {
		String url = "http://api.e-stat.go.jp/rest/" + Ver
				+ "/app/json/getMetaInfo?appId=" + appId + "&statsDataId="
				+ statsDataId;

		String res = "";

		int loopTime = 0;
		while (true) {
			res = curl.httpGet(url);
			if (res == null || res.length() < 10) {
				echo("url:" + url);
			} else {

				break;
			}
			if (loopTime > 5)
				return;
			loopTime++;
		}

		Map<String, Object> json = JSON.parseObject(res, Map.class);

		Map<String, Object> GET_META_INFO = (Map<String, Object>) json
				.get("GET_META_INFO");
		Map<String, Object> RESULT = (Map<String, Object>) GET_META_INFO
				.get("RESULT");

		if (toInt(RESULT.get("STATUS")) != 0) {
			echo("STATUS:" + RESULT.get("STATUS"));
			echo("error:" + RESULT.get("ERROR_MSG"));

			return;
		}

		res = res.replace("$", "volume");
		res = res.replace("@", "");
		// echo(res);
		mgdb.SetCollection("getMetaInfo_" + statsField);
		mgdb.Insert(res);

	}

	@SuppressWarnings("unchecked")
	private boolean getStatsData(long startPosition, String statsDataId) {

		// echo("getStatsData m:"+m);

		String url = "http://api.e-stat.go.jp/rest/" + Ver
				+ "/app/json/getStatsData?appId=" + appId + "&statsDataId="
				+ statsDataId + "&metaGetFlg=N&limit=" + limit
				+ "&startPosition=" + startPosition;

		String res = "";

		int loopTime = 0;
		while (true) {
			res = curl.httpGet(url);
			if (res == null || res.length() < 10) {
				echo("url:" + url);
			} else {
				break;
			}
			if (loopTime > 5) {
				echo("i am go out loopTime no." + statsDataId);
				return false;
			}
			loopTime++;
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

		res = res.replace("$", "volume");
		res = res.replace("@", "");
		// echo(res);
		mgdb.Insert(res);

		return true;
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// TODO Auto-generated method stub
		curl = new cUrl();

		int startPosition = 0;
		KeyList = new ArrayList<>();
		boolean StatsList = true;
		while (StatsList) {
			echo(statsField + " = startPosition:" + startPosition);
			StatsList = getStatsList(startPosition);
			startPosition += limit;
		}

		echo("getJPGovData end... ");
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
