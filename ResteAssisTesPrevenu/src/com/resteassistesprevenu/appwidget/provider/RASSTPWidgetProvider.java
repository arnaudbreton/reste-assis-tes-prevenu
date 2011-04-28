package com.resteassistesprevenu.appwidget.provider;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.resteassistesprevenu.appwidget.service.UpdateService;

public class RASSTPWidgetProvider extends AppWidgetProvider {

	@Override
	public void onEnabled(Context context) {		
		context.startService(new Intent(context, UpdateService.class));
		
		super.onEnabled(context);
	}
	
	@Override
	public void onDisabled(Context context) {	
		context.stopService(new Intent(context, UpdateService.class));
		
		super.onDisabled(context);
	}
	
	@Override
	public void onUpdate(Context context,
			android.appwidget.AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		final int nbWidget = appWidgetIds.length;
		
		for(int i=0;i<nbWidget;i++) {
			// Build the widget update for today
//			RemoteViews updateViews = buildUpdate(context);
//			
//			// Push update for this widget to the home screen
//			ComponentName thisWidget = new ComponentName(context,
//					RASSTPWidgetProvider.class);
//			AppWidgetManager manager = AppWidgetManager.getInstance(context);
//			manager.updateAppWidget(thisWidget, updateViews);			
		}
	
	}
}
