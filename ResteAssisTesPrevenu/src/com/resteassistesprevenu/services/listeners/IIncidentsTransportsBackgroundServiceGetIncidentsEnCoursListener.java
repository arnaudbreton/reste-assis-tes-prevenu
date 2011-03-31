package com.resteassistesprevenu.services.listeners;

import java.util.List;

import com.resteassistesprevenu.model.IncidentModel;

/**
 * Listener pour la r�cup�ration des incidents
 * @author Arnaud
 *
 */
public interface IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener {
	 public void dataChanged(List<IncidentModel> incidents); 
}
