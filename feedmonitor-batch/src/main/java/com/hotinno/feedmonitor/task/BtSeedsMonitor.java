package com.hotinno.feedmonitor.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hotinno.feedmonitor.dao.btseed.BtSeed;
import com.hotinno.feedmonitor.dao.btseed.BtSeedDao;
import com.hotinno.feedmonitor.dao.config.Config;
import com.hotinno.feedmonitor.dao.config.ConfigDao;
import com.hotinno.feedmonitor.dao.config.Transmission;

@Component
public class BtSeedsMonitor {
	private static Log log = LogFactory.getLog(BtSeedsMonitor.class);

	private static final String HTTP = "http://";
	private static final String PORT_DELIMITER = ":";

	private static final String PATH_TO_RPC = "/transmission/rpc";
	private static final String SESSION_HEADER = "X-Transmission-Session-Id";

	private static final String RPC_ARGUMENTS = "arguments";
	private static final String RPC_TAG = "tag";
	private static final String RPC_IDS = "ids";
	private static final String RPC_DELETE_DATA = "delete-local-data";
	private static final String RPC_FILES_WANTED = "files-wanted";
	private static final String RPC_FILES_UNWANTED = "files-unwanted";
	private static final String RPC_FILES_LOW = "priority-low";
	private static final String RPC_FILES_NORMAL = "priority-normal";
	private static final String RPC_FILES_HIGH = "priority-high";

	private static final String RPC_METHOD = "method";
	private static final String RPC_METHOD_GET = "torrent-get";
	private static final String RPC_METHOD_SET = "torrent-set";
	private static final String RPC_METHOD_ADD = "torrent-add";
	private static final String RPC_METHOD_REMOVE = "torrent-remove";
	private static final String RPC_METHOD_PAUSE = "torrent-stop";
	private static final String RPC_METHOD_RESUME = "torrent-start";
	private static final String RPC_METHOD_SESSIONSET = "session-set";

	private static final String RPC_SESSION_LIMITDOWN = "speed-limit-down";
	private static final String RPC_SESSION_LIMITDOWNE = "speed-limit-down-enabled";
	private static final String RPC_SESSION_LIMITUP = "speed-limit-up";
	private static final String RPC_SESSION_LIMITUPE = "speed-limit-up-enabled";

	private static final String RPC_FIELDS = "fields";
	private static final String RPC_TORRENTS = "torrents";
	private static final String RPC_FILENAME = "filename";
	private static final String RPC_METAINFO = "metainfo";

	private static final String RPC_ID = "id";
	private static final String RPC_NAME = "name";
	private static final String RPC_STATUS = "status";
	private static final String RPC_DOWNLOADDIR = "downloadDir";

	private static final String RPC_RATEDOWNLOAD = "rateDownload";
	private static final String RPC_RATEUPLOAD = "rateUpload";
	private static final String RPC_PEERSGETTING = "peersGettingFromUs";
	private static final String RPC_PEERSSENDING = "peersSendingToUs";
	private static final String RPC_PEERSCONNECTED = "peersConnected";
	private static final String RPC_PEERSKNOWN = "peersKnown";
	private static final String RPC_ETA = "eta";

	private static final String RPC_DOWNLOADSIZE1 = "haveUnchecked";
	private static final String RPC_DOWNLOADSIZE2 = "haveValid";
	private static final String RPC_UPLOADEDEVER = "uploadedEver";
	private static final String RPC_TOTALSIZE = "sizeWhenDone";

	private static final String RPC_DATEADDED = "addedDate";
	private static final String RPC_DATEDONE = "doneDate";
	private static final String RPC_AVAILABLE = "desiredAvailable";

	private static final String RPC_FILES = "files";
	// private static final String RPC_FILE_KEY = "key";
	private static final String RPC_FILE_NAME = "name";
	private static final String RPC_FILE_LENGTH = "length";
	private static final String RPC_FILE_COMPLETED = "bytesCompleted";
	private static final String RPC_FILESTATS = "fileStats";
	private static final String RPC_FILESTAT_WANTED = "wanted";
	private static final String RPC_FILESTAT_PRIORITY = "priority";

	private static final String[] RPC_FIELDS_ARRAY = new String[] { RPC_ID,
			RPC_NAME, RPC_STATUS, RPC_DOWNLOADDIR, RPC_RATEDOWNLOAD,
			RPC_RATEUPLOAD, RPC_PEERSGETTING, RPC_PEERSSENDING,
			RPC_PEERSCONNECTED, RPC_PEERSKNOWN, RPC_ETA, RPC_DOWNLOADSIZE1,
			RPC_DOWNLOADSIZE2, RPC_UPLOADEDEVER, RPC_TOTALSIZE, RPC_DATEADDED,
			RPC_DATEDONE, RPC_AVAILABLE };

	@Autowired
	private BtSeedDao btSeedDao;

	@Autowired
	private ConfigDao configDao;

	private String username;
	private String password;
	private String fqdn;
	private int port;
	private String rpcPath;

