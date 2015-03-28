package com.lib.manager.user;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.ppl.BaseClass.Permission;
import org.ppl.etc.UrlClassList;
import org.ppl.io.Encrypt;
import org.ppl.io.ProjectPath;
import org.ppl.io.TimeClass;

public class my_profile extends Permission {

	public my_profile() {
		// TODO Auto-generated constructor stub
		String className = this.getClass().getCanonicalName();
		// stdClass = className;
		super.GetSubClassName(className);
		setRoot("name", _MLang("name"));
		setRoot("fun", this);
		super.setAction(1);
	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		if (super.Init() == -1){
			return;
		}
		String edit_id = porg.getKey("edit_id");
		UrlClassList ucl = UrlClassList.getInstance();
		setRoot("action_url", ucl.BuildUrl(SliceName(stdClass), ""));

		if (edit_id != null) {
			setRoot("alerts", "q");
			setRoot("save_msg", editMyProfile());
			setRoot("nickname", porg.getKey("nickname"));
			setRoot("username", porg.getKey("username"));
			setRoot("email", porg.getKey("email"));
			setRoot("phone", porg.getKey("phone"));
		} else {
			setRoot("nickname", aclGetNickName());
			setRoot("username", aclGetName());
			setRoot("email", aclGetEmail());
			setRoot("phone", aclGetPhone());
		}
		super.View();
	}

	private String editMyProfile() {
		
		String nickname = porg.getKey("nickname");
		String email = porg.getKey("email");
		String phone = porg.getKey("phone");
		String pass1 = porg.getKey("pass1");
		String pass2 = porg.getKey("pass2");

		String pwd = "";
		if (!pass1.isEmpty() && !pass2.isEmpty()) {
			if (!pass1.equals(pass2)) {
				return _CLang("error_pwdneq");

			}
			Encrypt en = Encrypt.getInstance();
			pwd = ", `passwd`='" + en.MD5(pass1) + "' ";
		}

		TimeClass tc = TimeClass.getInstance();
		int now = (int) tc.time();

		String format = "UPDATE `"
				+ DB_PRE
				+ "user_info` SET `nickname` = '%s',`email`='%s',`etime`='%d',`phone`='%s'"
				+ pwd + " WHERE `uid` = %s";
		String sql = String.format(format, nickname, email, now, phone,
				aclGetUid());
		try {
			update(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			SaveLogo();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return _CLang("ok_save");
	}
	
	private void SaveLogo() throws FileNotFoundException {
		String name = porg.getUpload_name().get("user_logo_file").toString();
		byte[] val = porg.getUpload_string().get("user_logo_file");
		if(val.length<1)return;
		ProjectPath pp = ProjectPath.getInstance();
		pp.SaveFile(name, val);

	}

}
