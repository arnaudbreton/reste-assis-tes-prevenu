package com.resteassistesprevenu.services.listeners;

import java.util.List;

/**
 * Listener pour la récupération des types de ligne
 * @author Arnaud
 *
 */
public interface IIncidentsTransportsBackgroundServiceGetTypeLignesListener {
	 public void dataChanged(List<String> lignes); 
}