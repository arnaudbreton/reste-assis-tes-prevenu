package com.android.resteassistesprevenu.services.listeners;

import java.util.List;

import com.android.resteassistesprevenu.model.LigneModel;


public interface IIncidentsTransportsBackgroundServiceGetLignesListener {
	 public void dataChanged(List<LigneModel> lignes); 
}