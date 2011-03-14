package com.android.resteassistesprevenu.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.resteassistesprevenu.R;
import com.android.resteassistesprevenu.model.IncidentModel;
import com.android.resteassistesprevenu.model.adapters.IncidentModelArrayAdapter;
import com.android.resteassistesprevenu.services.IIncidentsBackgroundService;
import com.android.resteassistesprevenu.services.IIncidentsBackgroundServiceListener;
import com.android.resteassistesprevenu.services.IncidentBackgroundServiceBinder;

public class IncidentsEnCoursActivity extends Activity {
	
	private List<IncidentModel> incidents;
	private IncidentModelArrayAdapter mAdapter;	
	private IIncidentsBackgroundService mBoundService;
	
	private Button mBtnEnCours;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incidents_en_cours_view);
		
		this.incidents = new ArrayList<IncidentModel>();
		this.mAdapter = new IncidentModelArrayAdapter(IncidentsEnCoursActivity.this, R.id.listViewIncidentEnCours, this.incidents);
		((android.widget.ListView) this.findViewById(R.id.listViewIncidentEnCours)).setAdapter(mAdapter);
		
		mBtnEnCours = (Button) this.findViewById(R.id.radio0);
		mBtnEnCours.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mBoundService != null) {
					setIncidents(mBoundService.getIncidentsEnCours());
			    	mAdapter.notifyDataSetChanged();	
				}
			}
		});
		

		
		ServiceConnection connection = new ServiceConnection() {
		    public void onServiceConnected(ComponentName className, IBinder service) {
		        Log.i("BackgroundService", "Connected!"); 
		    	mBoundService = ((IncidentBackgroundServiceBinder)service).getService();
		    	
		    	setIncidents(mBoundService.getIncidentsEnCours());
		    	mAdapter.notifyDataSetChanged();
		    	
		        mBoundService.addListener(new IIncidentsBackgroundServiceListener() {			
					@Override
					public void dataChanged(Object o) {
						incidents.clear();					
						incidents.addAll((ArrayList<IncidentModel>) o);		
						mAdapter.notifyDataSetChanged();
					}
		        });
		    }

		    public void onServiceDisconnected(ComponentName className) {		
		    }
		};
		
		getApplicationContext().bindService(new Intent(".IncidentsBackgroundService.ACTION"), connection, Context.BIND_AUTO_CREATE);
	}
	
	/**
	 * @return the incidents
	 */
	public List<IncidentModel> getIncidents() {
		return incidents;
	}

	/**
	 * @param incidents the incidents to set
	 */
	public void setIncidents(List<IncidentModel> incidents) {
		this.incidents.clear();		
		this.incidents.addAll(incidents);
	}
}