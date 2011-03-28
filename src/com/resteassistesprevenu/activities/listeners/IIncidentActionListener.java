package com.resteassistesprevenu.activities.listeners;

import com.resteassistesprevenu.model.IncidentAction;
import com.resteassistesprevenu.model.IncidentModel;

public interface IIncidentActionListener {
	public void actionPerformed(IncidentModel incident, IncidentAction action);
}
