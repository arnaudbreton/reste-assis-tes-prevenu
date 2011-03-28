package com.resteassistesprevenu.activities.listeners;

import com.resteassistesprevenu.model.IncidentAction;

public interface IIncidentActionListener {
	public void actionPerformed(int incidentId, IncidentAction action);
}
