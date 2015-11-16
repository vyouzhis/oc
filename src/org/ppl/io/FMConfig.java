package org.ppl.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class FMConfig {
	static FMConfig fmc = null;
	static Configuration cfg = null;
	private static Map<String, Template> TempList;

	public static FMConfig getInstance() {
		if (fmc == null) {
			fmc = new FMConfig();
			TempList = new HashMap<>();
			Init();
		}

		return fmc;
	}

	public static void Init() {
		ProjectPath pp = ProjectPath.getInstance();

		cfg = new Configuration();
		try {
			File file = new File(pp.ViewDir());
			cfg.setDirectoryForTemplateLoading(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		cfg.setDefaultEncoding("UTF-8");
	}

	public Template getTemplate(String path) {
		if (TempList.containsKey(path))
			return TempList.get(path);
		try {
			Template temple = cfg.getTemplate(path + ".html");
			temple.setEncoding("UTF-8");
			TempList.put(path, temple);

			return temple;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
