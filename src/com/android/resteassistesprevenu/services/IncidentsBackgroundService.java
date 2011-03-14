package com.android.resteassistesprevenu.services;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import com.android.resteassistesprevenu.model.IncidentModel;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class IncidentsBackgroundService extends Service {

	private ArrayList<IncidentModel> incidents;
	
	private String serviceURLBase = "http://openreact.alwaysdata.net";
	private String serviceURLByHour = "/api/incidents.json/hour";
	private String serviceURLRunning = "/api/incidents.json/current";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();		
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		
		try {
			this.incidents = IncidentModel.deserializeFromArray(getIncidentsEnCours());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private String getIncidentsEnCours() throws JSONException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(this.serviceURLBase + this.serviceURLRunning);
		String result = null; 		
		
		ResponseHandler<String> handler = new BasicResponseHandler();
		try {
			result = httpclient.execute(request, handler);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		httpclient.getConnectionManager().shutdown();
		Log.i("ResteAssisTesPrevenu : ", result);
		
		return result;
	}	
}
