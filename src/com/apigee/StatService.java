
/**
 * This template file was generated by Dynatrace client.
 * The Dynatrace community portal can be found here: http://community.dynatrace.com/
 * For information how to publish a plugin please visit https://community.dynatrace.com/community/display/DL/How+to+add+a+new+plugin/
 **/

package com.apigee;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;

import com.dynatrace.diagnostics.pdk.Monitor;
import com.dynatrace.diagnostics.pdk.MonitorEnvironment;
import com.dynatrace.diagnostics.pdk.MonitorMeasure;
import com.dynatrace.diagnostics.pdk.Plugin;
import com.dynatrace.diagnostics.pdk.Status;
import com.dynatrace.diagnostics.pdk.TaskEnvironment;

import sun.misc.BASE64Encoder;

public class StatService implements Monitor {
	private static final String DATE_FORMAT = "MM/dd/yyyy%20HH:mm";
	private static final String TIME_RANGE = "&timeRange=";
	private String url = null;
	private String userInfo = null;
	private String auth = null;

	private static final Logger log = Logger.getLogger(StatService.class.getName());

	/**
	 * Initializes the Plugin. This method is called in the following cases:
	 * <ul>
	 * <li>before <tt>execute</tt> is called the first time for this scheduled
	 * Plugin</li>
	 * <li>before the next <tt>execute</tt> if <tt>teardown</tt> was called
	 * after the last execution</li>
	 * </ul>
	 * <p>
	 * If the returned status is <tt>null</tt> or the status code is a
	 * non-success code then {@link Plugin#teardown() teardown()} will be called
	 * next.
	 * <p>
	 * Resources like sockets or files can be opened in this method.
	 * 
	 * @param env
	 *            the configured <tt>MonitorEnvironment</tt> for this Plugin;
	 *            contains subscribed measures, but <b>measurements will be
	 *            discarded</b>
	 * @see Plugin#teardown()
	 * @return a <tt>Status</tt> object that describes the result of the method
	 *         call
	 */
	@Override
	public Status setup(MonitorEnvironment env) throws Exception {
		log.info("start setup");
		url = setupApiUrl(env);
		log.info("url: " + url);
		String username = env.getConfigString("uid");
		String password = env.getConfigPassword("pwd");
		log.info("username: " + username);
		userInfo = username + ":" + password;
		auth = new BASE64Encoder().encode(userInfo.getBytes());
		log.info("setup finished successfully");
		return new Status(Status.StatusCode.Success);
	}

	/**
	 * combines the url and the time range from the drop down list in the plugin
	 * properties
	 * 
	 * @param env
	 *            - MonitorEnvironment obj to use
	 * @return - array for urls in a string format with the new time range added
	 */
	private String setupApiUrl(MonitorEnvironment env) {
		String results = null;
		String timerange = env.getConfigString("timerange");
		results = addTimeRangeToUrl(env.getConfigString("query"), getTimeRange(timerange));
		return results;
	}

	/**
	 * urls may come in with timeRanges already applied, this removes the
	 * timeRange in the url and replaces it with the one based on the time range
	 * dropdown.
	 * 
	 * @param url
	 *            - api url
	 * @param timeRange
	 *            - the time range from drop down list
	 * @return - formatted url with appropreate time range
	 */
	private String addTimeRangeToUrl(String url, String timeRange) {
		String result = null;
		if (!url.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			if (url.contains("&timeRange")) {
				sb.append(url.split("&")[0]);
			} else {
				sb.append(url);
			}
			sb.append(timeRange);
			log.info("sb: " + sb.toString());
			result = sb.toString();
		}
		return result;
	}

	/**
	 * returns a formatted start and end date range for the apigee api
	 * 
	 * @param duration
	 *            - timerange value picked by the user in the properties of the
	 *            plugin
	 * @return - string formatted date range for apigee api
	 * 
	 *         if there is an exception it will return a start and end date that
	 *         are the same so the url doesn't fail with nothing in the
	 *         timeRange.
	 */
	private String getTimeRange(String duration) {
		log.info("starting TimeRange");
		String result = null;
		// end date which is always today
		Date currentDate = new Date();
		// the date prior to today you wish to start at
		Date startDate = null;
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(currentDate);
			int range = calculateTimeRange(duration);
			log.info("Range: " + range);
			c.add(range, -1);
			startDate = c.getTime();
			log.info("startDate: " + startDate.toString());
			log.info("currentDate: " + currentDate.toString());
			SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
			// formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			// startDate = formatter.parse(startDate.toString());
			// currentDate = formatter.parse(currentDate.toString());
			String currentDateFormatted = formatter.format(currentDate);
			String startDateFormatted = formatter.format(startDate);
			log.info("startDateFormatted: " + startDateFormatted.toString());
			log.info("currentDateFormatted: " + currentDateFormatted.toString());
			result = TimeRangeToString(startDateFormatted, currentDateFormatted);
		} catch (Exception e) {
			log.severe(
					"An Exception has ocurred in the getTimeRange method, the begin and end date should be the same now. Message is: "
							+ e.getMessage() + " call Stack: " + e.getStackTrace());
			String startDateException = new SimpleDateFormat(DATE_FORMAT).format(Calendar.getInstance().getTime());
			String endtDateException = new SimpleDateFormat(DATE_FORMAT).format(Calendar.getInstance().getTime());
			return TimeRangeToString(startDateException, endtDateException);
		} finally {

		}

