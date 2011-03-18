package com.android.resteassistesprevenu.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android.resteassistesprevenu.R;
import com.android.resteassistesprevenu.services.IIncidentsTransportsBackgroundService;
import com.android.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetLignesListener;

import java.util.List;
import com.android.resteassistesprevenu.model.LigneModel;

public class NewIncidentActivity extends Activity {	
	private IIncidentsTransportsBackgroundService mBoundService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_incident_view);
		
		ServiceConnection connection = new ServiceConnection() {
		    public void onServiceConnected(ComponentName className, IBinder service) {
		        Log.i("IncidentsTransportsBackgroundService", "Connected!");
		        
		    	mBoundService = ((IncidentsTransportsBackgroundServiceBinder)service).getService();		    
		    	
		        mBoundService.addGetLignesListener(new IIncidentsTransportsBackgroundServiceGetLignesListener() {
					
					@Override
					public void dataChanged(List<LigneModel> data) {
						// TODO Auto-generated method stub						
					}
				});
		    }

		    public void onServiceDisconnected(ComponentName className) 
		    {		
		    }		
		};
		
		bindService(new Intent(".IncidentsBackgroundService.ACTION"), connection, Context.BIND_AUTO_CREATE);
	}
}
