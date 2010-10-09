package com.rubikaz.cisco.tuenti;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.rubikaz.cisco.util.MD5;

public class Tuenti {
	public Tuenti() {
	}

	public Tuenti(int user_id, String session_id) {
		this.user_id = user_id;
		this.session_id = session_id;
	}

	public void login(String email, String password) throws TuentiException {
		try {
			JSONArray challenge = getChallenge();
			JSONObject challenge0 = challenge.getJSONObject(0);
			String appkey = "MDI3MDFmZjU4MGExNWM0YmEyYjA5MzRkODlmMjg0MTU6MC4xMzk0ODYwMCAxMjYxMDYwNjk2";
			String passcode = md5(challenge0.getString("challenge")
					+ md5(password));
			JSONArray session = getSession(email, passcode, appkey, challenge0
					.getString("timestamp"), challenge0.getString("seed"));
			JSONObject session0 = session.getJSONObject(0);
			if (session0.has("error")) {
				throw new TuentiException(session0.getString("message"));
			} else {
				user_id = session0.getInt("user_id");
				session_id = session0.getString("session_id");
			}
		} catch (JSONException jsone) {
			throw new TuentiException("Unexpected error", jsone);
		} catch (IOException ioe) {
			throw new TuentiException("Unexpected error", ioe);
		}
	}

	public void addPostToProfileWall(int user_id, String body)
			throws TuentiException {
		if (!isAuthenticated())
			throw new TuentiException("Please log in!");
		String request = "{\"requests\":[[\"addPostToProfileWall\",{\"user_id\":"
				+ String.valueOf(user_id)
				+ ", \"body\":\""
				+ body
				+ "\"}]], \"session_id\":\""
				+ session_id
				+ "\", \"version\":\"0.4\"}";
		String response;
		try {
			response = http(request);
		} catch (IOException ioe) {
			throw new TuentiException("Unexpected error", ioe);
		}
		try {
			JSONArray jsonres = new JSONArray(response);
			if (!jsonres.optBoolean(0, false)) {
				throw new TuentiException(
						"addPostToProfileWall failed. Check permissions. Response was:\n"
								+ response);
			}
			JSONObject jsonres1 = jsonres.optJSONObject(1);
			if (jsonres1 != null) {
				user_id = jsonres1.getInt("user_id");
				session_id = jsonres1.getString("session_id");
			}
		} catch (JSONException jsone) {
			throw new TuentiException("Unexpected error", jsone);
		}
	}

	public boolean isAuthenticated() {
		return (session_id != null) && (user_id > 0);
	}

	public int getUserID() {
		return user_id;
	}

	public String getSessionID() {
		return session_id;
	}

	private JSONArray getChallenge() throws JSONException, IOException {
		String request = "{\"requests\":[[\"getChallenge\",{\"type\":\"login\"}]],\"version\":\"0.4\"}";
		String response = http(request);
		return string2json(response);
	}

	private JSONArray getSession(String email, String passcode, String appkey,
			String timestamp, String seed) throws JSONException, IOException {
		String request = "{\"requests\":[[\"getSession\",{\"email\":\"" + email
				+ "\", \"passcode\":\"" + passcode
				+ "\", \"application_key\":\"" + appkey
				+ "\", \"timestamp\":\"" + timestamp + "\", \"seed\":\"" + seed
				+ "\"}]],\"version\":\"0.4\"}";
		String response = http(request);
		return string2json(response);
	}

	private String http(String json) throws IOException {
		URL url = new URL("http://api.tuenti.com/api/");
		URLConnection conn = url.openConnection();

		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(json);
		wr.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(conn
				.getInputStream()));
		String inputLine;

		StringBuffer res = new StringBuffer("");
		String nl = "";
		while ((inputLine = in.readLine()) != null) {
			res.append(nl + inputLine);
			nl = "\n";
		}

		in.close();

		return res.toString();
	}

	private JSONArray string2json(String s) throws JSONException {
		return new JSONArray(s);
	}

	private String md5(String s) {
		String md5;
		try {
			md5 = MD5.getInstance().hashData(s.getBytes());
		} catch (java.security.NoSuchAlgorithmException nsae) {
			// nsae.printStackTrace();
			md5 = null;
		}
		return md5;
	}

	private int user_id = -1;
	private String session_id = null;
}
