// Adapter handle IO HTTP request

package com.books;

//	import bib 
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

// adapter for handle Http request 

public class Adapter {

	//	set variables
	static String response = null;
	public final static int GET = 1;
	public final static int POST = 2;

	//	call adapter
	public Adapter() {
	}

	 // call service and method for URL/HTTP request
	public String makeServiceCall(String url, int method) {
		return this.makeServiceCall(url, method, null);
	}

	 // call service to add method, params for HTTP request and url request
	public String makeServiceCall(String url, int method,
			List<NameValuePair> params) {
		try {
			//	initialize HTTP Client 
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpEntity httpEntity = null;
			HttpResponse httpResponse = null;

			// only method post and get 

			//	check HTTP request method 
//			if (method == POST) {
//				HttpPost httpPost = new HttpPost(url);
//
//				//	add post params 
//				if (params != null) {
//					httpPost.setEntity(new UrlEncodedFormEntity(params));
//				}
//
//				httpResponse = httpClient.execute(httpPost);
//
//			} else 
				if (method == GET) {
				//	add params to URL 
				if (params != null) {
					String paramString = URLEncodedUtils.format(params, "utf-8");
					url += "?" + paramString;
				}

				HttpGet httpGet = new HttpGet(url);
				httpResponse = httpClient.execute(httpGet);
			}

			httpEntity = httpResponse.getEntity();
			response = EntityUtils.toString(httpEntity);

		//	catch mistakes
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//	return Http Link 
		return response;
	}
	
	
	//handle input stream for download bitmap
	public InputStream OpenHttpConnection(String strURL)
			throws IOException {
		InputStream inputStream = null;
		URL url = new URL(strURL);
		URLConnection conn = url.openConnection();

		try {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setRequestMethod("GET");
			httpConn.connect();

			if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				inputStream = httpConn.getInputStream();
			}
		} catch (Exception ex) {
		}
		return inputStream;
	}
}
