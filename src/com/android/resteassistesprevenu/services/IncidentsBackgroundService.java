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

public class IncidentsBackgroundService extends Service implements IIncidentsBackgroundService {

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

	@Override
	public void startGetIncidentsAsync(String scope) {
		new LoadIncidentsAsyncTask().execute(scope);				
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
