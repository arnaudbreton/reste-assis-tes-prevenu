package com.android.resteassistesprevenu.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.android.resteassistesprevenu.R;

public class HomeActivity extends TabActivity {

	/**
	 * Référence au TabHost
	 */
	private TabHost mTabHost;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mTabHost = getTabHost();
		startService(new Intent(".IncidentsBackgroundService.ACTION"));

		mTabHost.addTab(mTabHost.newTabSpec("tab_test1")
				.setIndicator("Incidents en cours")
				.setContent(new Intent(this, IncidentsEnCoursActivity.class)));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
				.setIndicator("Lignes favorites").setContent(new Intent(this, IncidentsEnCoursActivity.class)));

		mTabHost.setCurrentTab(0);		
	}
}