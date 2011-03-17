package com.android.resteassistesprevenu.services;

public interface IIncidentsBackgroundService {
	public void addListener(IIncidentsBackgroundServiceListener listener);

	public void removeListener(IIncidentsBackgroundServiceListener listener);

	/**
	 * Recherche des incidents, asynchrone
	 */
	public void startGetIncidentsAsync(String scope);
}
