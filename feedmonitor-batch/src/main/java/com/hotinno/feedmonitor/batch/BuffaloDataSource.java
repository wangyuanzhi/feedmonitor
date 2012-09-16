package com.hotinno.feedmonitor.batch;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

public class BuffaloDataSource extends BasicDataSource {
	private static final Log log = LogFactory.getLog(Main.class);
	private static final String URL_PATTERN = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8";

	public BuffaloDataSource() {
		String vcap = System.getenv("VCAP_SERVICES");

		try {
			JSONObject json = new JSONObject(vcap);
			JSONObject m = json.getJSONArray("mysql-5.1").getJSONObject(0);
			JSONObject credentials = m.getJSONObject("credentials");
			String name = credentials.getString("name");
			String host = credentials.getString("host");
			int port = credentials.getInt("port");
			String username = credentials.getString("username");
			String password = credentials.getString("password");

			String url = String.format(URL_PATTERN, host, port, name);

			setUrl(url);
			setUsername(username);
			setPassword(password);

			log.error("######################################################");
			log.error("URL is: " + getUrl());
			log.error("Username is: " + getUsername());
			log.error("Password is: " + getPassword());
			log.error("######################################################");
		} catch (JSONException e) {
			log.error("Error occurred while parsing vcap: " + vcap, e);
		}
	}
}
