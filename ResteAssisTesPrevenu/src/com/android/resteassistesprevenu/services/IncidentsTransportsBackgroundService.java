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
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.android.resteassistesprevenu.model.IncidentModel;
import com.android.resteassistesprevenu.provider.DefaultContentProvider;
import com.android.resteassistesprevenu.provider.LigneBaseColumns;
import com.android.resteassistesprevenu.provider.TypeLigneBaseColumns;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetLignesListener;
import com.android.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetTypeLignesListener;

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
	 * AsyncTask de récupération des types de lignes
	 *
	 */
	private class LoadTypeLignesAsyncTask extends AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... params) {		
			try {			
				return getTypeLignesFromService();
			} catch (Exception e) {
				Log.e("ResteAssisTesPrevenu", "Erreur au chargement des incidents par le service", e);
				return new ArrayList<String>();
			}
		}
		
		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			fireTypeLignesChanged(result);
		}
	}
	
	/**
	 * AsyncTask de récupération des types de lignes
	 *
	 */
	private class LoadLignesAsyncTask extends AsyncTask<String, Void, List<String>> {

		@Override
		protected List<String> doInBackground(String... params) {		
			try {			
				return getLignesFromService(params[0]);
			} catch (Exception e) {
				Log.e("ResteAssisTesPrevenu", "Erreur au chargement des lignes du type " + params[0] + " par le service.", e);
				return new ArrayList<String>();
			}
		}
		
		@Override
		protected void onPostExecute(List<String> result) {
			super.onPostExecute(result);
			fireLignesChanged(result);
		}
	}
	
	/**
	 * AsyncTask de report d'un incident
	 *
	 */
	private class ReportIncidentAsyncTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {		
			try {			
				return null;
			} catch (Exception e) {
				Log.e("ResteAssisTesPrevenu", "Erreur lors de la création de l'incident", e);
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			//fireLignesChanged(result);
		}
	}
	
	private static String SERVICE_PRE_PRODUCTION_URL_BASE = "http://openreact.alwaysdata.net/api";
	private static String INCIDENTS_JSON_URL = "/incidents.json/";

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
	
	private List<String> getTypeLignesFromService() {
		ContentResolver cr = getContentResolver();
		String[] projection = new String[] { TypeLigneBaseColumns.TYPE_LIGNE };
		Cursor c = cr.query(Uri.parse(DefaultContentProvider.CONTENT_URI + "/type_lignes"), projection, null, null, null);
		
		ArrayList<String> lignes = new ArrayList<String>();
		if(c.moveToFirst()) {
			do {
				lignes.add(c.getString(c.getColumnIndex(TypeLigneBaseColumns.TYPE_LIGNE)));
			}while(c.moveToNext());
		}
		return lignes;
	}
	
	private List<String> getLignesFromService(String typeLigne) {
		ContentResolver cr = getContentResolver();
		String[] projection = new String[] { LigneBaseColumns.NOM_LIGNE };
		String selection = "type_ligne = '" + typeLigne + "'";
		Cursor c = cr.query(Uri.parse(DefaultContentProvider.CONTENT_URI + "/lignes"), projection, selection, null, null);
		
		ArrayList<String> lignes = new ArrayList<String>();
		if(c.moveToFirst()) {
			do {
				lignes.add(c.getString(c.getColumnIndex(LigneBaseColumns.NOM_LIGNE)));
			}while(c.moveToNext());
		}
		return lignes;
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
	
	@Override
	public void startGetTypeLignesAsync() {
		new LoadTypeLignesAsyncTask().execute();		
	}
	
	private List<IIncidentsTransportsBackgroundServiceGetTypeLignesListener> getTypeLigneslisteners = null; 
	 
	// Ajout d'un listener 
	public void addGetTypeLignesListener(IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener) { 
	    if(getTypeLigneslisteners == null){ 
	    	getTypeLigneslisteners = new ArrayList<IIncidentsTransportsBackgroundServiceGetTypeLignesListener>(); 
	    } 
	    getTypeLigneslisteners.add(listener); 
	} 
	 
	// Suppression d'un listener 
	public void removeGetTypeLignesListener(IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener) { 
	    if(getTypeLigneslisteners != null){ 
	    	getTypeLigneslisteners.remove(listener); 
	    } 
	} 
	 
	// Notification des listeners 
	private void fireTypeLignesChanged(List<String> data){ 
	    if(getTypeLigneslisteners != null){ 
	        for(IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener: getTypeLigneslisteners){ 
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
	private void fireLignesChanged(List<String> data){ 
	    if(getLigneslisteners != null){ 
	        for(IIncidentsTransportsBackgroundServiceGetLignesListener listener: getLigneslisteners){ 
	            listener.dataChanged(data); 
	        } 
	    } 
	}

	@Override
	public void startGetLignesAsync(String typeLigne) {
		new LoadLignesAsyncTask().execute(typeLigne);		
	}

	@Override
	public void startReportIncident(String typeLigne, String numLigne,
			String raison) {		
	}


}
