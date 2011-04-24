package com.resteassistesprevenu.appwidget.provider;

import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.activities.IncidentsEnCoursActivity;
import com.resteassistesprevenu.activities.NewIncidentActivity;

public class RASSTPWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, android.appwidget.AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
        	int appWidgetId = appWidgetIds[i];
        	 
        	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.rasstp_appwidget);
			
        	Intent incidentEnCoursIntent = new Intent(context, IncidentsEnCoursActivity.class);
	        PendingIntent incidentEnCoursPendingIntent = PendingIntent.getActivity(context, 0, incidentEnCoursIntent, 0);
	        views.setOnClickPendingIntent(R.id.btnWidgetLogo, incidentEnCoursPendingIntent);
	        
	     	Intent newIncidentIntent = new Intent(context, NewIncidentActivity.class);
	        PendingIntent newIncidentPendingIntent = PendingIntent.getActivity(context, 0, newIncidentIntent, 0);
	        views.setOnClickPendingIntent(R.id.btnWidgetAddIncident, newIncidentPendingIntent);
	        
	        appWidgetManager.updateAppWidget(appWidgetId, views);
        }
	}
}
