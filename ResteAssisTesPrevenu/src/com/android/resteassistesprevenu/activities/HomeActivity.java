package com.android.resteassistesprevenu.activities;

import java.io.IOException;
import java.text.ParseException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

import com.android.resteassistesprevenu.R;
import com.android.resteassistesprevenu.model.IncidentModel;
import com.android.resteassistesprevenu.model.adapters.IncidentModelArrayAdapter;
import com.android.resteassistesprevenu.services.IncidentsBackgroundService;

public class HomeActivity extends TabActivity {

	/**
	 * Référence au TabHost
	 */
	private TabHost mTabHost;

	
	/**
	 * Référence à la vue des incidents en cours
	 */
	private Intent mIncidentsEnCours;
	
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