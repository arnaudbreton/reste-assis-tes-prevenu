package com.resteassistesprevenu.services;

import com.resteassistesprevenu.model.IncidentAction;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.model.LigneModel;
import com.resteassistesprevenu.services.listeners.IBackgroundServiceFavorisModifiedListener;
import com.resteassistesprevenu.services.listeners.IBackgroundServiceGetFavorisListener;
import com.resteassistesprevenu.services.listeners.IBackgroundServiceGetIncidentsEnCoursListener;
import com.resteassistesprevenu.services.listeners.IBackgroundServiceGetLignesListener;
import com.resteassistesprevenu.services.listeners.IBackgroundServiceGetParametrageListener;
import com.resteassistesprevenu.services.listeners.IBackgroundServiceGetTypeLignesListener;
import com.resteassistesprevenu.services.listeners.IBackgroundServiceRegisterParametrageListener;
import com.resteassistesprevenu.services.listeners.IBackgroundServiceReportNewIncidentListener;
import com.resteassistesprevenu.services.listeners.IBackgroundServiceVoteIncidentListener;

/**
 * Interface du service
 * @author Arnaud
 *
 */
public interface IBackgroundService {	
	/**
	 * Récupération des incidents, asynchrone
	 */
	public void startGetIncidentsAsync(String scope, boolean forceUpdate, IBackgroundServiceGetIncidentsEnCoursListener callback);
	
	/**
	 * Récupération des type de lignes, asynchrone
	 */
	public void startGetTypeLignesAsync(IBackgroundServiceGetTypeLignesListener callback);	

	/**
	 * Récupération des lignes, asynchrone
	 */
	public void startGetLignesAsync(String typeLigne, IBackgroundServiceGetLignesListener callback);

	/**
	 * Création d'un incident, asynchrone
	 * @param typeLigne
	 * @param numLigne
	 * @param raison
	 */
	public void startReportIncident(String typeLigne,String numLigne, String raison, IBackgroundServiceReportNewIncidentListener callback);
	
	/**
	 * Vote pour un incident, asynchrone
	 * @param incident L'incident sur lequel on souhaite voter
	 * @param action "plus","minus","end"
	 */
	public void startVoteIncident(IncidentModel incident, IncidentAction action, IBackgroundServiceVoteIncidentListener callback);
	
	/** 
	 * Enregistrement d'un favoris, asynchrone.
	 */
	public void startRegisterFavoris(LigneModel ligne);
	
	/**
	 * Ajout d'un listener pour prévenir des modifications de favoris
	 * @param listener
	 */
	public void addFavorisModifiedListener(IBackgroundServiceFavorisModifiedListener listener);
	
	/**
	 * Suppression d'un listener pour prévenir des modifications de favoris
	 * @param listener
	 */
	public void removeFavorisModifiedListener(IBackgroundServiceFavorisModifiedListener listener);
	
	/** 
	 * Lecture des favoris, asynchrone.
	 */
	public void startGetFavorisAsync(IBackgroundServiceGetFavorisListener callback);
	
	/**
	 * Lecture d'un paramètre, asynchrone
	 */
	public void startGetParametreAsync(IBackgroundServiceGetParametrageListener callback);
	
	/**
	 * Enregistrement d'un paramètre, asynchrone
	 */
	public void startRegisterParametreAsync(String cle, String valeur, IBackgroundServiceRegisterParametrageListener callback);

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
