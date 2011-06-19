package com.resteassistesprevenu.services;

import com.resteassistesprevenu.model.IncidentAction;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.model.LigneModel;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceFavorisModifiedListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetFavorisListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetLignesListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetParametrageListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetTypeLignesListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceReportNewIncidentListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceVoteIncidentListener;

/**
 * Interface du service
 * @author Arnaud
 *
 */
public interface IIncidentsTransportsBackgroundService {	
	/**
	 * R�cup�ration des incidents, asynchrone
	 */
	public void startGetIncidentsAsync(String scope, boolean forceUpdate, IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener callback);
	
	/**
	 * R�cup�ration des type de lignes, asynchrone
	 */
	public void startGetTypeLignesAsync(IIncidentsTransportsBackgroundServiceGetTypeLignesListener callback);	

	/**
	 * R�cup�ration des lignes, asynchrone
	 */
	public void startGetLignesAsync(String typeLigne, IIncidentsTransportsBackgroundServiceGetLignesListener callback);

	/**
	 * Cr�ation d'un incident, asynchrone
	 * @param typeLigne
	 * @param numLigne
	 * @param raison
	 */
	public void startReportIncident(String typeLigne,String numLigne, String raison, IIncidentsTransportsBackgroundServiceReportNewIncidentListener callback);
	
	/**
	 * Vote pour un incident, asynchrone
	 * @param incident L'incident sur lequel on souhaite voter
	 * @param action "plus","minus","end"
	 */
	public void startVoteIncident(IncidentModel incident, IncidentAction action, IIncidentsTransportsBackgroundServiceVoteIncidentListener callback);
	
	/** 
	 * Enregistrement d'un favoris, asynchrone.
	 */
	public void startRegisterFavoris(LigneModel ligne);
	
	/**
	 * Ajout d'un listener pour pr�venir des modifications de favoris
	 * @param listener
	 */
	public void addFavorisModifiedListener(IIncidentsTransportsBackgroundServiceFavorisModifiedListener listener);
	
	/**
	 * Suppression d'un listener pour pr�venir des modifications de favoris
	 * @param listener
	 */
	public void removeFavorisModifiedListener(IIncidentsTransportsBackgroundServiceFavorisModifiedListener listener);
	
	/** 
	 * Lecture des favoris, asynchrone.
	 */
	public void startGetFavorisAsync(IIncidentsTransportsBackgroundServiceGetFavorisListener callback);
	
	/**
	 * Lecture d'un param�tre, asynchrone
	 */
	public void startGetParametreAsync(IIncidentsTransportsBackgroundServiceGetParametrageListener callback);

	/**
	 * Indique si le service pointe sur la production
	 * @return
	 */
	public boolean isProduction();
	
	/**
	 * Indique au service s'il doit pointer sur la production
	 * @param isProduction
	 */
	public void setProduction(boolean isProduction);
	
	/**
	 * Retourne l'URL sur lequel pointe courrament le service
	 */
	public String getUrlService();
}
