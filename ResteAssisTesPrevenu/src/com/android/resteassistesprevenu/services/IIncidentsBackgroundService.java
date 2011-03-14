package com.android.resteassistesprevenu.services;

import java.util.List;

import com.android.resteassistesprevenu.model.IncidentModel;

public interface IIncidentsBackgroundService {
	 public void addListener(IIncidentsBackgroundServiceListener listener); 
	 public void removeListener(IIncidentsBackgroundServiceListener listener);
	 
	 public List<IncidentModel> getIncidentsEnCours();
}
