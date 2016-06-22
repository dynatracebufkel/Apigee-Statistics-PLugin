package com.apigee.beans;

import java.util.ArrayList;
import java.util.List;

public class Environment {
	private List<Metric> metrics = new ArrayList<Metric>();
	private List<Dimension> dimensions = new ArrayList<Dimension>();
	private String name;
	
	public List<Metric> getMetrics() {
		return metrics;
	}
	
	public void setMetrics(List<Metric> metrics) {
		this.metrics = metrics;
	}
	public List<Dimension> getDimensions() {
		return dimensions;
	}
	public void setDimensions(List<Dimension> dimensions) {
		this.dimensions = dimensions;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
