package com.resteassistesprevenu.services.listeners;

import java.util.List;

import com.resteassistesprevenu.model.LigneModel;

/**
 * Listener pour la r�cup�ration des lignes
 * @author Arnaud
 *
 */
public interface IIncidentsTransportsBackgroundServiceGetLignesListener {
	 public void dataChanged(List<LigneModel> lignes); 
}