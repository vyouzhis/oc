package com.lib.manager.dashboard;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;

import com.alibaba.fastjson.JSON;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.AxisType;
import com.github.abel533.echarts.code.Magic;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Line;

public class EchartsJson extends Permission implements BasePerminterface {
	private List<String> rmc;
	private GsonOption option = null;

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
		option.tooltip().trigger(Trigger.axis);
		// .formatter("{a} <br/>{b} : ({c}%)");

		CategoryAxis categoryAxis = new CategoryAxis();
		categoryAxis.axisLine().onZero(false);
		categoryAxis.setType(AxisType.value);
		categoryAxis.axisLabel().formatter("{value}");
		option.yAxis(categoryAxis);
	}

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub
		super.setHtml(Line());
	}

	@SuppressWarnings("unchecked")
	public String Line() {
		String json_id = porg.getKey("ids");
		json_id = Myreplace(json_id);
		if (json_id.length() < 2) {
			return "";
		}

		ValueAxis valueAxis = new ValueAxis();
		valueAxis.setType(AxisType.category);
		// valueAxis.axisLabel().formatter("{value} °C");

		List<Map<String, String>> ids = JSON.parseObject(json_id, List.class);
		String format = "select rule,volume,dial from "
				+ DB_HOR_PRE
				+ "webvisitcount  where rule = %d and dial > 2015011100 order by rule, dial ;";
		boolean Xbool = true;
		List<Map<String, Object>> res;

		for (Map<String, String> id : ids) {
			if (!id.get("id").toString().matches("[0-9]+"))
				continue;

			String sql = String.format(format,
					Integer.valueOf(id.get("id").toString()));
			if(id.get("qaction").equals("4")){
				String usql = "select sql from hor_usersql where id="+id.get("id").toString()+" LIMIT 1";
				Map<String, Object> ures;
				
				ures = FetchOne(usql);
				if(ures!=null){
					sql = ures.get("sql").toString();
					sql = Myreplace(sql);
					//echo("sql:"+sql);
				}
			}
			try {
				res = FetchAll(sql);
				if (res != null && res.size() > 0) {
					// ListVolume = new ArrayList<>();
					// Listdial = new ArrayList<>();
					option.legend(id.get("name").toString());
					Line line = new Line();
					line.smooth(true).name(id.get("name").toString())
							.itemStyle().normal().lineStyle();
					for (Map<String, Object> key : res) {
						// ListVolume.add(Integer.valueOf(key.get("volume")
						// .toString()));
						// Listdial.add(Integer.valueOf(key.get("dial").toString()));
						// echo(Listdial);
						if (Xbool) {
							valueAxis.data(key.get("dial").toString());
						}
						line.data(key.get("volume"));
					}
					// echo(ListVolume);
					Xbool = false;

					option.series(line);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		option.xAxis(valueAxis);

		if (Xbool){
			return "";
		}
		return option.toString();
	}
	
	private String Pie() {
		return null;
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
