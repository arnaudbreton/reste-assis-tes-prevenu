package com.android.resteassistesprevenu.activities;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.android.resteassistesprevenu.R;

public class Main extends TabActivity {

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

		mTabHost.addTab(mTabHost.newTabSpec("tab_test1")
				.setIndicator("Incidents en cours")
				.setContent(new Intent(this, IncidentsEnCours.class)));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
				.setIndicator("Lignes favorites").setContent(new Intent(this, IncidentsEnCours.class)));

		mTabHost.setCurrentTab(0);
	}
}