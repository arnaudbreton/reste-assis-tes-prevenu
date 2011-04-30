package com.resteassistesprevenu.appwidget.provider;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.resteassistesprevenu.appwidget.service.UpdateService;

public class RASSTPWidgetProvider extends AppWidgetProvider {
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		context.startService(new Intent(context, UpdateService.class));	
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		context.stopService(new Intent(context, UpdateService.class));	
	}
}
