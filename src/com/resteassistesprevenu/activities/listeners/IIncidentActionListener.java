package com.resteassistesprevenu.activities.listeners;

import com.resteassistesprevenu.model.IncidentAction;
import com.resteassistesprevenu.model.IncidentModel;

/**
 * Interface représentant une action réalisée sur un incident
 * @author Arnaud
 *
 */
public interface IIncidentActionListener {
	/**
	 * Réalisation d'une action
	 * @param incident Modèle de l'incident
	 * @param action Action réalisée
	 */
	public void actionPerformed(IncidentModel incident, IncidentAction action);
}
