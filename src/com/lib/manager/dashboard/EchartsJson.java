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
import org.ppl.io.Encrypt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.abel533.echarts.DataRange;
import com.github.abel533.echarts.Label;
import com.github.abel533.echarts.Title;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.AxisType;
import com.github.abel533.echarts.code.Magic;
import com.github.abel533.echarts.code.RoseType;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.code.Y;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Pie;
import com.github.abel533.echarts.style.AreaStyle;
import com.github.abel533.echarts.style.ItemStyle;
import com.github.abel533.echarts.style.itemstyle.Emphasis;
import com.github.abel533.echarts.style.itemstyle.Normal;

public class EchartsJson extends Permission implements BasePerminterface {
	private List<String> rmc;
	private GsonOption option = null;
	private List<Map<String, String>> JsonIds = null;
	private List<List<Map<String, Object>>> pieList = null;

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
				.feature(
						Tool.mark,
						Tool.dataView,
						Tool.dataZoom,
						new MagicType(Magic.line, Magic.bar, Magic.stack,
								Magic.tiled), Tool.restore, Tool.saveAsImage);

		option.calculable(true);

		option.legend().y(Y.bottom);
		// .x("function(){alert('ok');}");
		// .formatter("{a} <br/>{b} : ({c}%)");

	}

	@SuppressWarnings("unchecked")
	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		String gt = "";
		String EJson = "";
		if (porg.getKey("graph_type") != null) {
			gt = porg.getKey("graph_type").toString();
		}

		List<Map<String, Object>> GL;
		String glJson = mConfig.GetValue("echarts.graph");

		GL = JSON.parseObject(glJson, List.class);

		PaserJson();

		pieList = getEcharts();

		if (JsonIds != null) {

			if (GL.get(0).get("graph").toString().equals(gt)) {
				EJson = JsonLine();

			} else if (GL.get(1).get("graph").toString().equals(gt)) {
				EJson = JsonPie();

			} else if (GL.get(2).get("graph").toString().equals(gt)) {
				EJson = JsonMap();
			}
			// else{
			//
			// }
		}
		super.setHtml(EJson);
	}

	public String JsonLine() {
		CategoryAxis categoryAxis = new CategoryAxis();
		categoryAxis.axisLine().onZero(false);
		categoryAxis.setType(AxisType.value);
		categoryAxis.axisLabel().formatter("{value}");
		option.yAxis(categoryAxis);
		List<Object> valAxiList = new ArrayList<>();
		option.tooltip().trigger(Trigger.axis);

		boolean Xbool = true;
		ValueAxis valueAxis = new ValueAxis();
		valueAxis.setType(AxisType.category);

		if (pieList == null || pieList.size() == 0 || JsonIds.size() == 0)
			return "";
		int m = 0;

		int itemStyle_lable = 0, itemStyle_areaStyle = 0, markLine_average = 0;

		itemStyle_lable = toInt(porg.getKey("itemStyle_lable"));
		itemStyle_areaStyle = toInt(porg.getKey("itemStyle_areaStyle"));
		markLine_average = toInt(porg.getKey("markLine_average"));

		for (Map<String, String> id : JsonIds) {
			if (!id.get("id").toString().matches("[0-9]+"))
				continue;
			option.legend(id.get("name").toString());

			List<Map<String, Object>> list = pieList.get(m);

			if (list.size() == 0)
				continue;
			m++;

			if (JsonIds.size() == 1) {
				Bar bar = new Bar();
				bar.name(id.get("name").toString()).itemStyle().normal()
						.lineStyle();
				if (markLine_average == 1) {
					Map<String, String> mkline = new HashMap<>();
					mkline.put("type", "average");
					mkline.put("name", _CLang("line_markLine"));
					bar.markLine().data(mkline);
				}
				for (Map<String, Object> key : list) {

					if (Xbool) {
						valueAxis.data(key.get("dial").toString());
					}
					float val = toFloat(key.get("volume"));
					if (val == 0) {
						bar.data(key.get("volume"));
					} else {
						bar.data(Float.valueOf(String.format("%.2f", val)));
					}
				}

				Xbool = false;

				if (itemStyle_lable == 1 || itemStyle_areaStyle == 1) {
					ItemStyle itemStyle = new ItemStyle();
					Normal normal = new Normal();

					if (itemStyle_lable == 1) {
						Label label = new Label();
						label.setShow(true);

						normal.setLabel(label);
					}
					if (itemStyle_areaStyle == 1) {
						AreaStyle aStyle = new AreaStyle();
						normal.setAreaStyle(aStyle.typeDefault());
					}
					// itemStyle: {normal: {color:'rgba(193,35,43,1)',
					// label:{show:true}}},

					itemStyle.setNormal(normal);

					bar.itemStyle(itemStyle);
				}
				option.series(bar);
			} else {
				Line line = new Line();
				line.smooth(true).name(id.get("name").toString()).itemStyle()
						.normal().lineStyle();

				if (markLine_average == 1) {
					Map<String, String> mkline = new HashMap<>();
					mkline.put("type", "average");
					mkline.put("name", _CLang("line_markLine"));
					line.markLine().data(mkline);
				}
				int j = 0;
				boolean xC=true;
				for (Map<String, Object> key : list) {

					if (Xbool) {
						valueAxis.data(key.get("dial").toString());
						valAxiList.add(key.get("dial").toString());
					}else{
						//数据对齐
						while (xC && j<valAxiList.size() && !valAxiList.get(j).toString()
								.equals(key.get("dial").toString())) {
							line.data("");
							j++;
						}
						xC = false;
					}
					float val = toFloat(key.get("volume"));
					if (val == 0) {
						line.data(key.get("volume"));
					} else {
						line.data(Float.valueOf(String.format("%.2f", val)));
					}

				}

				Xbool = false;
				if (itemStyle_lable == 1 || itemStyle_areaStyle == 1) {
					ItemStyle itemStyle = new ItemStyle();
					Normal normal = new Normal();

					if (itemStyle_lable == 1) {
						Label label = new Label();
						label.setShow(true);

						normal.setLabel(label);
					}
					if (itemStyle_areaStyle == 1) {
						AreaStyle aStyle = new AreaStyle();
						normal.setAreaStyle(aStyle.typeDefault());
					}

					// itemStyle: {normal: {color:'rgba(193,35,43,1)',
					// label:{show:true}}},

					itemStyle.setNormal(normal);

					line.itemStyle(itemStyle);
				}
				option.series(line);
			}

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

		List<Map<String, Object>> list = pieList.get(0);

		Line line = new Line();
		line.smooth(true).name(_MLang("mom")).itemStyle().normal().lineStyle();

		float front = 0;
		float x = 0;
		float m;
		for (Map<String, Object> key : list) {

			x = Float.valueOf(key.get("volume").toString());

			if (front == 0) {
				m = 100;
			} else {
				m = (x - front) / front * 100;
				// echo("front:" + front + " x:" + x + " m:" + m);
			}

			line.data((int) m);
			front = x;

		}

		option.series(line);
	}

	private String JsonPie() {
		option.tooltip().trigger(Trigger.item)
				.formatter("{b} <br/> {c} ({d}%)");

		if (pieList == null || pieList.size() == 0)
			return "";
		// List<String> legendTitle = null;
		int l = 0;
		int roseType = 0;
		roseType = toInt(porg.getKey("roseType"));

		for (Map<String, String> id : JsonIds) {
			if (!id.get("id").toString().matches("[0-9]+"))
				continue;
			
			List<Map<String, Object>> list = pieList.get(l);
			l++;
			Pie pie = new Pie();
			if (roseType == 1) {
				pie.roseType(RoseType.area);
			}
			//Title title = new Title();
			//title.text(id.get("name").toString());
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
			option.title(id.get("name").toString());
		}
		// option.legend().data(data);
		// option.xAxis(valueAxis);
		
		return option.toString();
	}

	private String JsonMap() {
		option.tooltip().trigger(Trigger.item).formatter("{b} <br/> {c}");
		List<List<Map<String, Object>>> mapList = getEcharts();
		ItemStyle itemStyle = new ItemStyle();
		Normal normal = new Normal();
		Label label = new Label();
		label.show(true);
		Emphasis emphasis = new Emphasis();
		emphasis.setLabel(label);

		normal.setLabel(label);
		itemStyle.setEmphasis(emphasis);
		itemStyle.setNormal(normal);
		int min = 0;
		int max = 0;
		int tmp = 0;

		if (mapList == null || mapList.size() == 0)
			return "";
		// List<String> legendTitle = new ArrayList<>();
		int l = 0;
		for (Map<String, String> id : JsonIds) {
			if (!id.get("id").toString().matches("[0-9]+"))
				continue;

			option.legend(id.get("name").toString());

			List<Map<String, Object>> list = mapList.get(l);
			l++;
			com.github.abel533.echarts.series.Map map = new com.github.abel533.echarts.series.Map();
			// legendTitle = new ArrayList<>();
			map.mapType("china");
			map.roam(false);
			map.itemStyle(itemStyle);
			map.setName(id.get("name").toString());
			for (Map<String, Object> key : list) {
				Map<String, Object> m = new HashMap<>();
				m.put("value", key.get("volume"));
				m.put("name", key.get("dial").toString());
				tmp = Integer.valueOf(key.get("volume").toString());
				if (tmp < min) {
					min = tmp;
				}
				if (tmp > max) {
					max = tmp;
				}
				map.data(m);
			}
			option.series(map);

		}
		// option.legend(legendTitle);
		// option.legend().data(data);
		// option.xAxis(valueAxis);

		DataRange dataRange = new DataRange();
		dataRange.setMin(min);
		dataRange.setMax(max);
		dataRange.x(X.left);
		dataRange.y(Y.bottom);
		List<String> tList = new ArrayList<>();
		tList.add(_MLang("hight"));
		tList.add(_MLang("low"));
		dataRange.text(tList);
		dataRange.calculable(true);
		option.dataRange(dataRange);

		option.legend().x(X.right);

		return option.toString();
	}

	@SuppressWarnings("unchecked")
	private void PaserJson() {

		String json_id = Escape.unescape(porg.getKey("ids"));

		json_id = escapeHtml(json_id);
		if (json_id.length() < 2) {
			return;
		}
		JsonIds = JSON.parseObject(json_id, List.class);

	}

	@SuppressWarnings("unchecked")
	private List<List<Map<String, Object>>> getEcharts() {
		List<List<Map<String, Object>>> ret = new ArrayList<>();
		int qaction = 0;
		String format = "select rule,volume,dial from "
				+ DB_HOR_PRE
				+ "webvisitcount  where rule = %d and dial > 2015011100 order by rule, dial ;";

		List<Map<String, Object>> res = null;
		String tmp_list = porg.getKey("tmp_list");
		Map<String, String> tmp_map = null;
		if (tmp_list != null) {
			tmp_map = JSON.parseObject(tmp_list, Map.class);
		}

		for (Map<String, String> id : JsonIds) {

			qaction = toInt(id.get("qaction"));
			int dtype = 0;
			if (qaction == 0)
				continue;

			String sql = String.format(format, toInt(id.get("id")));
			int tid = toInt(id.get("id"));
			if (qaction == 4) {

				String usql = "select sql,dtype from " + DB_HOR_PRE
						+ "usersql where id=" + tid + " LIMIT 1";
				Map<String, Object> ures;

				ures = FetchOne(usql);

				if (ures != null) {
					sql = ures.get("sql").toString();
					sql = escapeHtml(sql);
					dtype = toInt(ures.get("dtype"));
				}

			} else if (qaction == 5) {

				sql = "select t.sqltmp,u.sql,u.dtype,u.sqltmp as usqltmp from "
						+ DB_HOR_PRE + "sqltmp t, " + DB_HOR_PRE
						+ "usersql u where t.sid=u.id and t.id=" + tid
						+ " limit 1";

				Map<String, Object> tres;
				tres = FetchOne(sql);
				// echo(tsql);
				if (tres == null)
					continue;

				sql = tres.get("sql").toString();
				String tJson = tres.get("sqltmp").toString();
				String uJson = tres.get("usqltmp").toString();
				dtype = toInt(tres.get("dtype"));
				Map<String, String> tList = JSON.parseObject(tJson, Map.class);

				List<List<String>> uList = JSON.parseObject(uJson, List.class);

				for (String key : tList.keySet()) {
					sql = sql.replace("@" + key + "@", tList.get(key));
				}

				for (List<String> lkey : uList) {
					sql = sql.replace("@" + lkey.get(0) + "@", lkey.get(1));
				}

				sql = escapeHtml(sql);
				// echo(sql);

			} else if (qaction == 6) {
				if (tmp_map == null)
					continue;
				sql = "SELECT sql,dtype from " + DB_HOR_PRE
						+ "usersql where id=" + tid + " limit 1";
				Map<String, Object> tmpRes;
				tmpRes = FetchOne(sql);
				if (tmpRes == null)
					continue;
				format = tmpRes.get("sql").toString();
				dtype = toInt(tmpRes.get("dtype"));
				for (int i = 0; i < 10; i++) {
					if (tmp_map.containsKey(tid + "_arg" + i)) {
						format = format.replace("@arg" + i + "@",
								tmp_map.get(tid + "_arg" + i));
					}
				}

				sql = escapeHtml(format);

			}

			try {
				res = getCache(sql);
				if (res != null) {
					ret.add(res);
					continue;
				}

				if (dtype == 0) {

					res = FetchAll(sql);
				} else {
					res = CustomDB(sql, dtype);
				}

				if (res != null) {
					ret.add(res);
					setCache(sql, id.get("name").toString(), res);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return ret;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getCache(String key) {
		Encrypt ec = Encrypt.getInstance();
		String md5Key = ec.MD5(key);
		String json = "";
		String format = "select json from " + DB_HOR_PRE
				+ "cache where md5='%s'";
		String csql = String.format(format, md5Key);

		List<Map<String, Object>> res = null;

		Map<String, Object> co;
		co = FetchOne(csql);

		if (co != null && co.size() > 0) {

			json = co.get("json").toString();

			if (json.length() > 1) {
				res = JSON.parseObject(json, List.class);
			}
		}

		return res;
	}

	private void setCache(String key, String title, List<Map<String, Object>> o) {
		String format = "insert into hor_cache (md5,json,title)values ('%s', '%s', '%s')";

		String json = JSON.toJSONString(o,
				SerializerFeature.UseISO8601DateFormat);

		Encrypt ec = Encrypt.getInstance();
		String md5Key = ec.MD5(key);

		String sql = String.format(format, md5Key, json, title);

		try {
			insert(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		// echo("pwd:"+sql);
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
