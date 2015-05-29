package com.lib.surface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.ppl.BaseClass.BaseSurface;

public class testsalsefoce extends BaseSurface{
	public testsalsefoce() {
		// TODO Auto-generated constructor stub
		String className = null;
		className = this.getClass().getCanonicalName();
		super.GetSubClassName(className);
		isAutoHtml = false;
	}
	
	@Override
	public void Show() {
		// TODO Auto-generated method stub

		
		String instanceUrl = cookieAct.GetCookie("instance_url");
		String accessToken = cookieAct.GetCookie("access_token");
		echo(instanceUrl);
		echo(accessToken);
		
		ShowLead(instanceUrl, accessToken);
		super.setAjax(true);
		super.setHtml("{}");
	}
	
	
	public static void ShowLead(String instanceUrl, String accessToken) {
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet();

		// add key and value
		httpGet.addHeader("Authorization", "OAuth " + accessToken);

		try {

			URIBuilder builder = new URIBuilder(instanceUrl
					+ "/services/data/v20.0/query/");
			builder.setParameter("q", "SELECT Name, Id from Account LIMIT 100");

			httpGet.setURI(builder.build());
			System.out.println(httpGet.getURI());
			CloseableHttpResponse closeableresponse;
			try {
				closeableresponse = httpclient.execute(httpGet);
				System.out.println("Response Status line :"
						+ closeableresponse.getStatusLine());
				System.out.println(getBody(closeableresponse.getEntity()
						.getContent()));
				if (closeableresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					// Now lets use the standard java json classes to work with
					// the results

					// Do the needful with entity.
					HttpEntity entity = closeableresponse.getEntity();
					InputStream rstream = entity.getContent();
					System.out.println(rstream);
					// JSONObject authResponse = new JSONObject(new
					// JSONTokener(rstream));
					//
					// System.out.println("Query response: " +
					// authResponse.toString(2));
					//
					// writer.write(authResponse.getInt("totalSize") +
					// " record(s) returned\n\n");
					//
					// JSONArray results = authResponse.getJSONArray("records");
					//
					// for (int i = 0; i < results.length(); i++) {
					// writer.write(results.getJSONObject(i).getString("Id")
					// + ", "
					// + results.getJSONObject(i).getString("Name")
					// + "\n");
					// }
					//
					// writer.write("\n");

				}
			} catch (ClientProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} catch (URISyntaxException e1) {
			// TODO Auto­generated catch block
			e1.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static String getBody(InputStream inputStream) {
		String result = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				result += inputLine;
				result += "\n";
			}
			in.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return result;
	}
}
