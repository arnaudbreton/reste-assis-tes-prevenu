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

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.resteassistesprevenu.model.IncidentModel;

public class IncidentsBackgroundService extends Service {

	private ArrayList<IncidentModel> incidents;
	
	private String serviceURLBase = "http://openreact.alwaysdata.net";
	private String serviceURLByHour = "/api/incidents.json/hour";
	private String serviceURLRunning = "/api/incidents.json/current";
	
	private IncidentsBackgroundServiceBinder mBinder;
	
	public class IncidentsBackgroundServiceBinder extends Binder {
		public IncidentsBackgroundService getService() {
            return IncidentsBackgroundService.this;
        }
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return this.mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.mBinder = new IncidentsBackgroundServiceBinder();
	}
	
	  @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		try {
			this.incidents = IncidentModel.deserializeFromArray(getIncidentsEnCours());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return START_STICKY;
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

	/**
	 * @return the incidents
	 */
	public ArrayList<IncidentModel> getIncidents() {
		return incidents;
	}	
}
