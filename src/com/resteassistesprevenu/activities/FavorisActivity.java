package com.resteassistesprevenu.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.ExpandableListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.LigneModel;
import com.resteassistesprevenu.model.adapters.FavorisExpandableListAdapter;
import com.resteassistesprevenu.services.IIncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetLignesListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetTypeLignesListener;

public class FavorisActivity extends ExpandableListActivity {
	private IIncidentsTransportsBackgroundService mBoundService;

	private List<String> typeLignesGroups;
	private List<List<String>> lignesGroups;
	
	private FavorisExpandableListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favoris_view);

		typeLignesGroups = new ArrayList<String>();
		lignesGroups = new ArrayList<List<String>>();
		
		mAdapter = new FavorisExpandableListAdapter(
				FavorisActivity.this,
				typeLignesGroups, lignesGroups);
		setListAdapter(mAdapter);
		
		ServiceConnection connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName className,
					IBinder service) {
				Log.i(getString(R.string.log_tag_name), "Service Connected!");

				
				
				mBoundService = ((IncidentsTransportsBackgroundServiceBinder) service)
						.getService();

				mBoundService.addGetTypeLignesListener(new IIncidentsTransportsBackgroundServiceGetTypeLignesListener() {					
					@Override
					public void dataChanged(List<String> data) {
						typeLignesGroups = new ArrayList<String>();
						for(String typeLigne : data) {
							typeLignesGroups.add(typeLigne);
							lignesGroups.add(new ArrayList<String>());
							mBoundService.startGetLignesAsync(typeLigne);
						}		
						mAdapter.setTypeLignesGroups(typeLignesGroups);
						mAdapter.notifyDataSetChanged();
					}
				});
				
				mBoundService
						.addGetLignesListener(new IIncidentsTransportsBackgroundServiceGetLignesListener() {
							@Override
							public void dataChanged(List<LigneModel> lignesModel) {
								if(lignesModel.size() > 0) {
									int indexType = typeLignesGroups.indexOf(lignesModel.get(0).getTypeLigne());
									List<String> lignes = lignesGroups.get(indexType);
									
									for(LigneModel ligneModel : lignesModel) {
										lignes.add(ligneModel.getNumLigne());									
									}							
							     
									mAdapter.setLignesChildrenGroups(lignesGroups);
									mAdapter.notifyDataSetChanged();
								}
							}
						});			
								
				mBoundService.startGetTypeLignesAsync();
			}

			public void onServiceDisconnected(ComponentName className) {
			}
		};

		bindService(new Intent(getApplicationContext(),
				IncidentsTransportsBackgroundService.class), connection,
				Context.BIND_AUTO_CREATE);
	}
}
