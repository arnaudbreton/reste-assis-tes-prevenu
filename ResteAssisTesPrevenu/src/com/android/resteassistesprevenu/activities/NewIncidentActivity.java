package com.android.resteassistesprevenu.activities;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.resteassistesprevenu.R;
import com.android.resteassistesprevenu.services.IIncidentsTransportsBackgroundService;
import com.android.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetLignesListener;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetTypeLignesListener;

public class NewIncidentActivity extends Activity {	
	private IIncidentsTransportsBackgroundService mBoundService;
	
	private Spinner mSpinTypeLignes; 
	private Spinner mSpinLignes;
	
	private EditText mTxtRaison;
	
	private Button mBtnRapporter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_incident_view);
		
		 mSpinTypeLignes = (Spinner)this.findViewById(R.id.spinnerTypeLigne);		        
		 mSpinTypeLignes.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View arg1,
						int arg2, long arg3) {
					mBoundService.startGetLignesAsync(parent.getSelectedItem().toString());						
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {}		        	
	        });	    
		 
		mSpinLignes = (Spinner)this.findViewById(R.id.spinnerNumeroLigne);	
		 
		mBtnRapporter = (Button)this.findViewById(R.id.btnAjouterIncident);
		mBtnRapporter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				String typeLigne = null;
				String numLigne = null;
				String raison = null;
				
				if(mSpinTypeLignes != null && mSpinTypeLignes.getSelectedItem() != null) {
					typeLigne = mSpinTypeLignes.getSelectedItem().toString();
				}
				
				if(mSpinLignes != null && mSpinLignes.getSelectedItem() != null) {
					numLigne = mSpinLignes.getSelectedItem().toString();
				}
				
				if(mTxtRaison != null) {
					raison = mTxtRaison.getText().toString();
				}
				
				if(raison == null || raison == "") {
				}
				
				mBoundService.startReportIncident(typeLigne, numLigne, raison);
			}
		});
		
		ServiceConnection connection = new ServiceConnection() {
		    public void onServiceConnected(ComponentName className, IBinder service) {
		        Log.i("IncidentsTransportsBackgroundService", "Connected!");
		        
		    	mBoundService = ((IncidentsTransportsBackgroundServiceBinder)service).getService();   
		    	
		    	mBoundService.addGetTypeLignesListener(new IIncidentsTransportsBackgroundServiceGetTypeLignesListener() {					
					@Override
					public void dataChanged(List<String> data) {
						Spinner spinTypeLigne = (Spinner)NewIncidentActivity.this.findViewById(R.id.spinnerTypeLigne);
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewIncidentActivity.this,
					            android.R.layout.simple_spinner_item, data.toArray(new String[data.size()]));
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinTypeLigne.setAdapter(adapter);
						mBoundService.startGetLignesAsync(spinTypeLigne.getSelectedItem().toString());
					}
				});
		    	
		    	mBoundService.addGetLignesListener(new IIncidentsTransportsBackgroundServiceGetLignesListener() {					
					@Override
					public void dataChanged(List<String> data) {
						Spinner spinTypeLigne = (Spinner)NewIncidentActivity.this.findViewById(R.id.spinnerNumeroLigne);
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(NewIncidentActivity.this,
					            android.R.layout.simple_spinner_item, data.toArray(new String[data.size()]));
						adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinTypeLigne.setAdapter(adapter);
					}
				});
		        
		        mBoundService.startGetTypeLignesAsync();     
		    }

		    public void onServiceDisconnected(ComponentName className) {}		
		};
		
		bindService(new Intent(".IncidentsBackgroundService.ACTION"), connection, Context.BIND_AUTO_CREATE);
	}
}
