package com.resteassistesprevenu.services.listeners;

import java.util.List;

import com.resteassistesprevenu.model.LigneModel;


public interface IIncidentsTransportsBackgroundServiceGetLignesListener {
	 public void dataChanged(List<LigneModel> lignes); 
}