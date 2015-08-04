package com.lib.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Map;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.ppl.BaseClass.BaseRapidThread;

public class updateCSVData extends BaseRapidThread {

	private Map<String, Object> ThreadMail;

	@Override
	public void Run() {
		// TODO Auto-generated method stub
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
			if(bytesAsString==null) return;
			
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
				
				if (copyManager != null) {
					copyManager.copyIn("COPY " + DB_HOR_PRE + "class (" + field
							+ ") FROM STDIN DELIMITER ',' ", reader);	
										
					format = "CREATE VIEW %s AS SELECT %s FROM " + DB_HOR_PRE
							+ "class WHERE rule=%d";
					Sql = String.format(format, view_name, asName, rule);
					
					dbcreate(Sql);
									
					Sql = "update " + DB_HOR_PRE + "class  set rule=" + rule
							+ " where rule=0";
					
					update(Sql);
				}else {
					echo("error");
				}
								

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
}
