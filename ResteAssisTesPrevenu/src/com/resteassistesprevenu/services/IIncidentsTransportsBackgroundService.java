package com.resteassistesprevenu.services;

import com.resteassistesprevenu.model.IncidentAction;
import com.resteassistesprevenu.model.LigneModel;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetFavorisListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetLignesListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetTypeLignesListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceReportNewIncidentListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceVoteIncidentListener;

public interface IIncidentsTransportsBackgroundService {	
	/**
	 * Récupération des incidents, asynchrone
	 */
	public void startGetIncidentsAsync(String scope);
	
	public void addGetIncidentsListener(IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener);
	public void removeGetIncidentsListener(IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener);
	
	
	/**
	 * Récupération des type de lignes, asynchrone
	 */
	public void startGetTypeLignesAsync();	

	public void addGetTypeLignesListener(IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener);
	public void removeGetTypeLignesListener(IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener);
	
	
	/**
	 * Récupération des lignes, asynchrone
	 */
	public void startGetLignesAsync(String typeLigne);
	
	public void addGetLignesListener(IIncidentsTransportsBackgroundServiceGetLignesListener listener);
	public void removeGetLignesListener(IIncidentsTransportsBackgroundServiceGetLignesListener listener);
	
	/**
	 * Création d'un incident, asynchrone
	 * @param typeLigne
	 * @param numLigne
	 * @param raison
	 */
	public void startReportIncident(String typeLigne,String numLigne, String raison);
	
	public void addReportNewIncidentListener(IIncidentsTransportsBackgroundServiceReportNewIncidentListener listener);
	public void removeReportNewIncidentListener(IIncidentsTransportsBackgroundServiceReportNewIncidentListener listener);
	
	/**
	 * Vote pour un incident, asynchrone
	 * @param incidentId
	 * @param action "plus","minus","end"
	 */
	public void startVoteIncident(int incidentId, IncidentAction action);
	
	public void addVoteIncidentListener(IIncidentsTransportsBackgroundServiceVoteIncidentListener listener);
	public void removeVoteIncidentListener(IIncidentsTransportsBackgroundServiceVoteIncidentListener listener);
	
	/** 
	 * Enregistrement d'un favoris, asynchrone.
	 * @return
	 */
	public void startRegisterFavoris(LigneModel ligne);
	
	/** 
	 * Lecture des favoris, asynchrone.
	 * @return
	 */
	/**
	 * Récupération des favoris, asynchrone
	 */
	public void startGetFavorisAsync();
	
	public void addGetFavorisListener(IIncidentsTransportsBackgroundServiceGetFavorisListener listener);
	public void removeGetFavorisListener(IIncidentsTransportsBackgroundServiceGetFavorisListener listener);

	
	public boolean isProduction();
	
	public void setProduction(boolean isProduction);
}
