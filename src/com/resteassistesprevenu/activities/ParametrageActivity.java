package com.resteassistesprevenu.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.services.IBackgroundService;
import com.resteassistesprevenu.services.BackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;

public class ParametrageActivity extends ListActivity {
	/**
	 * Tag pour les logs
	 */
	private static final String TAG_ACTIVITY = "ParametrageActivity";

	/**
	 * Connexion au service
	 */
	private ParametrageServiceConnection conn;

	/**
	 * Binding au service
	 */
	private IBackgroundService mBoundService;

	private ListView mListViewParametres;

	private final static String[] listeParametres = {
			"Plages horaires de synchronisation", "Choix du serveur" };

	private static final int PLAGES_HORAIRES_SYNC = 0;

	private static final int CHOIX_SERVEUR = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.parametrage_view);

		getListView().setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listeParametres));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position) {
		case PLAGES_HORAIRES_SYNC:
			startActivity(new Intent(this, PlagesHorairesActivity.class));
			break;
		case CHOIX_SERVEUR:
			chooseServeur();
			break;
		default:
			break;
		}
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
				BackgroundService.class), conn,
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