	private void initTransimissionConfig() {
		List<Config> items = configDao
				.getConfigBySection(Transmission.PARAM_SECTION_TRANSMISSION);
		for (Config item : items) {
			if (item.getName().equals(Transmission.PARAM_NAME_FQDN)) {
				fqdn = item.getValue();
				continue;
			}
			if (item.getName().equals(Transmission.PARAM_NAME_PORT)) {
				port = (Integer.valueOf(item.getValue()));
				continue;
			}
			if (item.getName().equals(Transmission.PARAM_NAME_RPC_PATH)) {
				rpcPath = (item.getValue());
				continue;
			}
			if (item.getName().equals(Transmission.PARAM_NAME_USERNAME)) {
				username = (item.getValue());
				continue;
			}
			if (item.getName().equals(Transmission.PARAM_NAME_PASSWORD)) {
				password = item.getValue();
				continue;
			}
		}
	}

	// @Scheduled(cron = "0 */1 * * * ?")
	// @Scheduled(cron = "0 */15 * * * ?")
	/**
	 * Will be called by HeartBeatController
	 */
	public void checkSeeds() {
		initTransimissionConfig();

		List<BtSeed> seeds = btSeedDao.getAllUnProcessed();
		if (log.isDebugEnabled()) {
			log.debug(String.format("Unprocessed seeds number is: %s",
					seeds.size()));
		}
		for (BtSeed seed : seeds) {
			seed.setProcessedTime(new Timestamp(System.currentTimeMillis()));
			try {
				JSONObject result = addTorrent(seed.getMagnetUrl());

				seed.setProcessed(true);
				seed.setComment(result.getString("result"));
			} catch (Exception e) {
				seed.setComment(e.getMessage());
			}
			btSeedDao.merge(seed);
		}
	}

	/**
	 * Will be called by HeartBeatController
	 */
	public BtSeed checkSeed(long seedId) {
		initTransimissionConfig();

		BtSeed seed = btSeedDao.getById(seedId);

		try {
			JSONObject result = addTorrent(seed.getMagnetUrl());

			seed.setProcessed(true);
			seed.setProcessedTime(new Timestamp(System.currentTimeMillis()));
			seed.setComment(result.getString("result"));
		} catch (Exception e) {
			seed.setComment(e.getMessage());
		}

		btSeedDao.merge(seed);

		return seed;
	}

	private JSONObject addTorrent(String magnetUrl) {
		JSONObject request = new JSONObject();
		try {
			request.put(RPC_FILENAME, magnetUrl);

			JSONObject jsonObj = doRequest(buildRequestObject(RPC_METHOD_ADD,
					request));

			return jsonObj;
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	private JSONObject buildRequestObject(String sendMethod,
			JSONObject arguments) throws JSONException {
		// Build request for method
		JSONObject request = new JSONObject();
		request.put(RPC_METHOD, sendMethod);
		request.put(RPC_ARGUMENTS, arguments);
		request.put(RPC_TAG, 0);
		return request;
	}

	private String sessionToken;

	private JSONObject doRequest(JSONObject data) {

		try {

			// Initialize the HTTP client
			DefaultHttpClient httpclient = createStandardHttpClient();

			// Setup request using POST stream with URL and data
			HttpPost httppost = new HttpPost(buildRpcUrl());
			StringEntity se = new StringEntity(data.toString());
			httppost.setEntity(se);

			if (sessionToken != null) {
				httppost.addHeader(SESSION_HEADER, sessionToken);
			}

			// Execute
			HttpResponse response = httpclient.execute(httppost);

			// Authentication error?
			if (response.getStatusLine().getStatusCode() == 401) {
				throw new RuntimeException(
						"401 HTTP response (username or password incorrect)");
			}

			// 409 error because of a session id?
			if (response.getStatusLine().getStatusCode() == 409) {

				httpclient = createStandardHttpClient();

				// Retry post, but this time with the new session token that was
				// encapsulated in the 409 response
				sessionToken = response.getFirstHeader(SESSION_HEADER)
						.getValue();
				httppost.addHeader(SESSION_HEADER, sessionToken);
				response = httpclient.execute(httppost);

			}

			HttpEntity entity = response.getEntity();
			if (entity != null) {

				// Read JSON response
				java.io.InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				JSONObject json = new JSONObject(result);
				instream.close();

				// Return the JSON object
				return json;
			}

			throw new RuntimeException("No entity got.");
			// Any entity?
		} catch (JSONException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private DefaultHttpClient createStandardHttpClient() {
		final int connectionTimeout = 30 * 1000;

		HttpParams httpparams = new BasicHttpParams();
		HttpConnectionParams
				.setConnectionTimeout(httpparams, connectionTimeout);
		HttpConnectionParams.setSoTimeout(httpparams, connectionTimeout);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpparams);

		// Authentication credentials
		httpclient.getCredentialsProvider().setCredentials(
				new AuthScope(fqdn, port),
				new UsernamePasswordCredentials(username, password));

		return httpclient;
	}

	private String buildRpcUrl() {
		return String.format("http://%s:%s%s", fqdn, port, rpcPath);
	}

	private static String ConvertStreamToString(InputStream is, String encoding)
			throws UnsupportedEncodingException {
		InputStreamReader isr;
		if (encoding != null) {
			isr = new InputStreamReader(is, encoding);
		} else {
			isr = new InputStreamReader(is);
		}
		BufferedReader reader = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private static String convertStreamToString(InputStream is) {
		try {
			return ConvertStreamToString(is, null);
		} catch (UnsupportedEncodingException e) {
			// Since this is going to use the default encoding, it is never
			// going to crash on an UnsupportedEncodingException
			e.printStackTrace();
			return null;
		}
	}
}
