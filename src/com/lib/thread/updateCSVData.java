package com.lib.thread;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.ppl.BaseClass.BaseRapidThread;

import com.lib.manager.analysis.sqledit;
import com.opencsv.CSVReader;

public class updateCSVData extends BaseRapidThread {

	private Map<String, Object> ThreadMail;

	@Override
	public void Run() {
		// TODO Auto-generated method stub
		// ucsv();
		vcsCopy();
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

	@SuppressWarnings("unchecked")
	@Override
	public void mailbox(Object o) {
		// TODO Auto-generated method stub
		ThreadMail = (Map<String, Object>) o;
	}

	@SuppressWarnings("unchecked")
	private void ucsv() {
		if (ThreadMail == null)
			return;
		echo("start csv data!");
		Map<String, byte[]> file = (Map<String, byte[]>) ThreadMail
				.get("csv_file");
		long rule = (long) ThreadMail.get("rule");
		String view_name = (String) ThreadMail.get("view_name");

		int m = 0;
		String field = "";
		String values = "";
		String view_field = "";
		for (String key : file.keySet()) {

			String bytesAsString = null;
			try {
				bytesAsString = new String(file.get(key), "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				// e1.printStackTrace();
				echo("error file get string");
				return;
			}

			StringReader reader = new StringReader(bytesAsString);

			try {
				int offset = 0; // 如果有空格，就往下跳一格
				CSVReader csvReader = new CSVReader(reader);
				String[] strs;
				strs = csvReader.readNext();
				if (strs != null && strs.length > 0) {
					for (String str : strs) {

						if (null != str && !str.equals("")) {
							view_field += "act_v" + Integer.toHexString(m)
									+ " AS " + str.trim().replace(" ", "_")
									+ ", ";
							field += "act_v" + Integer.toHexString(m) + ", ";
							m++;
						} else if (m == 0) {
							offset++;
						}
					}
				}
				int l = 0, next = 0;
				List<String[]> list;
				list = csvReader.readAll();
				for (String[] ss : list) {
					values += "(";
					l = 0;
					next = 0;
					for (String s : ss) {
						if (next < offset) {
							next++;
							continue;
						}
						if (null != s && l < m) {

							values += "'" + s.trim() + "',";
						}
						l++;
					}
					while (l < m) {
						values += "'',";
						l++;
					}
					values += rule + "),";
					// echo("values ---"+time());
				}
				csvReader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		view_field = clear(view_field);

		field += " rule";

		String dropview = "drop view if exists " + view_name;
		try {
			dbcreate(dropview);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String format = "CREATE VIEW %s AS SELECT %s FROM " + DB_HOR_PRE
				+ "class WHERE rule=%d";
		String Sql = String.format(format, view_name, view_field, rule);

		try {
			dbcreate(Sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		values = clear(values);
		String format_class = "INSERT INTO " + DB_HOR_PRE
				+ "class (%s) VALUES %s ;";
		Sql = String.format(format_class, field, values);

		try {
			dbcreate(Sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		echo("ok updatecsv");
	}

	@SuppressWarnings("unchecked")
	private void vcsCopy() {

		Map<String, byte[]> file = (Map<String, byte[]>) ThreadMail
				.get("csv_file");
		long rule = (long) ThreadMail.get("rule");
		String view_name = (String) ThreadMail.get("view_name");

		CopyManager copyManager = null;
		String bytesAsString = null;
		StringReader readers = null;
		BufferedReader reader = null;
		String line = null;
		String format = "";
		String Sql = "";
		String dropview = "";
		String field = "";
		try {

			copyManager = new CopyManager(getCon().unwrap(BaseConnection.class));
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return;
		}

		for (String key : file.keySet()) {

			try {
				bytesAsString = new String(file.get(key), "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				// e1.printStackTrace();
				echo("error file get string");
				return;
			}

			readers = new StringReader(bytesAsString);
			reader = new BufferedReader(readers);
			try {
				line = reader.readLine();
				line = line.replace(" ", "_");

				dropview = "drop view if exists " + view_name;
				try {
					dbcreate(dropview);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				
				String[] lines = line.split(",");
				String act = "";
				String asName = "";
				for (int m = 0; m < lines.length; m++) {
					act = "act_v" + Integer.toHexString(m);
					asName += act +" as "+lines[m]+",";
					field +=  act+ ", ";
				}
				field = field.trim();
				field = field.substring(0, field.length()-1);
				asName = asName.substring(0, asName.length()-1);
				
				//System.out.println(field);
				if (copyManager != null) {
					copyManager.copyIn("COPY " + DB_HOR_PRE + "class (" + field
							+ ") FROM STDIN DELIMITER ',' ", reader);					
				}

				
				format = "CREATE VIEW %s AS SELECT %s FROM " + DB_HOR_PRE
						+ "class WHERE rule=%d";
				Sql = String.format(format, view_name, asName, rule);
				
				//echo(Sql);
				dbcreate(Sql);
								
				Sql = "update " + DB_HOR_PRE + "class  set rule=" + rule
						+ " where rule=0";
				
				//echo(Sql);
				update(Sql);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			echo("Done.");
		}

	}

	private String clear(String v) {
		String s = v.trim();
		if (s.length() > 1) {
			return s.substring(0, s.length() - 1);
		} else {
			return v;
		}
	}

}
