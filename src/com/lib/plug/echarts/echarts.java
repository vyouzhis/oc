package com.lib.plug.echarts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.Magic;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Line;

/**
 * @since old
 * @author 
 *
 */
public class echarts {
	public final static String Center ="center"; 
	public List<String> trigger;
	public List<String> formatter;
	private String legend_y = "bottom";
	private List<String> legendData;
	
	public echarts() {
		// TODO Auto-generated constructor stub
		trigger = new ArrayList<>();
		trigger.add("axis");
		trigger.add("item");
		
		formatter = new ArrayList<>();
		formatter.add("{a} <br/>{b} : {c} ({d}%)");
		formatter.add("{a} <br/>{b} : ({c}%)");
	}
	
	
	public String BuildBar() {
		trigger.get(0);
		return null;
	}
	
	public String BuildLine() {
		trigger.get(0);
		return null;
	}
	
	public String BuildPie() {
		trigger.get(1);
		return null;
	}
	
	public String BuildScatter() {
		trigger.get(0);
		return null;
	}
	
	public String BuildFunnel() {
		trigger.get(1);
		return null;
	}
	
	public Map<String, String> Title(String title, String subtext, String x) {
		Map<String, String> t = new HashMap<String, String>();
		t.put("text", title);
		t.put("subtext", subtext);
		t.put("x", x);
		return t;
	}


	public List<String> getLegendData() {
		return legendData;
	}


	public void setLegendData(List<String> legendData) {
		this.legendData = legendData;
	}
	
	public String getResult() {
		GsonOption  option = new GsonOption();
	    option.legend("高度(km)与气温(°C)变化关系");
	 
	    option.toolbox().show(true).feature(Tool.mark, Tool.dataView, new MagicType(Magic.line, Magic.bar), Tool.restore, Tool.saveAsImage);
	 
	    option.calculable(true);
	    option.tooltip().trigger(Trigger.axis).formatter("Temperature : <br/>{b}km : {c}°C");
	 
	    ValueAxis valueAxis = new ValueAxis();
	    valueAxis.axisLabel().formatter("{value} °C");
	    option.xAxis(valueAxis);
	 
	    CategoryAxis categoryAxis = new CategoryAxis();
	    categoryAxis.axisLine().onZero(false);
	    categoryAxis.axisLabel().formatter("{value} km");
	    categoryAxis.boundaryGap(false);
	    categoryAxis.data(0, 10, 20, 30, 40, 50, 60, 70, 80);
	    option.yAxis(categoryAxis);
	 
	    Line line = new Line();
	    line.smooth(true).name("高度(km)与气温(°C)变化关系").data(15, -50, -56.5, -46.5, -22.1, -2.5, -27.7, -55.7, -76.5).itemStyle().normal().lineStyle().shadowColor("rgba(0,0,0,0.4)");
	    option.series(line);
	    //option.exportToHtml("line5.html");
	    //option.view();
	    return option.toString();
	}

}
