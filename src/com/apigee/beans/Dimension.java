package com.apigee.beans;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Dimension {

	@SerializedName("metrics")
	@Expose
	private List<Metric> metrics = new ArrayList<Metric>();
	@SerializedName("name")
	@Expose
	private String name;

	/**
	 * 
	 * @return The metrics
	 */
	public List<Metric> getMetrics() {
		return metrics;
	}

	/**
	 * 
	 * @param metrics
	 *            The metrics
	 */
	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}

	/**
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            The name
	 */
	public void setName(String name) {
		this.name = name;
	}
}
