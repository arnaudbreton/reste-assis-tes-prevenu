package com.android.resteassistesprevenu.services;

public interface IIncidentsBackgroundService {
	public void addListener(IIncidentsBackgroundServiceListener listener);

	public void removeListener(IIncidentsBackgroundServiceListener listener);

	/**
	 * Recherche des incidents en cours, asynchrone
	 */
	public void startGetIncidentsEnCoursAsync();

	/**
	 * Recherche des incidents des dernières minutes, asynchrone
	 */
	public void startGetIncidentsMinuteAsync();

	/**
	 * Recherche des incidents de l'heure, asynchrone
	 */
	public void startGetIncidentsHeureAsync();
}
