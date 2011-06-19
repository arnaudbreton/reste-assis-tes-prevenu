package com.resteassistesprevenu.services.listeners;

import java.util.List;

import com.resteassistesprevenu.model.LigneModel;

/**
 * Listener pour la récupération des favoris
 * @author Arnaud
 *
 */
public interface IBackgroundServiceGetFavorisListener {
	 public void dataChanged(List<LigneModel> lignes); 
}