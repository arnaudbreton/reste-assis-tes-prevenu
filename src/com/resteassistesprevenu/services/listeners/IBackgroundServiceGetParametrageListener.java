package com.resteassistesprevenu.services.listeners;

import com.resteassistesprevenu.model.ParametreModel;

/**
 * Listener pour la récupération des favoris
 * @author Arnaud
 *
 */
public interface IBackgroundServiceGetParametrageListener {
	 public void dataChanged(ParametreModel param); 
}