package com.android.resteassistesprevenu.services;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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

public class IncidentsBackgroundService extends Service implements IIncidentsBackgroundService {

	private static final String CURRENT = "current";

	private static final Object HOUR = "hour";

	private ArrayList<IncidentModel> incidents;
	
	private String serviceURLBase = "http://openreact.alwaysdata.net";
	private String jsonURL = "/api/incidents.json/";

	private IncidentBackgroundServiceBinder mBinder;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return this.mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.mBinder = new IncidentBackgroundServiceBinder(this);
	}
	
	  @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private String getIncidentsEnCoursFromService(String scope) {
		HttpClient httpclient = new DefaultHttpClient();

		HttpGet request = new HttpGet(this.serviceURLBase + this.jsonURL + scope);
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
	
	public ArrayList<IncidentModel> getIncidentsEnCours() {
		try {
			return IncidentModel.deserializeFromArray(getIncidentsEnCoursFromService(CURRENT));
		} catch (Exception e) {
			Log.e("IncidentsBackgroundService","Erreur au chargement des incidents : " + e.getMessage());
			return new ArrayList<IncidentModel>();
		}	
	}

	
	private void setIncidents(ArrayList<IncidentModel> incidents) {
		if(this.incidents == null) {
			this.incidents = new ArrayList<IncidentModel>();
		}
		
		this.incidents.clear();
		this.incidents.addAll(incidents);
	}

	private List<IIncidentsBackgroundServiceListener> listeners = null; 
	 
	// Ajout d'un listener 
	public void addListener(IIncidentsBackgroundServiceListener listener) { 
	    if(listeners == null){ 
	        listeners = new ArrayList<IIncidentsBackgroundServiceListener>(); 
	    } 
	    listeners.add(listener); 
	} 
	 
	// Suppression d'un listener 
	public void removeListener(IIncidentsBackgroundServiceListener listener) { 
	    if(listeners != null){ 
	        listeners.remove(listener); 
	    } 
	} 
	 
	// Notification des listeners 
	private void fireDataChanged(Object data){ 
	    if(listeners != null){ 
	        for(IIncidentsBackgroundServiceListener listener: listeners){ 
	            listener.dataChanged(data); 
	        } 
	    } 
	}
}
