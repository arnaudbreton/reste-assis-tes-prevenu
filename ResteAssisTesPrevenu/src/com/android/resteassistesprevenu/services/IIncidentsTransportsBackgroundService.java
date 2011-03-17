package com.android.resteassistesprevenu.services;

public interface IIncidentsTransportsBackgroundService {	
	public void addListener(IIncidentsTransportsBackgroundServiceListener listener);
	public void removeListener(IIncidentsTransportsBackgroundServiceListener listener);

	/**
	 * Recherche des incidents, asynchrone
	 */
	public void startGetIncidentsAsync(String scope);
	
	/**
	 * R�cup�ration des lignes, asynchrone
	 */
	public void startGetLignesAsync();
}
