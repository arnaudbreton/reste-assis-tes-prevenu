package com.resteassistesprevenu.services.listeners;

import java.util.List;

import com.resteassistesprevenu.model.IncidentModel;

/**
 * Listener pour la r�cup�ration des incidents
 * @author Arnaud
 *
 */
public interface IBackgroundServiceGetIncidentsEnCoursListener {
	 public void dataChanged(List<IncidentModel> incidents); 
}
