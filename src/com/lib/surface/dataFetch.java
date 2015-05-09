package com.lib.surface;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.ppl.BaseClass.BaseSurface;
import org.ppl.db.MGDB;
import org.ppl.plug.wryip.FileUtil;
import org.ppl.plug.wryip.IPLocation;
import org.ppl.plug.wryip.IPSeeker;

import com.alibaba.fastjson.JSON;
import com.fasterxml.uuid.Generators;

public class dataFetch extends BaseSurface {
	private HttpServletResponse response;
	private MGDB mgdb = new MGDB();
	private Map<String, Object> BiData = new HashMap<String, Object>();
	private String uuid = null;
	protected String Col = mConfig.GetValue("db.mon.col");
	protected String IpCol = mConfig.GetValue("db.mon.ipcol");

	public dataFetch() {
		// TODO Auto-generated constructor stub
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
		isAutoHtml = false;
	}

	@Override
	public void Show() {
		SetUUid();
		
		OutPutGif();
		//TellPostMan(ThreadName, message);
		GetData();
		//close mongodb 
		mgdb.Close();
	}

	public void OutPutGif() {
		// TODO Auto-generated method stub
		response = porg.getHsr();
		response.setDateHeader("Expires", 0);
		// Set standard HTTP/1.1 no-cache headers.
		response.setHeader("Cache-Control",
				"no-store, no-cache, must-revalidate");
		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Set standard HTTP/1.0 no-cache header.
		response.setHeader("Pragma", "no-cache");
		// return a gif
		response.setContentType("image/gif");

		// create the text for the image

		ServletOutputStream out;
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		// write the data out
		try {
			out = response.getOutputStream();
			ImageIO.write(bi, "gif", out);
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void GetData() {

		// 保存到Mongodb
		
		Map<String, String> porg_new = porg.getAllpg();

		if (porg_new == null) {
			// echo("null");
			return;
		}
		String browser = porg.getKey("browser");
		String os = porg.getKey("os");
		if (browser == null || os == null) {
			// echo("null");
			return;
		}
		if (browser.length() < 2 || os.length() < 2) {
			// echo("length:" + browser.length());
			return;
		}
		browser = StringEscapeUtils.unescapeHtml3(browser);
		os = StringEscapeUtils.unescapeHtml3(os);
		try {
			Map<String, Object> br_new = JSON.parseObject(browser, Map.class);
			Map<String, Object> os_new = JSON.parseObject(os, Map.class);
			BiData.put("browser", br_new);
			BiData.put("os", os_new);
			porg_new.remove("browser");
			porg_new.remove("os");
		} catch (Exception e) {
			// TODO: handle exception
		}
				
		BiData.put("PorG", porg_new);

		BiData.put("ctime", time()); // create time

		IPtoAddr();
		
		
		String json = JSON.toJSONString(BiData);
		mgdb.SetCollection(Col);
		mgdb.Insert(json);
	}

	private void SetUUid() {
		uuid = cookieAct.GetCookie("uuid");
		if(uuid==null){
			UUID uid = Generators.randomBasedGenerator().generate();
			uuid = uid.toString();
			cookieAct.SetCookie("uuid", uuid, 63072000);//63072000=2*365*24*60*60;即2年。
			BiData.put("uuid",uuid);
		}else{
			BiData.put("uuid", uuid);
		}
	}
	
	
	private void IPtoAddr() {
		String city = "";
		IPSeeker seeker = null;
		Path ipWry = null;
		String ip = porg.GetIP();
		if (ip == null)
			return;
		try {
			FileUtil fu = new FileUtil();
			ipWry = fu.classpath("properties/qqwry.dat");
			seeker = new IPSeeker(ipWry);
			IPLocation location = seeker.getIP(ip);
			city = location.getCountry();
			BiData.put("City", city);

			if (city == "" || city.equals("局域网")) {
				BiData.put("CityId", 0);
				return;
			} else if (city.length() > 2) {
				city = location.getCountry().substring(0, 2);
			}
			// echo("city:" + city);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Integer Id = 0;
		String field = "{\"id\":1}";
		String DBwhere = "{\"city\":{\"$regex\":\"" + city + "\"}}";
		// echo("field:" + field);
		mgdb.SetCollection(IpCol);
		// echo("IpCol:" + IpCol);
		mgdb.JsonWhere(DBwhere);
		mgdb.JsonColumn(field);
		mgdb.JsonSort(field);
		// mgdb.setLimit(1);
		boolean s = mgdb.FetchList();
		// echo("bollean:" + s);
		if (s) {
			List<Map<String, Object>> res = mgdb.GetValue();
			// echo("res:" + res);
			Id = Integer.valueOf(res.get(0).get("id").toString());
		} else {// 查询不到，则设置为国外
			Id = 100;
		}
		BiData.put("CityId", Id);
	}

}
