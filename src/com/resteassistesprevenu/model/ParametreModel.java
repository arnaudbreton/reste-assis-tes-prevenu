package com.resteassistesprevenu.model;


public class ParametreModel {
	private String key;
	private String value;
	
	public ParametreModel(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}	
}
