package com.apigee.beans;

import java.util.ArrayList;
import java.util.List;

public class Metric {
	private String name;
	private List<String> values = new ArrayList<String>();
	public Metric(){
		
	}
	public String getEnvironmentName() {
		return name;
	}

	public void setEnvironmentName(String environmentName) {
		this.name = environmentName;
	}

	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}

}
