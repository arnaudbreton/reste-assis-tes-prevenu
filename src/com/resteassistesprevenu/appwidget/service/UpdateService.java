package com.resteassistesprevenu.appwidget.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.activities.IncidentsEnCoursActivity;
import com.resteassistesprevenu.activities.NewIncidentActivity;
import com.resteassistesprevenu.appwidget.provider.RASSTPWidgetProvider;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.model.adapters.IncidentModelAdapter;
import com.resteassistesprevenu.services.IIncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener;

public class UpdateService extends Service {
	public static final String ACTION_SHOW_PREC_INCIDENT = "ACTION_SHOW_PREC_INCIDENT";
	public static final String ACTION_SHOW_NEXT_INCIDENT = "ActionShowNextIncident";

	/**
	 * Action à réaliser après chargement des incidents
	 */
	private IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener getIncidentsEnCoursListener;

	/**
	 * Les incidents du service
	 */
	private List<IncidentModel> incidents;

	private int incidentIndex;

	public UpdateService() {
		this.incidents = new ArrayList<IncidentModel>();
	}

	/**
	 * Connexion au service
	 */
	private ServiceIncidentConnection conn;

	/**
	 * Binder du service
	 */
	private IIncidentsTransportsBackgroundService mBoundService;

	private class ServiceIncidentConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(getApplicationContext().getString(R.string.log_tag_name),
					"Service Connected!");

			mBoundService = ((IncidentsTransportsBackgroundServiceBinder) service)
					.getService();

			getIncidentsEnCoursListener = new IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener() {
				@Override
				public void dataChanged(List<IncidentModel> incidentsService) {
					try {
						Log.i(getApplicationContext().getString(
								R.string.log_tag_name),
								"Début du chargement des incidents.");

						incidents.clear();
						if (incidentsService != null) {
							incidents.addAll(incidentsService);
						}

						ComponentName thisWidget = new ComponentName(
								getApplicationContext(),
								RASSTPWidgetProvider.class);
						AppWidgetManager manager = AppWidgetManager
								.getInstance(getApplicationContext());
						RemoteViews updateViews = buildUpdate();
						manager.updateAppWidget(thisWidget, updateViews);

						Log.i(getApplicationContext().getString(
								R.string.log_tag_name),
								"Chargement des incidents réussi.");
					} catch (Exception e) {
						Log.e(getApplicationContext().getString(
								R.string.log_tag_name),
								"Problème de chargement des incidents", e);
					}
				}
			};

			final Handler handler = new Handler();
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					handler.post(new Runnable() {
						public void run() {
							mBoundService.startGetIncidentsAsync(
									IncidentModel.SCOPE_JOUR, false,
									getIncidentsEnCoursListener);
						}
					});
				}
			}, 0, 20000);
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		Log.d(getResources().getString(R.string.log_tag_name),
				"UpdateService : onStart()");
		this.conn = new ServiceIncidentConnection();
		getApplicationContext().bindService(
				new Intent(getApplicationContext(),
						IncidentsTransportsBackgroundService.class), conn,
				Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(getResources().getString(R.string.log_tag_name),
				"UpdateService : onDestroy()");
		getApplicationContext().unbindService(conn);
		this.conn = null;
	}

	/**
	 * Build a widget update to show the current Wiktionary "Word of the day."
	 * Will block until the online API returns.
	 */
	public RemoteViews buildUpdate() {

		// récupération des 3 dernier titre du flux dans un tableau
		RemoteViews updateViews = new RemoteViews(getApplicationContext()
				.getPackageName(), R.layout.rasstp_appwidget);

		Intent incidentEnCoursIntent = new Intent(getApplicationContext(),
				IncidentsEnCoursActivity.class);
		PendingIntent incidentEnCoursPendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, incidentEnCoursIntent, 0);
		updateViews.setOnClickPendingIntent(R.id.btnWidgetLogo,
				incidentEnCoursPendingIntent);

//		Intent newIncidentIntent = new Intent(getApplicationContext(),
//				NewIncidentActivity.class);
//		PendingIntent newIncidentPendingIntent = PendingIntent.getActivity(
//				getApplicationContext(), 0, newIncidentIntent, 0);
//		updateViews.setOnClickPendingIntent(R.id.btnWidgetAddIncident,
//				newIncidentPendingIntent);

		if (this.incidents.size() > 0) {
			incidentIndex = (int) (Math.random() * (incidents.size()));
			Log.d(getResources().getString(R.string.log_tag_name),
					"Affichage de l'incident numéro " + incidentIndex);
			IncidentModel incident = incidents.get(incidentIndex);
			IncidentModelAdapter.getIncidentRemoteView(getApplicationContext(),
					updateViews, incident, incidentIndex + 1);

			updateViews.setTextViewText(R.id.txtNbTotalIncidents,
					String.valueOf(incidents.size() + 1));

			Intent showPrecIncidentIntent = new Intent(ACTION_SHOW_PREC_INCIDENT);
			PendingIntent showPrecIncidentPendingIntent = PendingIntent
					.getBroadcast(getApplicationContext(), 0,
							showPrecIncidentIntent, 0);
			updateViews.setOnClickPendingIntent(R.id.btnPrecIncident,
					showPrecIncidentPendingIntent);

			updateViews.setViewVisibility(R.id.incidentItemView, View.VISIBLE);
			updateViews.setViewVisibility(R.id.txtAucunIncident, View.GONE);
		} else {
			updateViews.setViewVisibility(R.id.incidentItemView, View.GONE);
			updateViews.setViewVisibility(R.id.txtAucunIncident, View.VISIBLE);
		}

		return updateViews;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't need to bind to this service
		return null;
	}
};