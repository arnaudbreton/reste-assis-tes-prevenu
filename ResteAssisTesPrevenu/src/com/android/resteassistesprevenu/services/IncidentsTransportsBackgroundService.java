package com.android.resteassistesprevenu.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.android.resteassistesprevenu.model.IncidentModel;

public class IncidentsTransportsBackgroundService extends Service implements IIncidentsTransportsBackgroundService {

	/**
	 * AsyncTask de récupération des incidents
	 *
	 */
	private class LoadIncidentsAsyncTask extends AsyncTask<String, Void, List<IncidentModel>> {

		@Override
		protected List<IncidentModel> doInBackground(String... params) {		
			try {			
				return IncidentModel.deserializeFromArray(getIncidentsEnCoursFromService(params[0]));
			} catch (Exception e) {
				Log.e("ResteAssisTesPrevenu", "Erreur au chargement des incidents par le service", e);
				return new ArrayList<IncidentModel>();
			}
		}
		
		@Override
		protected void onPostExecute(List<IncidentModel> result) {
			super.onPostExecute(result);
			fireDataChanged(result);
		}
	}
	
	/**
	 * AsyncTask de récupération des incidents
	 *
	 */
	private class LoadLignesAsyncTask extends AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... params) {		
			try {			
				return new ArrayList<String>();
			} catch (Exception e) {
				Log.e("ResteAssisTesPrevenu", "Erreur au chargement des incidents par le service", e);
				return new ArrayList<String>();
			}
		}
		
		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			fireDataChanged(result);
		}
	}
	
	private static String SERVICE_PRE_PRODUCTION_URL_BASE = "http://openreact.alwaysdata.net/api";
	private static String INCIDENTS_JSON_URL = "/incidents.json/";
	private static String LIGNES_JSON_URL = "/ligne";

	private IncidentsTransportsBackgroundServiceBinder mBinder;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return this.mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.mBinder = new IncidentsTransportsBackgroundServiceBinder(this);
	}
	
	  @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}		
	
	private String getIncidentsEnCoursFromService(String scope) {
		return getFromService(new HttpGet(SERVICE_PRE_PRODUCTION_URL_BASE + INCIDENTS_JSON_URL));
	}
	
	private String getLignesFromService() {
		return getFromService(new HttpGet(SERVICE_PRE_PRODUCTION_URL_BASE + LIGNES_JSON_URL));
	}
	
	private String getFromService(HttpGet request) {
		HttpClient httpclient = new DefaultHttpClient();

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

	@Override
	public void startGetIncidentsAsync(String scope) {
		new LoadIncidentsAsyncTask().execute(scope);				
	}

	private List<IIncidentsTransportsBackgroundServiceListener> listeners = null; 
	 
	// Ajout d'un listener 
	public void addListener(IIncidentsTransportsBackgroundServiceListener listener) { 
	    if(listeners == null){ 
	        listeners = new ArrayList<IIncidentsTransportsBackgroundServiceListener>(); 
	    } 
	    listeners.add(listener); 
	} 
	 
	// Suppression d'un listener 
	public void removeListener(IIncidentsTransportsBackgroundServiceListener listener) { 
	    if(listeners != null){ 
	        listeners.remove(listener); 
	    } 
	} 
	 
	// Notification des listeners 
	private void fireDataChanged(Object data){ 
	    if(listeners != null){ 
	        for(IIncidentsTransportsBackgroundServiceListener listener: listeners){ 
	            listener.dataChanged(data); 
	        } 
	    } 
	}

	@Override
	public void startGetLignesAsync() {
		// TODO Auto-generated method stub
		
	}


}
