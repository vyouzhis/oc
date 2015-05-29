package org.ppl.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class cUrl {
	private List<NameValuePair> params = new ArrayList<NameValuePair>();

	public static String httpGet(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpGet httpget = new HttpGet(url);

		// Create a custom response handler
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {

			public String handleResponse(final HttpResponse response)
					throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException(
							"Unexpected response status: " + status);
				}
			}

		};
		String responseBody = null;
		try {
			responseBody = httpclient.execute(httpget, responseHandler);

			httpclient.close();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return responseBody;
	}

	public String httpPost(String url) {
		CloseableHttpClient httpclient = HttpClients.custom()
				.addInterceptorFirst(new HttpRequestInterceptor() {

					public void process(final HttpRequest request,
							final HttpContext context) throws HttpException,
							IOException {
						if (!request.containsHeader("Accept-Encoding")) {
							request.addHeader("Accept-Encoding", "gzip");
						}

					}
				}).addInterceptorFirst(new HttpResponseInterceptor() {

					public void process(final HttpResponse response,
							final HttpContext context) throws HttpException,
							IOException {
						HttpEntity entity = response.getEntity();
						if (entity != null) {
							Header ceheader = entity.getContentEncoding();
							if (ceheader != null) {
								HeaderElement[] codecs = ceheader.getElements();
								for (int i = 0; i < codecs.length; i++) {
									if (codecs[i].getName().equalsIgnoreCase(
											"gzip")) {
										response.setEntity(new GzipDecompressingEntity(
												response.getEntity()));
										return;
									}
								}
							}
						}
					}

				}).build();

		HttpPost httppost = new HttpPost(url);

		if (params.size() > 0) {
			try {
				httppost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		CloseableHttpResponse response;
		ByteArrayOutputStream bao = null;
		InputStream bis = null;
		byte[] buf = new byte[10240];

		String content = null;
		try {

			response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				bis = response.getEntity().getContent();

				//Header[] gzip = response.getHeaders("Content-Encoding");
				Header encoding = response.getEntity().getContentEncoding();

				bao = new ByteArrayOutputStream();
				int count;
				while ((count = bis.read(buf)) != -1) {
					bao.write(buf, 0, count);

				}
				bis.close();
				
				ByteArrayInputStream bai = new ByteArrayInputStream(
						bao.toByteArray());
				if (encoding != null) {
					if (encoding.getValue().equals("gzip")
							|| encoding.getValue().equals("zip")
							|| encoding.getValue().equals(
									"application/x-gzip-compressed")) {

						GZIPInputStream gzin = new GZIPInputStream(bai);
						StringBuffer sb = new StringBuffer();
						int size;
						while ((size = gzin.read(buf)) != -1) {
							sb.append(new String(buf, 0, size, "utf-8"));
						}
						gzin.close();
						bao.close();

						content = sb.toString();
					}
				} else {

					content = bao.toString();
				}

			}

			httpclient.close();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e) {
				}
			}
		}
		return content;

	}

	public void addParams(String key, String val) {
		params.add(new BasicNameValuePair(key, val));
	}
}
