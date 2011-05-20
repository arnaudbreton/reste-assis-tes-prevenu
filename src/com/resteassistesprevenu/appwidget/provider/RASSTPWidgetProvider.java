package com.resteassistesprevenu.appwidget.provider;

import com.resteassistesprevenu.appwidget.service.UpdateService;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class RASSTPWidgetProvider extends AppWidgetProvider {
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		if (appWidgetIds == null) {
            appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, RASSTPWidgetProvider.class));
        }		
		
		UpdateService.requestUpdate(appWidgetIds);
		context.startService(new Intent(context, UpdateService.class));
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		
		if(intent.getAction().equals(UpdateService.ACTION_SHOW_PREC_INCIDENT)) {
			UpdateService.showPrecIncident();
		}
		else if(intent.getAction().equals(UpdateService.ACTION_SHOW_NEXT_INCIDENT)) {
			UpdateService.showNextIncident();
		}
	}
}
