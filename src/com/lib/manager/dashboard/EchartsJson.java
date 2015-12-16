package com.lib.manager.dashboard;

import java.net.URI;
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
import org.ppl.io.ProjectPath;
import org.ppl.plug.R.Rlan;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RFactor;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RserveException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.abel533.echarts.DataRange;
import com.github.abel533.echarts.Label;
import com.github.abel533.echarts.Legend;
import com.github.abel533.echarts.Polar;
import com.github.abel533.echarts.Title;
import com.github.abel533.echarts.axis.Axis;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.AxisType;
import com.github.abel533.echarts.code.Magic;
import com.github.abel533.echarts.code.Orient;
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
import com.github.abel533.echarts.series.Radar;
import com.github.abel533.echarts.series.Scatter;
import com.github.abel533.echarts.style.AreaStyle;
import com.github.abel533.echarts.style.ItemStyle;
import com.github.abel533.echarts.style.itemstyle.Emphasis;
import com.github.abel533.echarts.style.itemstyle.Normal;

public class EchartsJson extends Permission implements BasePerminterface {
	private List<String> rmc;
	private GsonOption option = null;
	private List<Map<String, String>> JsonIds = null;
	private List<List<Map<String, Object>>> pieList = null;
	private int itemStyle_lable = 0, itemStyle_areaStyle = 0,
			markLine_average = 0;
	private int math_mom = 0, math_var = 0, math_sma = 0, math_wma = 0,
			math_diff = 0;
	private int math_lm = 0;
	private int order_desc = 0;
	private int ftype = 0;
	private List<Map<String, Object>> RListJson;
	private int index = 0;

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
		Title title = new Title();
		title.subtext("DiscoveryDBS.com");
		title.x(X.center);
		title.y(Y.top);
		option.title(title);

		option.legend().y(Y.bottom);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		ftype = toInt(porg.getKey("ftype"));

		String gt = "";
		String EJson = "";
		if (porg.getKey("graph_type") != null) {
			gt = porg.getKey("graph_type").toString();
		}

		List<Map<String, Object>> GL;
		String glJson = mConfig.GetValue("echarts.graph");

		GL = JSON.parseObject(glJson, List.class);

		PaserJson();
		if (ftype == 0) {
			pieList = getEcharts();
		} else {
			pieList = getRList();
			echo(pieList);
		}

		math_lm = toInt(porg.getKey("math_lm"));

		if (math_lm == 1 && JsonIds.size() == 1) {
			math_lm();
		}
		if (JsonIds != null) {

			if (GL.get(0).get("graph").toString().equals(gt)) {
				EJson = JsonLine();

			} else if (GL.get(1).get("graph").toString().equals(gt)) {
				EJson = JsonPie();

			} else if (GL.get(2).get("graph").toString().equals(gt)) {
				EJson = JsonMap();
			} else if (GL.get(3).get("graph").toString().equals(gt)) {
				EJson = JsonScatter();
			} else if (GL.get(4).get("graph").toString().equals(gt)) {
				EJson = JsonRadar();
			}
			// else{
			//
			// }
		}

