package com.lib.manager.dashboard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;
import org.ppl.common.Escape;
import org.ppl.db.UserCoreDB;
import org.ppl.io.DesEncrypter;

import com.alibaba.fastjson.JSON;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.AxisType;
import com.github.abel533.echarts.code.Magic;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.code.Y;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Pie;
import com.google.inject.matcher.Matcher;

public class EchartsJson extends Permission implements BasePerminterface {
	private List<String> rmc;
	private GsonOption option = null;
	private List<Map<String, String>> JsonIds = null;

	public EchartsJson() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		setRoot("name", _MLang("name"));
		InAction(); // 设置只是动作
		setAjax(true); // 设置是 ajax
		isAutoHtml = false; // 不用加载页头和页脚
	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		if (super.Init() == -1)
			return;

		rmc = porg.getRmc();
		if (rmc.size() != 2) {
			Msg(_CLang("error_role"));
			return;
		}
		option = new GsonOption();
		InitOption();

		switch (rmc.get(1).toString()) {
		case "read":
			read(null);
			break;
		case "search":
			search(null);
			break;

		default:
			Msg(_CLang("error_role"));
			return;
		}
	}

	private void InitOption() {
		option.toolbox()
				.show(true)
				.feature(Tool.mark, Tool.dataView, Tool.dataZoom,
						new MagicType(Magic.line, Magic.bar), Tool.restore,
						Tool.saveAsImage);

		option.calculable(true);

		option.legend().y(Y.bottom);
		// .x("function(){alert('ok');}");
		// .formatter("{a} <br/>{b} : ({c}%)");

	}

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		int gt = 1;
		String EJson = "";
		if (porg.getKey("graph_type") != null
				&& porg.getKey("graph_type").matches("[0-9]+")) {
			gt = Integer.valueOf(porg.getKey("graph_type").toString());
		}
		PaserJson();
		if (JsonIds != null) {
			switch (gt) {
			case 1:
				EJson = JsonLine();
				break;
			case 2:
				EJson = JsonPie();
				break;
			default:
				break;
			}
		}
		super.setHtml(EJson);
	}

	public String JsonLine() {
		CategoryAxis categoryAxis = new CategoryAxis();
		categoryAxis.axisLine().onZero(false);
		categoryAxis.setType(AxisType.value);
		categoryAxis.axisLabel().formatter("{value}");
		option.yAxis(categoryAxis);

		option.tooltip().trigger(Trigger.axis);

		boolean Xbool = true;
		ValueAxis valueAxis = new ValueAxis();
		valueAxis.setType(AxisType.category);

		List<List<Map<String, Object>>> pieList = getEcharts();

		if (pieList == null || pieList.size() == 0)
			return "";
		int m = 0;
		for (Map<String, String> id : JsonIds) {
			if (!id.get("id").toString().matches("[0-9]+"))
				continue;
			option.legend(id.get("name").toString());

			List<Map<String, Object>> list = pieList.get(m);
			m++;

			Line line = new Line();
			line.smooth(true).name(id.get("name").toString()).itemStyle()
					.normal().lineStyle();
			Map<String, String> mkline = new HashMap<>();
			mkline.put("type", "average");
			mkline.put("name", _CLang("line_markLine"));
			line.markLine().data(mkline);
			for (Map<String, Object> key : list) {

				if (Xbool) {
					valueAxis.data(key.get("dial").toString());
				}
				line.data(key.get("volume"));
			}

			Xbool = false;

			option.series(line);

		}
		if (JsonIds.size() == 1) {
			mom();
		}
		option.xAxis(valueAxis);

		if (Xbool) {
			return "";
		}
		return option.toString();
	}

	// month-on-month
	private void mom() {
		option.legend(_MLang("mom"));
		List<List<Map<String, Object>>> pieList = getEcharts();
		List<Map<String, Object>> list = pieList.get(0);

		Line line = new Line();
		line.smooth(true).name(_MLang("mom")).itemStyle().normal().lineStyle();
	
		float front = 0;
		float x = 0;
		float m;
		for (Map<String, Object> key : list) {
			
			x = Float.valueOf(key.get("volume").toString());
			
			if (front == 0){
				m = 100;
			}else{
				m = (x - front) / front * 100;
				echo("front:" + front+" x:"+x +" m:"+m);				
			}
			
			line.data(m);
			front = x;
			
		}

		option.series(line);
	}

	private String JsonPie() {
		option.tooltip().trigger(Trigger.item)
				.formatter("{b} <br/> {c} ({d}%)");
		List<List<Map<String, Object>>> pieList = getEcharts();

		if (pieList == null || pieList.size() == 0)
			return "";
		// List<String> legendTitle = null;
		int l = 0;
		for (Map<String, String> id : JsonIds) {
			if (!id.get("id").toString().matches("[0-9]+"))
				continue;

			List<Map<String, Object>> list = pieList.get(l);
			l++;
			Pie pie = new Pie();
			// legendTitle = new ArrayList<>();
			for (Map<String, Object> key : list) {
				Map<String, Object> m = new HashMap<>();
				m.put("value", key.get("volume"));
				m.put("name", key.get("dial").toString());
				// legendTitle.add(key.get("dial").toString());
				option.legend(key.get("dial").toString());
				pie.data(m);
			}

			// option.legend().data(legendTitle);
			option.series(pie);

		}
		// option.legend().data(data);
		// option.xAxis(valueAxis);

		return option.toString();
	}

	@SuppressWarnings("unchecked")
	private void PaserJson() {

		String json_id = Escape.unescape(porg.getKey("ids"));

		json_id = Myreplace(json_id);
		if (json_id.length() < 2) {
			return;
		}
		JsonIds = JSON.parseObject(json_id, List.class);

	}

	private List<List<Map<String, Object>>> getEcharts() {
		List<List<Map<String, Object>>> ret = new ArrayList<>();

		String format = "select rule,volume,dial from "
				+ DB_HOR_PRE
				+ "webvisitcount  where rule = %d and dial > 2015011100 order by rule, dial ;";

		List<Map<String, Object>> res = null;

		for (Map<String, String> id : JsonIds) {
			if (!id.get("id").toString().matches("[0-9]+"))
				continue;

			String sql = String.format(format,
					Integer.valueOf(id.get("id").toString()));
			if (id.get("qaction").equals("4")) {
				String usql = "select sql,dtype from hor_usersql where id="
						+ id.get("id").toString() + " LIMIT 1";
				Map<String, Object> ures;

				ures = FetchOne(usql);
				if (ures != null) {
					sql = ures.get("sql").toString();
					sql = Myreplace(sql);
				}
				try {
					int dtype = Integer.valueOf(ures.get("dtype").toString());
					if (dtype == 0) {
						res = FetchAll(sql);
					} else {
						res = CustomDB(sql, dtype);
					}
					if (res != null) {
						ret.add(res);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {

				try {

					res = FetchAll(sql);
					ret.add(res);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return ret;
	}

	private List<Map<String, Object>> CustomDB(String sql, int id) {
		List<Map<String, Object>> res = null;
		UserCoreDB ucdb = new UserCoreDB();

		String format = "select * from " + DB_HOR_PRE
				+ "dbsource where id=%d limit 1";
		String dsql = String.format(format, id);

		Map<String, Object> dres;

		dres = FetchOne(dsql);
		if (dres == null)
			return null;

		ucdb.setDriverClassName(dres.get("dcname").toString());
		ucdb.setDbUrl(dres.get("url").toString());
		ucdb.setDbUser(dres.get("username").toString());
		String pwd = dres.get("password").toString();

		try {
			DesEncrypter de = new DesEncrypter();
			pwd = de.decrypt(pwd);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ucdb.setDbPwd(pwd);

		if (ucdb.Init() == false) {
			setRoot("ErrorMsg", ucdb.getErrorMsg());
		} else {
			try {
				res = ucdb.FetchAll(sql);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				setRoot("ErrorMsg", e.getMessage().toString());
			}

			ucdb.DBEnd();
		}
		return res;
	}

	private String Myreplace(String old) {
		if (old == null)
			return "";

		String news = old.replace("&nbsp;", "");
		news = news.replace("&quot;", "\"");
		news = news.replace("&apos;", "\'");
		news = news.replace(";", "");

		return news;
	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void edit(Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void search(Object arg) {
		// TODO Auto-generated method stub

	}

}
