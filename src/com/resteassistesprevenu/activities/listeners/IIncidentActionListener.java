package com.resteassistesprevenu.activities.listeners;

import com.resteassistesprevenu.model.IncidentAction;
import com.resteassistesprevenu.model.IncidentModel;

/**
 * Interface repr�sentant une action r�alis�e sur un incident
 * @author Arnaud
 *
 */
public interface IIncidentActionListener {
	/**
	 * R�alisation d'une action
	 * @param incident Mod�le de l'incident
	 * @param action Action r�alis�e
	 */
	public void actionPerformed(IncidentModel incident, IncidentAction action);
}
