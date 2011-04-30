package com.resteassistesprevenu.appwidget.provider;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.resteassistesprevenu.appwidget.service.UpdateService;

public class RASSTPWidgetProvider extends AppWidgetProvider {	
	@Override
	public void onUpdate(Context context,
			android.appwidget.AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {		
		context.startService(new Intent(context, UpdateService.class));	
	}
}
