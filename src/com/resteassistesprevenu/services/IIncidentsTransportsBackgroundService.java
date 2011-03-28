package com.resteassistesprevenu.services;

import com.resteassistesprevenu.model.IncidentAction;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetLignesListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetTypeLignesListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceReportNewIncidentListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceVoteIncidentListener;

public interface IIncidentsTransportsBackgroundService {	
	public void addGetIncidentsListener(IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener);
	public void removeGetIncidentsListener(IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener);
	
	public void addGetTypeLignesListener(IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener);
	public void removeGetTypeLignesListener(IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener);
	
	public void addGetLignesListener(IIncidentsTransportsBackgroundServiceGetLignesListener listener);
	public void removeGetLignesListener(IIncidentsTransportsBackgroundServiceGetLignesListener listener);
	
	public void addReportNewIncidentListener(IIncidentsTransportsBackgroundServiceReportNewIncidentListener listener);
	public void removeReportNewIncidentListener(IIncidentsTransportsBackgroundServiceReportNewIncidentListener listener);
	
	public void addVoteIncidentListener(IIncidentsTransportsBackgroundServiceVoteIncidentListener listener);
	public void removeVoteIncidentListener(IIncidentsTransportsBackgroundServiceVoteIncidentListener listener);

	/**
	 * Recherche des incidents, asynchrone
	 */
	public void startGetIncidentsAsync(String scope);
	
	/**
	 * Récupération des type de lignes, asynchrone
	 */
	public void startGetTypeLignesAsync();
	
	/**
	 * Récupération des lignes, asynchrone
	 */
	public void startGetLignesAsync(String typeLigne);
	
	/**
	 * Création d'un incident, asynchrone
	 * @param typeLigne
	 * @param numLigne
	 * @param raison
	 */
	public void startReportIncident(String typeLigne,String numLigne, String raison);
	
	/**
	 * Vote pour un incident, asynchrone
	 * @param incidentId
	 * @param action "plus","minus","end"
	 */
	public void startVoteIncident(int incidentId, IncidentAction action);
	
	public boolean isProduction();
	
	public void setProduction(boolean isProduction);
}
