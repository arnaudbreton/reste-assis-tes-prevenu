package com.resteassistesprevenu.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.adapters.PlagesHorairesExpandableListAdapter;
import com.resteassistesprevenu.services.IIncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;

public class ParametrageActivity extends Activity {
	/**
	 * Tag pour les logs
	 */
	private static final String TAG_ACTIVITY = "ParametrageActivity";

	/**
	 * ExpandableView des plages horaires
	 */
	private ExpandableListView mExpPlagesHoraires;

	/**
	 * Adapteur pour les pages horaires
	 */
	private PlagesHorairesExpandableListAdapter mAdapter;

	/**
	 * Connexion au service
	 */
	private ParametrageServiceConnection conn;

	/**
	 * Binding au service
	 */
	private IIncidentsTransportsBackgroundService mBoundService;
	
	private ListView mListViewParametres;
	
	private final static String[] listeParametres = {"Choix du serveur"};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.parametrage_view);

		mExpPlagesHoraires = (ExpandableListView) findViewById(R.id.expandablePlagesHorairesView);

		mAdapter = new PlagesHorairesExpandableListAdapter(this, mExpPlagesHoraires);
		
		mListViewParametres = (ListView) findViewById(R.id.listViewParametres);
		mListViewParametres.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listeParametres));
	}

	private void chooseServeur() {
		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"Début création AlertDialog choix du serveur");
		final CharSequence[] items = { "Production", "Pré-Production" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				R.string.title_parametrage).setSingleChoiceItems(items,
				mBoundService.isProduction() ? 0 : 1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mBoundService != null) {
							if (which == 0) {
								mBoundService.setProduction(true);
							} else {
								mBoundService.setProduction(false);
							}
						}

						dialog.dismiss();
					}
				});

		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"Affichage du choix du serveur");
		builder.show();
	}

	private void plagesHoraires() {
		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"Début création AlertDialog choix du serveur");
		final CharSequence[] items = {};
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				R.string.title_parametrage).setSingleChoiceItems(items,
				mBoundService.isProduction() ? 0 : 1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mBoundService != null) {
							if (which == 0) {
								mBoundService.setProduction(true);
							} else {
								mBoundService.setProduction(false);
							}					
						}

						dialog.dismiss();
					}
				});

		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"Affichage du choix du serveur");
		builder.show();
	}

	@Override
	protected void onStart() {
		super.onStart();

		conn = new ParametrageServiceConnection();
		bindService(new Intent(getApplicationContext(),
				IncidentsTransportsBackgroundService.class), conn,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();

		unbindService(conn);
	}

	private class ParametrageServiceConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className, IBinder service) {

			mBoundService = ((IncidentsTransportsBackgroundServiceBinder) service)
					.getService();

		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};
}
