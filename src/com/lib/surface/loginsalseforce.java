package com.lib.surface;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.ppl.BaseClass.BaseSurface;
import org.ppl.net.cUrl;

import com.alibaba.fastjson.JSON;

public class loginsalseforce  extends BaseSurface{
	static final String USERNAME = "vyouzhi@gmail.com";
	static final String PASSWORD = "@#123qazWSXgLRQaiyNtFUO5MVyQNv4QvzjD";
	static final String LOGINURL = "https://login.salesforce.com/services/oauth2/token";
	static final String GRANTSERVICE = "/services/oauth2/token?grant_type=password";
	static final String CLIENTID = "3MVG9ZL0ppGP5UrDgARSEUtQHrlz1PJBOMsuumwj4jdOGoCW19ZG.Hur_h8aR6Urk7WPrYY.MUhE.L6m.iDgi";
	static final String CLIENTSECRET = "7906767619537109943";
	private static String REST_ENDPOINT = "/services/data";
	private static String API_VERSION = "/v30.0";
	private static String baseUri;
	private static Header oauthHeader;
	private static Header prettyPrintHeader = new BasicHeader("X-PrettyPrint",
			"1");
	private static String leadId;
	private static String leadFirstName;
	private static String leadLastName;
	private static String leadCompany;
	
	public loginsalseforce() {
		// TODO Auto-generated constructor stub
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
		isAutoHtml = false;
	}
	
	@Override
	public void Show() {
		// TODO Auto-generated method stub
		String LogS = "";
		String redirectUri = "http://localhost:8080/bi/testsalsefoce";
		cUrl mCUrl = new cUrl();
		mCUrl.addParams("client_id", CLIENTID);
		mCUrl.addParams("client_secret", CLIENTSECRET);
		mCUrl.addParams("username", USERNAME);
		mCUrl.addParams("password", PASSWORD);
		mCUrl.addParams("grant_type", "password");
		try {
			mCUrl.addParams("redirect_uri", URLEncoder.encode(redirectUri, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String getResult = mCUrl.httpPost(LOGINURL);
		if(getResult==null){
			echo("httpport null");
			return;
		}
		LogS += "\r\n"+getResult;

		JSONObject jsonObject = null;
		String loginAccessToken = null;
		String loginInstanceUrl = null;
		String loginId = null;
		try {
			jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
			loginAccessToken = jsonObject.getString("access_token");
			cookieAct.SetCookie("access_token", loginAccessToken);
			loginInstanceUrl = jsonObject.getString("instance_url");
			cookieAct.SetCookie("instance_url", loginInstanceUrl);
			loginId = jsonObject.getString("id");
		} catch (JSONException jsonException) {
			jsonException.printStackTrace();
		}

		baseUri = loginInstanceUrl + REST_ENDPOINT + API_VERSION;
		oauthHeader = new BasicHeader("Authorization", "OAuth "
				+ loginAccessToken);
		LogS += "\r\n"+"oauthHeader1: " + oauthHeader;
		// LogS += "\r\n"+"\n" + response.getStatusLine());
		LogS += "\r\n"+"Successful login";
		LogS += "\r\n"+"instance URL: " + loginInstanceUrl;
		LogS += "\r\n"+"access token/session ID: " + loginAccessToken;
		LogS += "\r\n"+"baseUri: " + baseUri;

		String userIdEndpoint = loginId;
		String accessToken = loginAccessToken;
		List<BasicNameValuePair> qsList = new ArrayList<BasicNameValuePair>();
		qsList.add(new BasicNameValuePair("oauth_token", accessToken));
		String queryString = URLEncodedUtils.format(qsList, HTTP.UTF_8);
		HttpGet userInfoRequest = new HttpGet(userIdEndpoint + "?"
				+ queryString);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse userInfoResponse;
		try {
			userInfoResponse = client.execute(userInfoRequest);
			@SuppressWarnings("unchecked")
			Map<String, Object> userInfo = (Map<String, Object>) JSON
					.parse(EntityUtils.toString(userInfoResponse.getEntity()));
			LogS += "\r\n"+"User info response";
			for (Map.Entry<String, Object> entry : userInfo.entrySet()) {
				LogS += "\r\n"+String.format("  %s = %s", entry.getKey(),
						entry.getValue());
			}
			LogS += "\r\n"+"";

			// Use the user info in interesting ways.
			LogS += "\r\n"+"Username is " + userInfo.get("username");
			LogS += "\r\n"+"User's email is " + userInfo.get("email");
			Map<String, String> urls = (Map<String, String>) userInfo.get("urls");
			LogS += "\r\n"+"REST API url is "
					+ urls.get("rest").replace("{version}", "34.0");
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		super.setHtml("["+LogS+"]");
	}
}
