package com.resteassistesprevenu.services.listeners;

/**
 * Listener pour voter pour un incident
 * @author Arnaud
 *
 */
public interface IBackgroundServiceVoteIncidentListener {
	 public void dataChanged(boolean voteSent); 
}
