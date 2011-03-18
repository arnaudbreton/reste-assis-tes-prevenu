package com.android.resteassistesprevenu.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.android.resteassistesprevenu.services.IIncidentsTransportsBackgroundService;
import com.android.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener;

public class IncidentsEnCoursActivity extends Activity {
	
	private List<IncidentModel> incidents;
	private IncidentModelArrayAdapter mAdapter;	
	private IIncidentsTransportsBackgroundService mBoundService;
	
	private Button mBtnEnCours;
	private Button mBtnHeure;
	private Button mBtnMinute;
	private Button mBtnAddIncident;
	
	private ProgressDialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incidents_en_cours_view);		
		
		initialize();
		
		ServiceConnection connection = new ServiceConnection() {
		    public void onServiceConnected(ComponentName className, IBinder service) {
		        Log.i("BackgroundService", "Connected!"); 
		    	mBoundService = ((IncidentsTransportsBackgroundServiceBinder)service).getService();
		    	
		    	startGetIncidentsFromServiceAsync(IncidentModel.SCOPE_CURRENT);
		    	
		        mBoundService.addGetIncidentsListener(new IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener() {			
					@Override
					public void dataChanged(List<IncidentModel> incidentsService) {
						try {
							setIncidents(incidentsService);
							loadingDialog.dismiss();
						}
						catch(Exception e) {
							Log.e("ResteAssisTesPrevenu : ", "Problème de conversion en retour du service", e);
						}						
					}
		        });
		    }

		    public void onServiceDisconnected(ComponentName className) 
		    {		
		    }
		};
		
		getApplicationContext().bindService(new Intent(".IncidentsBackgroundService.ACTION"), connection, Context.BIND_AUTO_CREATE);
	}
	
	private void initialize() {
		this.mBtnEnCours = (Button) this.findViewById(R.id.radioEnCours);
		this.mBtnHeure = (Button) this.findViewById(R.id.radioHeure);
		this.mBtnMinute = (Button) this.findViewById(R.id.radioMinute);		
		this.mBtnAddIncident = (Button) this.findViewById(R.id.btnAjouterIncident);		

		this.incidents = new ArrayList<IncidentModel>();
		this.mAdapter = new IncidentModelArrayAdapter(IncidentsEnCoursActivity.this, R.id.listViewIncidentEnCours, this.incidents);
		((android.widget.ListView) this.findViewById(R.id.listViewIncidentEnCours)).setAdapter(mAdapter);		
		
		this.mBtnAddIncident.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(IncidentsEnCoursActivity.this, NewIncidentActivity.class));
			}
		});
		
		this.mBtnEnCours.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startGetIncidentsFromServiceAsync(IncidentModel.SCOPE_CURRENT);
			}
		});
		
		this.mBtnHeure.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startGetIncidentsFromServiceAsync(IncidentModel.SCOPE_HOUR);
			}
		});
		
		this.mBtnMinute.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startGetIncidentsFromServiceAsync(IncidentModel.SCOPE_MINUTE);
			}
		});
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
		if(this.incidents == null) {
			this.incidents = new ArrayList<IncidentModel>();
		}
		
		this.incidents.clear();		
		this.incidents.addAll(incidents);
		mAdapter.notifyDataSetChanged();
	}
	
	private void startGetIncidentsFromServiceAsync(String scope) {
		loadingDialog = ProgressDialog.show(IncidentsEnCoursActivity.this, "", "Chargement des incidents en cours...");
		mBoundService.startGetIncidentsAsync(scope);
	}
}