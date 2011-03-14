package com.android.resteassistesprevenu.services;

import android.os.Binder;

public class IncidentBackgroundServiceBinder extends Binder {		  
	    private IIncidentsBackgroundService service = null; 
	  
	    public IncidentBackgroundServiceBinder(IIncidentsBackgroundService service) { 
	        super(); 
	        this.service = service; 
	    } 
	 
	    public IIncidentsBackgroundService getService(){ 
	        return service; 
	    } 
}
