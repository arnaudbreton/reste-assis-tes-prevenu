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
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

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

	private FavorisExpandableListAdapter mAdapter;

	private boolean isModified;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favoris_view);

		this.isModified = false;
		
		mAdapter = new FavorisExpandableListAdapter(FavorisActivity.this);
		setListAdapter(mAdapter);

		ServiceConnection connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName className,
					IBinder service) {
				Log.i(getString(R.string.log_tag_name), "Service Connected!");

				mBoundService = ((IncidentsTransportsBackgroundServiceBinder) service)
						.getService();

				mBoundService
						.addGetTypeLignesListener(new IIncidentsTransportsBackgroundServiceGetTypeLignesListener() {
							@Override
							public void dataChanged(List<String> data) {

								for (String typeLigne : data) {
									mAdapter.getTypeLignesGroups().add(
											typeLigne);
									mAdapter.getLignesChildrenGroups().add(
											new ArrayList<LigneModel>());
									mBoundService
											.startGetLignesAsync(typeLigne);
								}
								mAdapter.notifyDataSetChanged();
							}
						});

				mBoundService
						.addGetLignesListener(new IIncidentsTransportsBackgroundServiceGetLignesListener() {
							@Override
							public void dataChanged(List<LigneModel> lignesModel) {
								if (lignesModel.size() > 0) {
									int indexType = mAdapter
											.getTypeLignesGroups().indexOf(
													lignesModel.get(0)
															.getTypeLigne());

									if (mAdapter.getLignesChildrenGroups()
											.get(indexType).size() == 0) {
										List<LigneModel> lignes = mAdapter
												.getLignesChildrenGroups().get(
														indexType);

										lignes.addAll(lignesModel);
									}

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

		getExpandableListView().setOnChildClickListener(this);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		LigneModel ligne = mAdapter.getLignesChildrenGroups()
				.get(groupPosition).get(childPosition);
		ligne.setFavoris(!ligne.isFavoris());

		mAdapter.notifyDataSetChanged();
		mBoundService.startRegisterFavoris(ligne);

		Toast.makeText(this, getString(R.string.msg_favoris_registered),
				Toast.LENGTH_SHORT).show();

		this.isModified = true;
		
		return true;
	}
	
	@Override
	public void onBackPressed() {	
		if(this.isModified) {
			setResult(RESULT_OK);
		}
		
		super.onBackPressed();		
	}
}
