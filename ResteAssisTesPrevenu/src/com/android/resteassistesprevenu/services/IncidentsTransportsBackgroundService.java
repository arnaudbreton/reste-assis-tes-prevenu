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
import com.android.resteassistesprevenu.model.LigneModel;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetLignesListener;

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
			fireIncidentsChanged(result);
		}
	}
	
	/**
	 * AsyncTask de récupération des incidents
	 *
	 */
	private class LoadLignesAsyncTask extends AsyncTask<Void, Void, List<LigneModel>> {

		@Override
		protected List<LigneModel> doInBackground(Void... params) {		
			try {			
				return LigneModel.deserializeArray(getLignesFromService());
			} catch (Exception e) {
				Log.e("ResteAssisTesPrevenu", "Erreur au chargement des incidents par le service", e);
				return new ArrayList<LigneModel>();
			}
		}
		
		@Override
		protected void onPostExecute(List<LigneModel> result) {
			super.onPostExecute(result);
			fireLignesChanged(result);
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
		
		this.mBinder = null;
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

	private List<IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener> getIncidentslisteners = null; 
	 
	// Ajout d'un listener 
	public void addGetIncidentsListener(IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener) { 
	    if(getIncidentslisteners == null){ 
	    	getIncidentslisteners = new ArrayList<IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener>(); 
	    } 
	    getIncidentslisteners.add(listener); 
	} 
	 
	// Suppression d'un listener 
	public void removeGetIncidentsListener(IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener) { 
	    if(getIncidentslisteners != null){ 
	    	getIncidentslisteners.remove(listener); 
	    } 
	} 
	 
	// Notification des listeners 
	private void fireIncidentsChanged(List<IncidentModel> data){ 
	    if(getIncidentslisteners != null){ 
	        for(IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener: getIncidentslisteners){ 
	            listener.dataChanged(data); 
	        } 
	    } 
	}
	
	private List<IIncidentsTransportsBackgroundServiceGetLignesListener> getLigneslisteners = null; 
	 
	// Ajout d'un listener 
	public void addGetLignesListener(IIncidentsTransportsBackgroundServiceGetLignesListener listener) { 
	    if(getLigneslisteners == null){ 
	    	getLigneslisteners = new ArrayList<IIncidentsTransportsBackgroundServiceGetLignesListener>(); 
	    } 
	    getLigneslisteners.add(listener); 
	} 
	 
	// Suppression d'un listener 
	public void removeGetLignesListener(IIncidentsTransportsBackgroundServiceGetLignesListener listener) { 
	    if(getLigneslisteners != null){ 
	    	getLigneslisteners.remove(listener); 
	    } 
	} 
	 
	// Notification des listeners 
	private void fireLignesChanged(List<LigneModel> data){ 
	    if(getLigneslisteners != null){ 
	        for(IIncidentsTransportsBackgroundServiceGetLignesListener listener: getLigneslisteners){ 
	            listener.dataChanged(data); 
	        } 
	    } 
	}

	@Override
	public void startGetLignesAsync() {
		new LoadLignesAsyncTask().execute();		
	}


}
