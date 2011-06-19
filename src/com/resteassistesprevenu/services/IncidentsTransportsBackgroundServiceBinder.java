package com.resteassistesprevenu.services;

import android.os.Binder;

public class IncidentsTransportsBackgroundServiceBinder extends Binder {		  
	    private IBackgroundService service = null; 
	  
	    public IncidentsTransportsBackgroundServiceBinder(IBackgroundService service) { 
	        super(); 
	        this.service = service; 
	    } 
	 
	    public IBackgroundService getService(){ 
	        return service; 
	    } 
}
