package com.resteassistesprevenu.appwidget.provider;

import java.util.List;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.activities.IncidentsEnCoursActivity;
import com.resteassistesprevenu.activities.NewIncidentActivity;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.model.LigneModelService;
import com.resteassistesprevenu.services.IIncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener;

public class RASSTPWidgetProvider extends AppWidgetProvider {
	private Context context;
	
	private Intent intentService;
	
	/**
	 * Action à réaliser après chargement des incidents
	 */
	private IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener getIncidentsEnCoursListener;
	

	/**
	 * Les incidents du service
	 */
	private List<IncidentModel> incidents;
	
	/**
	 * Connexion au service
	 */
	private ServiceIncidentConnection conn;

	/**
	 * Binder du service
	 */
	private IIncidentsTransportsBackgroundService mBoundService;

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);	
		
		this.context = context;
		
		this.intentService = new Intent(context, IncidentsTransportsBackgroundService.class);
		context.startService(intentService);
	}
	
	@Override
	public void onDisabled(Context context) {	
		context.stopService(intentService);
		
		super.onDisabled(context);
	}
	
	@Override
	public void onUpdate(Context context,
			android.appwidget.AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		final int nbWidget = appWidgetIds.length;
		
		for(int i=0;i<nbWidget;i++) {
			// Build the widget update for today
			RemoteViews updateViews = buildUpdate(context);
			
			// Push update for this widget to the home screen
			ComponentName thisWidget = new ComponentName(context,
					RASSTPWidgetProvider.class);
			AppWidgetManager manager = AppWidgetManager.getInstance(context);
			manager.updateAppWidget(thisWidget, updateViews);			
		}
	
	}

	private RemoteViews buildUpdate(Context context) {
		// récupération des 3 dernier titre du flux dans un tableau
		RemoteViews updateViews = new RemoteViews(context.getPackageName(),
				R.layout.rasstp_appwidget);

		Intent incidentEnCoursIntent = new Intent(context,
				IncidentsEnCoursActivity.class);
		PendingIntent incidentEnCoursPendingIntent = PendingIntent
				.getActivity(context, 0, incidentEnCoursIntent, 0);
		updateViews.setOnClickPendingIntent(R.id.btnWidgetLogo,
				incidentEnCoursPendingIntent);

		Intent newIncidentIntent = new Intent(context,
				NewIncidentActivity.class);
		PendingIntent newIncidentPendingIntent = PendingIntent.getActivity(
				context, 0, newIncidentIntent, 0);
		updateViews.setOnClickPendingIntent(R.id.btnWidgetAddIncident,
				newIncidentPendingIntent);
		
		if(this.incidents != null && this.incidents.size() > 0) {
			IncidentModel incident = incidents.get(0);
			
			int imageResource =  context.getResources().getIdentifier(LigneModelService.getTypeLigneImage(incident.getLigne().getTypeLigne()), "drawable", context.getPackageName());
			updateViews.setImageViewResource(R.id.imgTypeLigne, imageResource);
		}
		
		return updateViews;
	}
	
	private class ServiceIncidentConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(context.getString(R.string.log_tag_name),
					"Service Connected!");

			mBoundService = ((IncidentsTransportsBackgroundServiceBinder) service)
					.getService();

			getIncidentsEnCoursListener = new IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener() {
				@Override
				public void dataChanged(List<IncidentModel> incidentsService) {
					try {
						Log.i(context.getString(R.string.log_tag_name),
								"Début du chargement des incidents.");

						incidents.addAll(incidentsService);
						
						// Build the widget update for today
						RemoteViews updateViews = buildUpdate(context);
						
						// Push update for this widget to the home screen
						ComponentName thisWidget = new ComponentName(context,
								RASSTPWidgetProvider.class);
						AppWidgetManager manager = AppWidgetManager.getInstance(context);
						manager.updateAppWidget(thisWidget, updateViews);			
						

						Log.i(context.getString(R.string.log_tag_name),
								"Chargement des incidents réussi.");
					} catch (Exception e) {
						Log.e(context.getString(R.string.log_tag_name),
								"Problème de chargement des incidents", e);
					}
				}
			};
			
			mBoundService.startGetIncidentsAsync(IncidentModel.SCOPE_JOUR, getIncidentsEnCoursListener);
		}
		
		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};

}
