package com.apigee;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import com.apigee.beans.Environments;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ApiService {
	private static final Logger log = Logger.getLogger(StatService.class.getName());

	/**
	 * External facing method to return the result from apigee. Parses return
	 * from json to an collection.
	 * 
	 * @param url
	 *            - apigee api url
	 * @param authorization
	 *            - base64 string of uid and pwd
	 * @return - result value in the form of a double
	 * @throws IOException
	 */
	public Double getResult(String url, String authorization) throws IOException {
		HttpURLConnection conn = null;
		double result = 0;
		Environments env = null;
		BufferedReader rd = null;
		try {
			conn = getConnected(url, authorization);
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			int value = 0;
			StringBuilder sb = new StringBuilder();

			while ((value = rd.read()) != -1) {
				char c = (char) value;
				sb.append(c);
			}
			rd.close();
			log.info("Return is: " + sb.toString());
			env = new Gson().fromJson(sb.toString(), Environments.class);
			result = getResults(env);
		} catch (MalformedURLException e) {
			log.severe("Malformed URL ERROR in getResults method: " + e.getMessage() + " Call Stack: " + e.getStackTrace());
		} catch (JsonSyntaxException e) {
			log.severe("JSON ERROR in getResults method: " + e.getMessage() + " Call Stack: " + e.getStackTrace());
		} catch (Exception e) {
			log.severe("ERROR in getResults method: " + e.getMessage() + " Call Stack: " + e.getStackTrace());
		} finally {
			if (rd != null) {
				try {
					rd.close();
				} catch (IOException e) {
					log.info("ERROR closing Reader in getmonthlyCount method. Exception Message: " + e);
				}
			}
			if (conn != null) {
				conn.disconnect();
			}
		}

		return result;
	}

	/**
	 * setup of the connection to apigee
	 * 
	 * @param url
	 *            - to api
	 * @param authorization
	 *            - uid and pwd as base64
	 * @return - httpConnection to apigee api
	 * @throws MalformedURLException
	 */
	private HttpURLConnection getConnected(String url, String authorization) throws MalformedURLException {
		URL apigeeURL = new URL(url);
		HttpURLConnection result = null;
		try {
			result = (HttpURLConnection) apigeeURL.openConnection();
			result.setRequestMethod("GET");
			result.setRequestProperty("Authorization", "Basic " + authorization);
		} catch (IOException e) {
			log.severe("ERROR in getConnected method. Exception Message: " + e);
		}
		return result;
	}

	/**
	 * Based on return from api this method will retrieve the first value in the
	 * list of metric values.
	 * 
	 * @param env
	 *            - environment that was returned from apigee JSON results
	 * @return - value of result as a double
	 */
	private double getResults(Environments env) {
		log.info("getResults started...");
		double result = 0;
		try {
			log.info("Environments Size: " + Integer.toString(env.getEnvironments().size()));
			log.info("Metrics Size: " + Integer.toString(env.getEnvironments().get(0).getMetrics().size()));
			if (env.getEnvironments().get(0).getMetrics().size() == 0) {
				log.info("getting metrics from dimensions");
				log.info("Dimension Size: " + Integer.toString(env.getEnvironments().get(0).getDimensions().size()));
				result = Double.parseDouble(
						env.getEnvironments().get(0).getDimensions().get(0).getMetrics().get(0).getValues().get(0));
			} else {
				log.info("getting metrics from environment");
				result = Double.parseDouble(env.getEnvironments().get(0).getMetrics().get(0).getValues().get(0));
			}
			log.info("count: " + result);
			log.info("getResults ended...");
		} catch (Exception e) {
			log.severe(e.getMessage());
		}
		return result;
	}
}
