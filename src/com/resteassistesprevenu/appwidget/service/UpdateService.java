package com.resteassistesprevenu.appwidget.service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
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
	public static final String ACTION_SHOW_PREC_INCIDENT = "com.resteassistesprevenu.appwidget.service.UpdateService.ACTION_SHOW_PREV_INCIDENT";
	 public static final String ACTION_SHOW_NEXT_INCIDENT = "com.resteassistesprevenu.appwidget.service.UpdateService.ACTION_SHOW_NEXT_INCIDENT";

	/**
	 * Action � r�aliser apr�s chargement des incidents
	 */
	private IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener getIncidentsEnCoursListener;

	/**
	 * Les incidents du service
	 */
	private static List<IncidentModel> incidents = new ArrayList<IncidentModel>();

	private static int incidentIndex = 0;

	/**
	 * Connexion au service
	 */
	private ServiceIncidentConnection conn;

	/**
	 * Binder du service
	 */
	private IIncidentsTransportsBackgroundService mBoundService;

	private static int mode = 0;
	
	private static final Object mLockObject = new Object();
	
    /**
     * Lock used when maintaining queue of requested updates.
     */
    private static Object sLock = new Object();

    /**
     * Flag if there is an update thread already running. We only launch a new
     * thread if one isn't already running.
     */
    private static boolean sThreadRunning = false;

    /**
     * Internal queue of requested widget updates. You <b>must</b> access
     * through {@link #requestUpdate(int[])} or {@link #getNextUpdate()} to make
     * sure your access is correctly synchronized.
     */
    private static Queue<Integer> sAppWidgetIds = new LinkedList<Integer>();

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
								"D�but du chargement des incidents.");

						incidents.clear();
						incidentIndex = 0;
						if (incidentsService != null) {
							incidents.addAll(incidentsService);
						}
						
						while(hasMoreUpdates()) {
							AppWidgetManager manager = AppWidgetManager
									.getInstance(getApplicationContext());
							RemoteViews updateViews = buildUpdate();
							manager.updateAppWidget(getNextUpdate(), updateViews);
							
							Log.i(getApplicationContext().getString(
									R.string.log_tag_name),
									"Chargement des incidents r�ussi.");
						}
					} catch (Exception e) {
						Log.e(getApplicationContext().getString(
								R.string.log_tag_name),
								"Probl�me de chargement des incidents", e);
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
									IncidentModel.SCOPE_HOUR, false,
									getIncidentsEnCoursListener);
						}
					});
				}
			}, 0, 10000);
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
     * Request updates for the given widgets. Will only queue them up, you are
     * still responsible for starting a processing thread if needed, usually by
     * starting the parent service.
     */
    public static void requestUpdate(int[] appWidgetIds) {
        synchronized (sLock) {
            for (int appWidgetId : appWidgetIds) {
                sAppWidgetIds.add(appWidgetId);
            }
        }
    }
    
    /**
     * Peek if we have more updates to perform. This method is special because
     * it assumes you're calling from the update thread, and that you will
     * terminate if no updates remain. (It atomically resets
     * {@link #sThreadRunning} when none remain to prevent race conditions.)
     */
    private static boolean hasMoreUpdates() {
        synchronized (sLock) {
            boolean hasMore = !sAppWidgetIds.isEmpty();
            if (!hasMore) {
                sThreadRunning = false;
            }
            return hasMore;
        }
    }
    
    /**
     * Poll the next widget update in the queue.
     */
    private static int getNextUpdate() {
        synchronized (sLock) {
            if (sAppWidgetIds.peek() == null) {
                return AppWidgetManager.INVALID_APPWIDGET_ID;
            } else {
                return sAppWidgetIds.poll();
            }
        }
    }
    
	/**
	 * Build a widget update to show the current Wiktionary "Word of the day."
	 * Will block until the online API returns.
	 */
	public RemoteViews buildUpdate() {
		// r�cup�ration des 3 dernier titre du flux dans un tableau
		RemoteViews updateViews = new RemoteViews(getApplicationContext()
				.getPackageName(), R.layout.rasstp_appwidget);

		// Action vers l'�cran d'accueil de RASSTP
		Intent incidentEnCoursIntent = new Intent(getApplicationContext(),
				IncidentsEnCoursActivity.class);
		PendingIntent incidentEnCoursPendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, incidentEnCoursIntent, 0);
		updateViews.setOnClickPendingIntent(R.id.btnWidgetLogo,
				incidentEnCoursPendingIntent);

		// Action vers l'�cran d'ajout d'un incident 
		Intent newIncidentIntent = new Intent(getApplicationContext(),
				NewIncidentActivity.class);
		PendingIntent newIncidentPendingIntent = PendingIntent.getActivity(
				getApplicationContext(), 0, newIncidentIntent, 0);
		updateViews.setOnClickPendingIntent(R.id.btnWidgetAddIncident,
				newIncidentPendingIntent);

		if (incidents.size() > 0) {
			if (mode == 0) {
				showNextIncident();
			} else {
				mode = 0;
			}
			Log.d(getResources().getString(R.string.log_tag_name),
					"Affichage de l'incident num�ro " + incidentIndex);
			IncidentModel incident = incidents.get(incidentIndex);
			IncidentModelAdapter.getIncidentRemoteView(getApplicationContext(),
					updateViews, incident, incidentIndex + 1);

			updateViews.setTextViewText(R.id.txtNbTotalIncidents,
					String.valueOf(incidents.size()));

			Intent showPrecIncidentIntent = new Intent(getApplicationContext(),
					RASSTPWidgetProvider.class);
			showPrecIncidentIntent.setAction(ACTION_SHOW_PREC_INCIDENT);
			PendingIntent showPrecIncidentPendingIntent = PendingIntent
					.getBroadcast(getApplicationContext(), 0,
							showPrecIncidentIntent, 0);
			updateViews.setOnClickPendingIntent(R.id.btnPrecIncident,
					showPrecIncidentPendingIntent);
			
			Intent showNextIncidentIntent = new Intent(getApplicationContext(),
					RASSTPWidgetProvider.class);
			showNextIncidentIntent.setAction(ACTION_SHOW_NEXT_INCIDENT);
			PendingIntent showNextIncidentPendingIntent = PendingIntent
					.getBroadcast(getApplicationContext(), 0,
							showNextIncidentIntent, 0);
			updateViews.setOnClickPendingIntent(R.id.btnNextIncident,
					showNextIncidentPendingIntent);

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

	public static void showPrecIncident() {
		new Thread() {
			public void run() {
				synchronized (mLockObject) {
					if (incidentIndex - 1 >= 0) {
						incidentIndex--;
					} else {
						incidentIndex = incidents.size();
					}

					mode = 1;
				}
			};
		}.start();
	}

	public static void showNextIncident() {
		new Thread() {
			public void run() {
				synchronized (mLockObject) {
					if (incidentIndex + 1 < incidents.size()) {
						incidentIndex++;
					} else {
						incidentIndex = 0;
					}

					mode = 1;			
				}
			};
		}.start();
	}

};