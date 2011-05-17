package com.resteassistesprevenu.appwidget.provider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.resteassistesprevenu.appwidget.service.UpdateService;

public class RASSTPWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		context.startService(new Intent(context, UpdateService.class));	
	}
}
