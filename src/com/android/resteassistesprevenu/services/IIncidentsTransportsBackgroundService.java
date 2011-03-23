package com.android.resteassistesprevenu.services;

import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetLignesListener;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetTypeLignesListener;

public interface IIncidentsTransportsBackgroundService {	
	public void addGetIncidentsListener(IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener);
	public void removeGetIncidentsListener(IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener);
	
	public void addGetTypeLignesListener(IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener);
	public void removeGetTypeLignesListener(IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener);

	/**
	 * Recherche des incidents, asynchrone
	 */
	public void startGetIncidentsAsync(String scope);
	
	/**
	 * Récupération des lignes, asynchrone
	 */
	public void startGetLignesAsync();
}
