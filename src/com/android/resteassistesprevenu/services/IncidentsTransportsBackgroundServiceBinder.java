package com.android.resteassistesprevenu.services;

import android.os.Binder;

public class IncidentsTransportsBackgroundServiceBinder extends Binder {		  
	    private IIncidentsTransportsBackgroundService service = null; 
	  
	    public IncidentsTransportsBackgroundServiceBinder(IIncidentsTransportsBackgroundService service) { 
	        super(); 
	        this.service = service; 
	    } 
	 
	    public IIncidentsTransportsBackgroundService getService(){ 
	        return service; 
	    } 
}
