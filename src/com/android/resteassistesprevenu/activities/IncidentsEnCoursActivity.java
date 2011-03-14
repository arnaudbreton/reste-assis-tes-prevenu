package com.android.resteassistesprevenu.activities;

import java.util.ArrayList;

import android.app.ListActivity;
import android.os.Bundle;

import com.android.resteassistesprevenu.model.IncidentModel;

public class IncidentsEnCoursActivity extends ListActivity {
	
	private ArrayList<IncidentModel> incidents;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.incidents = new ArrayList<IncidentModel>();
	}
	
	/**
	 * @return the incidents
	 */
	public ArrayList<IncidentModel> getIncidents() {
		return incidents;
	}

	/**
	 * @param incidents the incidents to set
	 */
	public void setIncidents(ArrayList<IncidentModel> incidents) {
		this.incidents.clear();		
		this.incidents.addAll(incidents);
	}
}