package com.lib.manager.dashboard;

import java.sql.SQLException;
import java.util.List;

import org.ppl.BaseClass.BasePerminterface;
import org.ppl.BaseClass.Permission;


public class SaveDoc extends Permission implements BasePerminterface {
	private List<String> rmc;

	public SaveDoc() {
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
		switch (rmc.get(1).toString()) {
		case "create":
			create(null);
			break;
		default:
			Msg(_CLang("error_role"));
			return;
		}
	}

	@Override
	public void read(Object arg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void create(Object arg) {
		// TODO Auto-generated method stub
		String doc = porg.getKey("doc");
		String title = porg.getKey("title");

		if (doc == null || title == null) {
			super.setHtml(_CLang("error_null"));
			return;
		}

		//BuildDoc(doc);

		String format = "insert into " + DB_HOR_PRE
				+ "doc(title,doc,ctime)VALUES('%s','%s', %d)";
		String sql = String.format(format, title, doc, time());

		try {
			insert(sql);
			super.setHtml(_CLang("ok_save"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			super.setHtml(_CLang("error_save"));
		}
	}
	/*
	@SuppressWarnings("unused")
	private void BuildDoc(String content) {
		//
		 ProjectPath pp = ProjectPath.getInstance();
		//
		// pp.SaveFile("dd.doc", val);

		String fileName = "a.doc";

		try {
			String path = SaveDoc.class.getClassLoader().getResource("../../Data/").getPath();
			byte b[] = content.getBytes();
			ByteArrayInputStream bais = new ByteArrayInputStream(b);
			POIFSFileSystem poifs = new POIFSFileSystem();
			DirectoryEntry directory = poifs.getRoot();
			DocumentEntry documentEntry = directory.createDocument(
					"WordDocument", bais);
			FileOutputStream ostream = new FileOutputStream(path + fileName);
			poifs.writeFilesystem(ostream);
			bais.close();
			ostream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/

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