		super.setHtml(EJson);
	}

	@SuppressWarnings("rawtypes")
	public String JsonLine() {
		CategoryAxis categoryAxis = new CategoryAxis();
		categoryAxis.axisLine().onZero(false);
		categoryAxis.setType(AxisType.value);
		categoryAxis.axisLabel().formatter("{value}");

		List<Axis> myaAxis = new ArrayList<>();
		ValueAxis myAxis = new ValueAxis();
		myAxis.scale(true);

		int jCount = 0;
		List<Object> valAxiList = new ArrayList<>();
		option.tooltip().trigger(Trigger.axis);

		boolean Xbool = true;
		ValueAxis valueAxis = new ValueAxis();
		valueAxis.setType(AxisType.category);

		if (pieList == null || pieList.size() == 0 || JsonIds.size() == 0)
			return "";
		int m = 0;

		itemStyle_lable = toInt(porg.getKey("itemStyle_lable"));
		itemStyle_areaStyle = toInt(porg.getKey("itemStyle_areaStyle"));
		markLine_average = toInt(porg.getKey("markLine_average"));

		math_mom = toInt(porg.getKey("math_mom"));
		math_var = toInt(porg.getKey("math_var"));
		math_sma = toInt(porg.getKey("math_sma"));
		math_wma = toInt(porg.getKey("math_wma"));
		math_diff = toInt(porg.getKey("math_diff"));

		order_desc = toInt(porg.getKey("order_desc"));

		for (Map<String, String> id : JsonIds) {
			if (toInt(id.get("id")) == 0) {
				continue;
			}
			option.legend(id.get("name").toString());

			List<Map<String, Object>> list = pieList.get(m);
			m++;
			if (list.size() == 0) {
				continue;
			}

			if (JsonIds.size() == 1
					|| (JsonIds.size() == 2 && (math_var == 1 || math_diff == 1))) {
				Bar bar = new Bar();
				bar.name(id.get("name").toString()).itemStyle().normal()
						.lineStyle();
				myAxis.name(id.get("name").toString());

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

						// bar.data(key.get("volume")); // 还不确定 是不是应该要直接给值为 0
						bar.data(0);
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

					itemStyle.setNormal(normal);

					bar.itemStyle(itemStyle);
				}
				myaAxis.add(myAxis);
				option.series(bar);
			} else {
				Line line = new Line();
				line.smooth(true).name(id.get("name").toString()).itemStyle()
						.normal().lineStyle();
				if (jCount == 1) {
					line.yAxisIndex(1);
				}
				ValueAxis tAxis = new ValueAxis();

				tAxis.scale(true);
				if (JsonIds.size() == 2) {
					tAxis.name(id.get("name").toString());

					jCount++;
				}
				myaAxis.add(tAxis);
				if (markLine_average == 1) {
					Map<String, String> mkline = new HashMap<>();
					mkline.put("type", "average");
					mkline.put("name", _CLang("line_markLine"));
					line.markLine().data(mkline);
				}
				int j = 0;

				for (Map<String, Object> key : list) {

					if (Xbool) {
						valueAxis.data(key.get("dial").toString());
						valAxiList.add(key.get("dial").toString());
					} else {
						// 数据对齐
						while (j < valAxiList.size()
								&& !valAxiList.get(j).toString()
										.equals(key.get("dial").toString())) {
							line.data(0);
							j++;
						}
						j++;
					}

					float val = toFloat(key.get("volume"));
					if (val == 0) {
						// line.data(key.get("volume")); // 还不确定 是不是应该要直接给值为 0
						line.data(0);
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

		if (math_mom == 1) {
			ValueAxis momAxis = new ValueAxis();
			momAxis.name(_MLang("mom"));
			myaAxis.add(momAxis);
			math_mom();
		}
		if (math_var == 1 && JsonIds.size() == 2) {
			math_var();
		}
		if (math_sma == 1) {
			math_sma();
		}
		if (math_wma == 1) {
			math_wma();
		}
		if (math_diff == 1 && JsonIds.size() == 2) {
			math_diff();
		}

		myaAxis.add(categoryAxis);
		option.yAxis(myaAxis);

		// option.yAxis();

		option.xAxis(valueAxis);

		if (Xbool) {
			return "";
		}
		return option.toString();
	}

	// month-on-month
	private void math_mom() {
		option.legend(_MLang("mom"));

		List<Map<String, Object>> list = pieList.get(0);

		Line line = new Line();
		line.smooth(true).name(_MLang("mom")).itemStyle().normal().lineStyle();

		float front = 0;
		float x = 0;
		float m;
		for (Map<String, Object> key : list) {

			x = toFloat(key.get("volume"));

			if (front == 0) {
				m = 100;
			} else {
				m = (x - front) / front * 100;
				// echo("front:" + front + " x:" + x + " m:" + m);
			}

			line.data((int) m);
			front = x;

		}

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
		line.yAxisIndex(1);

		option.series(line);
	}

	private void math_var() {

		List<Map<String, Object>> oneList = pieList.get(0);
		List<Map<String, Object>> twoList = pieList.get(1);

		Line pOption = new Line();

		float o, t, m;
		int max = twoList.size();
		if (twoList.size() > oneList.size()) {
			max = oneList.size();
		}

		for (int l = 0; l < max; l++) {

			o = toFloat(oneList.get(l).get("volume"));
			t = toFloat(twoList.get(l).get("volume"));

			if (order_desc == 0) {
				m = (o - t) / t * 100;
			} else {
				m = (t - o) / o * 100;
			}
			// echo("front:" + front + " x:" + x + " m:" + m);

			pOption.data(Float.valueOf(String.format("%.2f", m)));

		}
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

			pOption.itemStyle(itemStyle);
		}
		pOption.smooth(true).name(_MLang("var")).itemStyle().normal()
				.lineStyle();
		option.legend(_MLang("var"));
		pOption.yAxisIndex(1);
		option.series(pOption);
	}

	private void math_diff() {

		float tol = 0;
		List<Map<String, Object>> oneList = pieList.get(0);
		List<Map<String, Object>> twoList = pieList.get(1);

		Line pOption = new Line();

		float o, t, m;
		int max = twoList.size();
		if (twoList.size() > oneList.size()) {
			max = oneList.size();
		}

		for (int l = 0; l < max; l++) {

			o = toFloat(oneList.get(l).get("volume"));
			t = toFloat(twoList.get(l).get("volume"));

			if (order_desc == 0) {
				m = o - t;
			} else {
				m = t - o;
			}
			// echo("front:" + front + " x:" + x + " m:" + m);
			tol += m;
			pOption.data(Float.valueOf(String.format("%.2f", m)));

		}
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

			pOption.itemStyle(itemStyle);
		}
		pOption.yAxisIndex(1);

		pOption.name(_MLang("diff") + tol + ")").itemStyle().normal()
				.lineStyle();

		option.legend(_MLang("diff") + tol + ")");

		option.series(pOption);
	}

	private void math_lm() {
		List<Map<String, Object>> dList = pieList.get(0);

		Line pOption = new Line();
		Rlan rlan = Rlan.getInstance();

		double m;
		double[] dataX, dataY;

		for (int l = 0; l < dList.size(); l++) {
			dataX = new double[l + 1];
			dataY = new double[l + 1];
			for (int i = 0; i < l + 1; i++) {
				dataX[i] = toDouble(dList.get(i).get("volume"));
				dataY[i] = toDouble(dList.get(i).get("dial"));
				if (dataY[i] == 0) {
					// 数据有问题
					return;
				}
			}

			m = rlan.lm(dataX, dataY);

			pOption.data(Float.valueOf(String.format("%.2f", m)));

		}
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

			itemStyle.setNormal(normal);

			pOption.itemStyle(itemStyle);
		}
		// pOption.yAxisIndex(1);

		pOption.name(_MLang("lm")).itemStyle().normal().lineStyle();

		option.legend(_MLang("lm"));

		option.series(pOption);
		rlan.close();
	}

	private void math_sma() {
		option.legend(_MLang("sma"));

		List<Map<String, Object>> list = pieList.get(0);

		Line line = new Line();
		line.smooth(true).name(_MLang("sma")).itemStyle().normal().lineStyle();

		float front = 0;
		float x = 0;
		float m;
		int l = 1;
		for (Map<String, Object> key : list) {

			x = toFloat(key.get("volume"));

			front += x;

			m = front / l;

			l++;
			line.data(Float.valueOf(String.format("%.2f", m)));

		}

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

	private void math_wma() {
		option.legend(_MLang("wma"));

		List<Map<String, Object>> list = pieList.get(0);

		Line line = new Line();
		line.smooth(true).name(_MLang("wma")).itemStyle().normal().lineStyle();

		float front = 0;
		float x = 0;
		float m;
		int l = 1;
		for (Map<String, Object> key : list) {

			x = toFloat(key.get("volume"));

			front += x;

			m = front / l;

			l++;
			line.data(Float.valueOf(String.format("%.2f", m)));

		}

		option.series(line);
	}

	private String JsonScatter() {

		option.tooltip().trigger(Trigger.item).formatter("{c}");
		List<Map<String, Object>> list;
		if (pieList == null || pieList.size() == 0)
			return "";

		int l = 0;

		for (Map<String, String> id : JsonIds) {
			if (toInt(id.get("id")) == 0) {
				continue;
			}

			list = pieList.get(l);
			l++;

			Scatter scatter = new Scatter();

			option.legend(id.get("name").toString());
			for (Map<String, Object> key : list) {
				List<Object> m = new ArrayList<>();

				float val = toFloat(key.get("dial"));
				if (val == 0) {
					m.add(key.get("dial"));
				} else {
					m.add(Float.valueOf(String.format("%.2f", val)));
				}

				val = toFloat(key.get("volume"));
				if (val == 0) {
					m.add(key.get("volume"));
				} else {
					m.add(Float.valueOf(String.format("%.2f", val)));
				}

				scatter.data(m);
			}
			scatter.setName(id.get("name").toString());

			option.series(scatter);
			option.title(id.get("name").toString());
		}

		ValueAxis xAxis = new ValueAxis();
		xAxis.scale(true);
		xAxis.type(AxisType.value);

		ValueAxis yAxis = new ValueAxis();
		yAxis.scale(true);
		yAxis.type(AxisType.value);

		option.xAxis(xAxis);
		option.yAxis(yAxis);

		return option.toString();
	}

	private String JsonRadar() {
		Polar polar = new Polar();
		Legend legend = new Legend();
		legend.x(X.right).y(Y.bottom).orient(Orient.vertical);

		List<Map<String, Object>> indicator = new ArrayList<>();

		option.tooltip().trigger(Trigger.axis);

		if (pieList == null || pieList.size() == 0)
			return "";

		int l = 0;

		Map<String, Object> data, iMap;

		for (Map<String, String> id : JsonIds) {
			if (toInt(id.get("id")) == 0) {
				continue;
			}

			List<Map<String, Object>> list = pieList.get(l);

			Radar radar = new Radar();
			legend.data(id.get("name").toString());

			int i = 0;
			data = new HashMap<>();
			List<Object> m = new ArrayList<>();
			;

			for (Map<String, Object> key : list) {

				float val = toFloat(key.get("volume"));
				if (val == 0) {
					m.add(key.get("volume"));
				} else {
					m.add(Float.valueOf(String.format("%.2f", val)));
				}
				String dial = key.get("dial").toString();

				if (l == 0) {
					iMap = new HashMap<>();
					iMap.put("text", dial);
					iMap.put("max", Math.ceil(val));
					indicator.add(iMap);
				} else {
					if (indicator.get(i).get("text").equals(dial)
							&& Double.valueOf(indicator.get(i).get("max")
									.toString()) < val) {
						indicator.get(i).put("max", Math.ceil(val));
					}
				}
				i++;

				// m.add(val);
			}
			data.put("value", m);
			data.put("name", id.get("name").toString());
			radar.data(data);
			l++;

			radar.setName(id.get("name").toString());

			option.series(radar);
			// option.title(id.get("name").toString());
		}
		for (int i = 0; i < indicator.size(); i++) {
			polar.indicator(indicator.get(i));
		}

		option.polar(polar);
		option.legend(legend);
		return option.toString();
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
			// Title title = new Title();
			// title.text(id.get("name").toString());
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
		// List<List<Map<String, Object>>> mapList = getEcharts();
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

		if (pieList == null || pieList.size() == 0)
			return "";
		// List<String> legendTitle = new ArrayList<>();
		int l = 0;
		for (Map<String, String> id : JsonIds) {
			if (!id.get("id").toString().matches("[0-9]+"))
				continue;

			option.legend(id.get("name").toString());

			List<Map<String, Object>> list = pieList.get(l);
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
		int dtype = 0;
		String sql = "";
		int tid = 0;
		Map<String, Object> ures;
		Map<String, Object> tres;
		String usql = "";
		Map<String, String> tList = null;
		List<List<String>> uList = null;
		Map<String, Object> tmpRes;
		String format = "select rule,volume,dial from " + DB_HOR_PRE
				+ "webvisitcount  where rule = %d and dial > 2015011100 and "
				+ UserPermi() + " order by rule, dial ;";
		// why ?
		List<Map<String, Object>> res = null;
		String tmp_list = porg.getKey("tmp_list");
		Map<String, String> tmp_map = null;

		if (tmp_list != null) {
			tmp_map = JSON.parseObject(tmp_list, Map.class);
		}

		for (Map<String, String> id : JsonIds) {

			qaction = toInt(id.get("qaction"));

			if (qaction == 0)
				continue;

			sql = String.format(format, toInt(id.get("id")));
			tid = toInt(id.get("id"));
			if (qaction == 4) {

				usql = "select sql,dtype from " + DB_HOR_PRE
						+ "usersql where id=" + tid + " and " + UserPermi()
						+ " LIMIT 1";

				ures = FetchOne(usql);

				if (ures != null) {
					sql = ures.get("sql").toString();
					sql = escapeHtml(sql);
					dtype = toInt(ures.get("dtype"));
				}

			} else if (qaction == 5) {

				sql = "select t.sqltmp,u.sql,u.dtype,u.sqltmp as usqltmp from "
						+ DB_HOR_PRE + "sqltmp t, " + DB_HOR_PRE
						+ "usersql u where t.sid=u.id and (u.uid = "
						+ aclGetUid() + " or u.isshare=1) and t.id=" + tid
						+ " limit 1";

				tres = FetchOne(sql);
				// echo(tsql);
				if (tres == null)
					continue;

				sql = tres.get("sql").toString();
				String tJson = tres.get("sqltmp").toString();
				String uJson = tres.get("usqltmp").toString();
				dtype = toInt(tres.get("dtype"));
				tList = JSON.parseObject(tJson, Map.class);

				uList = JSON.parseObject(uJson, List.class);

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
						+ "usersql where id=" + tid + " and " + UserPermi()
						+ " limit 1";

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
				int start = time();
				if (dtype == 0) {

					res = FetchAll(sql);
				} else {
					res = CustomDB(sql, dtype);
				}
				int end = time();

				if (res != null) {
					ret.add(res);
					// echo("time:"+(end - start));
					if ((end - start) > mConfig.GetInt("cache.time")) {
						setCache(sql, id.get("name").toString(), res);
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return ret;
	}

	private List<List<Map<String, Object>>> getRList() {
		List<List<Map<String, Object>>> ret = new ArrayList<>();
		String format = "select rcode  from " + DB_HOR_PRE
				+ "rlanguage where id=%s limit 1";
		String sql = "";
		Map<String, Object> res;
		Rlan rcoonnect = Rlan.getInstance();
		ProjectPath pp = ProjectPath.getInstance();
		URI uri = pp.DataDir();
		String path = uri.getPath().substring(1);
		String setwd = String.format("setwd('%s')", path);
		//String[] scode;
		String rcode = "";
		REXP r;
		try {			
			rcoonnect.connection().voidEval(setwd);
			for (Map<String, String> id : JsonIds) {
				RListJson = new ArrayList<>();
				index = 0;
				rcode = "";
				sql = String.format(format, id.get("id").toString());
				res = FetchOne(sql);
				if (res == null)
					continue;
				rcode = res.get("rcode").toString();
				
				rcode = Escape.unescape(rcode);
				rcode = rcode.replace("\r", "");
//				//echo(rcode);
//				scode = rcode.split("\n");
//				//echo("length:"+scode.length);
//				for (int i = 0; i < scode.length-1; i++) {
//					echo(scode[i]);
//					rcoonnect.connection().voidEval(scode[i]);
//				}
				r = rcoonnect.connection().eval(rcode);
				
				if (r != null) {
					//SelectREXP(r._attr(), "attr");
					SelectREXP(r, "val");
					ret.add(RListJson);
					//echo("end");
				}				
			}
			
		} catch (RserveException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();			
			echo(rcoonnect.connection().getLastError());
		}finally{
			rcoonnect.close();
		}
		return ret;
	}

	private void SelectREXP(REXP r, String key) {
		// REXP r = c.eval("print(df)");
		if (r == null)
			return;
		try {
			if (r.isComplex()) {
				echo("isComplex");
			} else if (r.isEnvironment()) {
				echo("isEnvironment");
			} else if (r.isExpression()) {
				echo("isExpression");
			} else if (r.isFactor()) {
				RFactor factors = r.asFactor();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(key, factors.asStrings());
				RListJson.add(map);
				echo("isFactor");
			} else if (r.isInteger()) {
				int[] rint = r.asIntegers();
				Map<String, Object> map = new HashMap<String, Object>();
				if (key == null || key.length() == 0) {
					map.put(index + "", rint);
					index++;
				} else {
					map.put(key, rint);
				}
				SelectREXP(r._attr(), key);
				RListJson.add(map);
				echo("isInteger");
			} else if (r.isLanguage()) {
				RList lanList = r.asList();
				String nkey = "";
				for (int i = 0; i < lanList.size(); i++) {
					nkey = lanList.keyAt(i);
					if (nkey == null)
						nkey = key;
					SelectREXP(lanList.at(i), nkey);
				}
				echo("isLanguage");
			} else if (r.isList()) {
				echo("isList");
				RList rList;
				String nkey = "";
				rList = r.asList();

				for (int i = 0; i < rList.size(); i++) {
					nkey = rList.keyAt(i);
					if (nkey == null)
						nkey = key;
					if (nkey.equals("dim"))
						continue;
					SelectREXP(rList.at(i), nkey);
				}
			} else if (r.isLogical()) {
				String[] b = r.asStrings();
				Map<String, Object> map = new HashMap<String, Object>();
				if (key == null || key.length() == 0) {
					map.put(index + "", b);
					index++;
				} else {
					map.put(key, b);
				}

				RListJson.add(map);
				echo("isLogical");
			} else if (r.isNull()) {
				echo("isNull");
			} else if (r.isNumeric()) {
				Map<String, Object> map = new HashMap<String, Object>();

				int[] dim = r.dim();

				if (dim == null || dim.length == 1) {

					double[] m = r.asDoubles();

					if (key.length() == 0) {
						map.put(index + "", m);
						index++;
					} else {
						map.put(key, m);
					}

				} else {
					double[][] md = r.asDoubleMatrix();

					if (key == null || key.length() == 0) {
						map.put(index + "", md);
						index++;
					} else {
						map.put(key, md);
					}

				}

				RListJson.add(map);
				echo("isNumeric");
			} else if (r.isPairList()) {
				echo("isPairList");
			} else if (r.isRaw()) {
				echo("isRaw");
			} else if (r.isRecursive()) {
				echo("isRecursive");
			} else if (r.isReference()) {
				echo("isReference");
			} else if (r.isString()) {
				String[] d = r.asStrings();

				Map<String, Object> map = new HashMap<String, Object>();
				if (key == null || key.length() == 0) {
					map.put(index + "", d);
					index++;
				} else {
					map.put(key, d);
				}

				RListJson.add(map);
				echo("isString");
			} else if (r.isSymbol()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(key, r.asString());
				RListJson.add(map);
				echo("isSymbol");
			} else if (r.isVector()) {
				echo("isVector");
			} else {
				echo("else");
				echo(r.isNull());
			}

		} catch (REXPMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getCache(String key) {
		Encrypt ec = Encrypt.getInstance();
		String md5Key = ec.MD5(key);
		String json = "";
		String format = "select json from " + DB_HOR_PRE
				+ "cache where md5='%s' and " + UserPermi();
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

		String format = "select * from " + DB_HOR_PRE + "dbsource where id=%d "
				+ UserPermi() + " limit 1";
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
		// build table for editor
		PaserJson();

		List<List<Map<String, Object>>> eList = getEcharts();
		String listJson = JSON.toJSONString(eList);

		super.setHtml(listJson);
	}

}
