package com.resteassistesprevenu.model;

/**
 * Action possible sur un incident
 * @author Arnaud
 *
 */
public enum IncidentAction {
	VOTE_PLUS("plus"),
	VOTE_MINUS("minus"), 
	VOTE_END("end"), 
	SHARE("share");

	private IncidentAction(String name) {
		this.name = name;
	}

	private final String name;

	public String toString() {
		return name;
	}
}
