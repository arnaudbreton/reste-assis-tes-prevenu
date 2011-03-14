package com.android.resteassistesprevenu.activities;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.android.resteassistesprevenu.model.IncidentModel;
import com.android.resteassistesprevenu.model.adapters.IncidentModelArrayAdapter;
import com.android.resteassistesprevenu.services.IncidentsBackgroundService;

public class IncidentsEnCoursActivity extends ListActivity {
	
	private ArrayList<IncidentModel> incidents;

	private IncidentsBackgroundService mBoundService; 
	
	private ServiceConnection mConnection = new ServiceConnection() {
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        // This is called when the connection with the service has been
	        // established, giving us the service object we can use to
	        // interact with the service.  Because we have bound to a explicit
	        // service that we know is running in our own process, we can
	        // cast its IBinder to a concrete class and directly access it.
	        mBoundService = ((IncidentsBackgroundService.IncidentsBackgroundServiceBinder)service).getService();
	    }

	    public void onServiceDisconnected(ComponentName className) {
	        // This is called when the connection with the service has been
	        // unexpectedly disconnected -- that is, its process crashed.
	        // Because it is running in our same process, we should never
	        // see this happen.
	        mBoundService = null;
	    }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 bindService(new Intent(IncidentsEnCoursActivity.this, 
		            IncidentsBackgroundService.class), mConnection, Context.BIND_AUTO_CREATE);
		 this.incidents = mBoundService.getIncidents();
		 setListAdapter(new IncidentModelArrayAdapter(IncidentsEnCoursActivity.this, android.R.layout.simple_list_item_1, this.incidents));
	}
	
	/**
	 * @return the incidents
	 */
	public ArrayList<IncidentModel> getIncidents() {
		return incidents;
	}

	/**
	 * @param incidents the incidents to set
	 */
	public void setIncidents(ArrayList<IncidentModel> incidents) {
		this.incidents.clear();		
		this.incidents.addAll(incidents);
	}
}