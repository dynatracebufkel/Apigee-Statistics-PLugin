package com.apigee.beans;

import java.util.ArrayList;
import java.util.List;

public class MetaData {
	private List<Object> errors = new ArrayList<Object>();
	private List<String> notices = new ArrayList<String>();

	public List<Object> getErrors() {
		return errors;
	}

	public void setErrors(List<Object> errors) {
		this.errors = errors;
	}

	public List<String> getNotices() {
		return notices;
	}

	public void setNotices(List<String> notices) {
		this.notices = notices;
	}

}
