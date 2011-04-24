package com.resteassistesprevenu.appwidget.provider;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.resteassistesprevenu.appwidget.service.UpdateService;

public class RASSTPWidgetProvider extends AppWidgetProvider {
	/**
	 * Intent pour le service de mise à jour
	 */
	private Intent intentService;

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		
		this.intentService = new Intent(context, UpdateService.class);
		context.startService(intentService);
	}
	@Override
	public void onUpdate(Context context,
			android.appwidget.AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {		
		super.onDeleted(context, appWidgetIds);
	}	
}