		log.info("finishing TimeRange");
		return result;
	}

	/**
	 * determines the value of the Calendar range option, minute, hour, day,
	 * month
	 * 
	 * @param value
	 *            - duration value choosen from the timerange list in the
	 *            properties of the plugin
	 * @return - range value to be used in determining the end date for range
	 */
	private int calculateTimeRange(String value) {
		log.info("starting calc range");
		int result = 0;
		switch (value) {
		case "1 min":
			result = Calendar.MINUTE;
			break;
		case "1 hour":
			result = Calendar.HOUR_OF_DAY;
			break;
		case "1 day":
			result = Calendar.DATE;
			break;
		case "1 month":
			result = Calendar.MONTH;
			break;
		default:
			result = Calendar.MINUTE;
		}
		log.info("finishing calc range");
		return result;
	}

	/**
	 * Returns a string formatted date for the apigee timeRange parameter
	 * 
	 * @param startDateTime
	 *            - start date for range
	 * @param endDateTime
	 *            - end data for range
	 * @return formatted string for timeRange parameter
	 */
	private String TimeRangeToString(String startDateTime, String endDateTime) {
		String result = TIME_RANGE + startDateTime + "~" + endDateTime;
		return result;
	}

	/**
	 * Executes the Monitor Plugin to retrieve subscribed measures and store
	 * measurements.
	 *
	 * <p>
	 * This method is called at the scheduled intervals. If the Plugin execution
	 * takes longer than the schedule interval, subsequent calls to
	 * {@link #execute(MonitorEnvironment)} will be skipped until this method
	 * returns. After the execution duration exceeds the schedule timeout,
	 * {@link TaskEnvironment#isStopped()} will return <tt>true</tt>. In this
	 * case execution should be stopped as soon as possible. If the Plugin
	 * ignores {@link TaskEnvironment#isStopped()} or fails to stop execution in
	 * a reasonable timeframe, the execution thread will be stopped ungracefully
	 * which might lead to resource leaks!
	 *
	 * @param env
	 *            a <tt>MonitorEnvironment</tt> object that contains the Plugin
	 *            configuration and subscribed measures. These
	 *            <tt>MonitorMeasure</tt>s can be used to store measurements.
	 * @return a <tt>Status</tt> object that describes the result of the method
	 *         call
	 */
	@Override
	public Status execute(MonitorEnvironment env) throws Exception {
		ApiService apigee = null;
		double result = 0.0;
		log.info("Execute method Started");
		try {

			apigee = new ApiService();
			Collection<MonitorMeasure> monitorMeasures = env.getMonitorMeasures("ResultsGroup", "Result");
			if (isMonitorAvailable(monitorMeasures, "Result")) {
				// monthly hits
				result = apigee.getResult(url, auth);
				for (MonitorMeasure subscribedMonitorMeasure : monitorMeasures) {
					log.info("Result is: " + result + " measurement value: " + subscribedMonitorMeasure.getMetricName()
							+ "Monitor Count: " + monitorMeasures.size());
					subscribedMonitorMeasure.setValue(result);
				}
			}
		} catch (Exception e) {
			log.severe("ERROR: An Exception in EXECUTE method, Message is: " + e.getMessage() + " Call Stack: "
					+ e.getStackTrace());
			return new Status(Status.StatusCode.ErrorInternalException);
		} finally {
			if (apigee != null) {
				apigee = null;
			}
			log.info("Execute Method Ended");
		}

		return new Status(Status.StatusCode.Success);
	}

	/**
	 * Validates that the monitor being asked for is available for usage, it
	 * assumes it is and tests if it is not.
	 * 
	 * @param monitorMeasures
	 *            - collection to be checked
	 * @param measurementName
	 *            - name of measurement that is getting set
	 * @return
	 */
	private boolean isMonitorAvailable(Collection<MonitorMeasure> monitorMeasures, String measurementName) {
		boolean result = true;
		if (monitorMeasures == null) {
			result = false;
			log.info("Null Measurement: measurement " + measurementName
					+ " was not available. Measurement: apigeehitcount");
		}
		return result;
	}

	/**
	 * Shuts the Plugin down and frees resources. This method is called in the
	 * following cases:
	 * <ul>
	 * <li>the <tt>setup</tt> method failed</li>
	 * <li>the Plugin configuration has changed</li>
	 * <li>the execution duration of the Plugin exceeded the schedule timeout
	 * </li>
	 * <li>the schedule associated with this Plugin was removed</li>
	 * </ul>
	 *
	 * <p>
	 * The Plugin methods <tt>setup</tt>, <tt>execute</tt> and <tt>teardown</tt>
	 * are called on different threads, but they are called sequentially. This
	 * means that the execution of these methods does not overlap, they are
	 * executed one after the other.
	 *
	 * <p>
	 * Examples:
	 * <ul>
	 * <li><tt>setup</tt> (failed) -&gt; <tt>teardown</tt></li>
	 * <li><tt>execute</tt> starts, configuration changes, <tt>execute</tt> ends
	 * -&gt; <tt>teardown</tt><br>
	 * on next schedule interval: <tt>setup</tt> -&gt; <tt>execute</tt> ...</li>
	 * <li><tt>execute</tt> starts, execution duration timeout, <tt>execute</tt>
	 * stops -&gt; <tt>teardown</tt></li>
	 * <li><tt>execute</tt> starts, <tt>execute</tt> ends, schedule is removed
	 * -&gt; <tt>teardown</tt></li>
	 * </ul>
	 * Failed means that either an unhandled exception is thrown or the status
	 * returned by the method contains a non-success code.
	 *
	 *
	 * <p>
	 * All by the Plugin allocated resources should be freed in this method.
	 * Examples are opened sockets or files.
	 *
	 * @see Monitor#setup(MonitorEnvironment)
	 */
	@Override
	public void teardown(MonitorEnvironment env) throws Exception {
		url = null;
		userInfo = null;
		auth = null;
	}
}
