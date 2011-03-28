package com.resteassistesprevenu.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

public class FavorisActivity extends ExpandableListActivity {
	private IIncidentsTransportsBackgroundService mBoundService;

	private List<String> typeLignesGroups;
	private Map<String, String> lignesGroups;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favoris_view);

		ServiceConnection connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName className,
					IBinder service) {
				Log.i(getString(R.string.log_tag_name), "Service Connected!");

				mBoundService = ((IncidentsTransportsBackgroundServiceBinder) service)
						.getService();

				mBoundService
						.addGetLignesListener(new IIncidentsTransportsBackgroundServiceGetLignesListener() {
							@Override
							public void dataChanged(List<LigneModel> lignes) {
								typeLignesGroups = new ArrayList<String>();							
								
								int indexType;
								for (int i = 0; i < lignes.size(); i++) {
									indexType = typeLignesGroups
											.indexOf(lignes.get(i)
													.getTypeLigne());
									if (indexType == -1) {
										typeLignesGroups.add(lignes.get(i)
												.getTypeLigne());
										indexType = typeLignesGroups
												.indexOf(lignes.get(i)
														.getTypeLigne());
									}									
								}
								
								lignesGroups = new HashMap<String, String>();
								for (int i = 0; i < lignes.size(); i++) {
									boolean typeLigneExist = typeLignesGroups
									.indexOf(lignes.get(i)
											.getTypeLigne()) != -1;
									
									if(typeLigneExist) {
										lignesGroups.put(lignes.get(i).getTypeLigne(), lignes.get(i).getNumLigne());
									}									
								}
								

						        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
						        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
						        for (int i = 0; i < lignes.size(); i++) {
						            Map<String, String> curGroupMap = new HashMap<String, String>();
						            groupData.add(curGroupMap);
						            curGroupMap.put(NAME, "Group " + i);
						            curGroupMap.put(IS_EVEN, (i % 2 == 0) ? "This group is even" : "This group is odd");
						            
						            List<Map<String, String>> children = new ArrayList<Map<String, String>>();
						            for (int j = 0; j < 15; j++) {
						                Map<String, String> curChildMap = new HashMap<String, String>();
						                children.add(curChildMap);
						                curChildMap.put(NAME, "Child " + j);
						                curChildMap.put(IS_EVEN, (j % 2 == 0) ? "This child is even" : "This child is odd");
						            }
						            childData.add(children);
						        }
						        
						        // Set up our adapter
						        mAdapter = new SimpleExpandableListAdapter(
						                this,
						                groupData,
						                android.R.layout.simple_expandable_list_item_1,
						                new String[] { NAME, IS_EVEN },
						                new int[] { android.R.id.text1, android.R.id.text2 },
						                childData,
						                android.R.layout.simple_expandable_list_item_2,
						                new String[] { NAME, IS_EVEN },
						                new int[] { android.R.id.text1, android.R.id.text2 }
						                );
						        setListAdapter(mAdapter);
								
								Object[] keys = lignesGroups.keySet().toArray();
								Object[] values = lignesGroups.values().toArray();

								String[][] twoDarray = new String[keys.length][values.length];

								for (int row = 0; row < keys.length; row++) {
								    twoDarray[row] = keys[row].toString();								   
								}

								setListAdapter(new FavorisExpandableListAdapter(
										FavorisActivity.this,
										typeLignesGroups
												.toArray(new String[typeLignesGroups
														.size()]),twoDarray));
							}
						});
				
				mBoundService.startGetLignesAsync("");
			}

			public void onServiceDisconnected(ComponentName className) {
			}
		};

		bindService(new Intent(getApplicationContext(),
				IncidentsTransportsBackgroundService.class), connection,
				Context.BIND_AUTO_CREATE);
	}
}
