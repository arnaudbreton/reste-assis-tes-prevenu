package com.android.resteassistesprevenu.services;

import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetLignesListener;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetTypeLignesListener;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceReportNewIncidentListener;

public interface IIncidentsTransportsBackgroundService {	
	public void addGetIncidentsListener(IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener);
	public void removeGetIncidentsListener(IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener);
	
	public void addGetTypeLignesListener(IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener);
	public void removeGetTypeLignesListener(IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener);
	
	public void addGetLignesListener(IIncidentsTransportsBackgroundServiceGetLignesListener listener);
	public void removeGetLignesListener(IIncidentsTransportsBackgroundServiceGetLignesListener listener);
	
	public void addReportNewIncidentListener(IIncidentsTransportsBackgroundServiceReportNewIncidentListener listener);
	public void removeReportNewIncidentListener(IIncidentsTransportsBackgroundServiceReportNewIncidentListener listener);

	/**
	 * Recherche des incidents, asynchrone
	 */
	public void startGetIncidentsAsync(String scope);
	
	/**
	 * R�cup�ration des type de lignes, asynchrone
	 */
	public void startGetTypeLignesAsync();
	
	/**
	 * R�cup�ration des lignes, asynchrone
	 */
	public void startGetLignesAsync(String typeLigne);
	
	public void startReportIncident(String typeLigne,String numLigne, String raison);
	
	public boolean isProduction();
	
	public void setProduction(boolean isProduction);
}
