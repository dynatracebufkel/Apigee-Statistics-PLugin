package com.apigee.beans;

import java.util.ArrayList;
import java.util.List;
public class Environments {
	private List<Environment> environments = new ArrayList<Environment>();
	private MetaData metaData;
	public List<Environment> getEnvironments() {
		return environments;
		}
	public void setEnvironments(List<Environment> environments) {
		this.environments = environments;
		}
	public MetaData getMetaData() {
		return metaData;
		}
	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
		}
}

