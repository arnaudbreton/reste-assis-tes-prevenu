package com.resteassistesprevenu.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.resteassistesprevenu.model.IncidentAction;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.model.LigneModel;
import com.resteassistesprevenu.provider.DefaultContentProvider;
import com.resteassistesprevenu.provider.LigneBaseColumns;
import com.resteassistesprevenu.provider.TypeLigneBaseColumns;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetFavorisListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetLignesListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetTypeLignesListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceReportNewIncidentListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceVoteIncidentListener;

public class IncidentsTransportsBackgroundService extends Service implements
		IIncidentsTransportsBackgroundService {

	/**
	 * AsyncTask de récupération des incidents
	 * 
	 */
	private class LoadIncidentsAsyncTask extends
			AsyncTask<String, Void, List<IncidentModel>> {

		@Override
		protected List<IncidentModel> doInBackground(String... params) {
			try {
				return IncidentModel
						.deserializeFromArray(getIncidentsEnCoursFromService(params[0]));
			} catch (Exception e) {
				Log.e("ResteAssisTesPrevenu",
						"Erreur au chargement des incidents par le service", e);
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
	private class LoadTypeLignesAsyncTask extends
			AsyncTask<Void, Void, List<String>> {

		@Override
		protected List<String> doInBackground(Void... params) {
			try {
				return getTypeLignesFromService();
			} catch (Exception e) {
				Log.e("ResteAssisTesPrevenu",
						"Erreur au chargement des incidents par le service", e);
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
	 * AsyncTask de récupération des lignes
	 * 
	 */
	private class LoadLignesAsyncTask extends
			AsyncTask<String, Void, List<LigneModel>> {

		@Override
		protected List<LigneModel> doInBackground(String... params) {
			try {
				if (params.length == 0) {
					return getLignesFromProvider("");
				} else {
					return getLignesFromProvider(params[0]);
				}

			} catch (Exception e) {
				Log.e("ResteAssisTesPrevenu",
						"Erreur au chargement des lignes du type " + params[0]
								+ " par le service.", e);
				return new ArrayList<LigneModel>();
			}
		}

		@Override
		protected void onPostExecute(List<LigneModel> result) {
			super.onPostExecute(result);
			fireLignesChanged(result);
		}
	}

	/**
	 * AsyncTask de report d'un incident
	 * 
	 */
	private class ReportIncidentAsyncTask extends
			AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			try {
				return createIncident(params[0], params[1], params[2]);
			} catch (Exception e) {
				Log.e("ResteAssisTesPrevenu",
						"Erreur lors de la création de l'incident", e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			fireReportNewIncidentChanged(result);
		}
	}
	
	/**
	 * AsyncTask de récupération des favoris
	 * 
	 */
	private class GetFavorisAsyncTask extends
			AsyncTask<Void, Void, List<LigneModel>> {

		@Override
		protected List<LigneModel> doInBackground(Void... params) {
			try {
				return getFavorisFromProvider();				
			} catch (Exception e) {
				Log.e("ResteAssisTesPrevenu",
						"Erreur au chargement des lignes du type " + params[0]
								+ " par le service.", e);
				return new ArrayList<LigneModel>();
			}
		}

		@Override
		protected void onPostExecute(List<LigneModel> result) {
			super.onPostExecute(result);
			fireFavorisChanged(result);
		}
	}

	/**
	 * AsyncTask d'enregistrement d'un favoris
	 * 
	 */
	private class RegisterFavorisAsyncTask extends
			AsyncTask<LigneModel, Void, Void> {

		@Override
		protected Void doInBackground(LigneModel... params) {
			try {
				registerFavoris(params[0]);
			} catch (Exception e) {
				Log.e("ResteAssisTesPrevenu",
						"Erreur lors de la création de l'incident", e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// fireReportNewIncidentChanged(result);
		}
	}

	/**
	 * AsyncTask de report d'un incident
	 * 
	 */
	private class VoteIncidentAsyncTask extends
			AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				return voteIncident(Integer.parseInt(params[0]), params[1]);
			} catch (Exception e) {
				Log.e("ResteAssisTesPrevenu",
						"Erreur lors de la création de l'incident", e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			fireVoteIncidentChanged(result);
		}
	}

	private String urlService;

	private final static String SERVICE_URL_BASE_PRE_PRODUCTION = "http://openreact.alwaysdata.net/api";
	private final static String SERVICE_URL_BASE_PRODUCTION = "http://www.incidents-transports.com/api";
	
	private static String INCIDENTS_JSON_URL = "/incidents.json";
	private static String INCIDENT_JSON_URL = "/incident.json";
	
	private IncidentsTransportsBackgroundServiceBinder mBinder;

	@Override
	public IBinder onBind(Intent arg0) {
		return this.mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.mBinder = new IncidentsTransportsBackgroundServiceBinder(this);
		this.urlService = SERVICE_URL_BASE_PRODUCTION;
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
		return getFromService(new HttpGet(this.urlService + INCIDENTS_JSON_URL + "/"
				+ scope));
	}

	private List<String> getTypeLignesFromService() {
		ContentResolver cr = getContentResolver();
		String[] projection = new String[] { TypeLigneBaseColumns.TYPE_LIGNE };
		Cursor c = cr.query(
				Uri.parse(DefaultContentProvider.CONTENT_URI + "/type_lignes"),
				projection, null, null, null);

		ArrayList<String> lignes = new ArrayList<String>();
		if (c.moveToFirst()) {
			do {
				lignes.add(c.getString(c
						.getColumnIndex(TypeLigneBaseColumns.TYPE_LIGNE)));
			} while (c.moveToNext());
		}
		return lignes;
	}

	private List<LigneModel> getLignesFromProvider(String typeLigne) {
		ContentResolver cr = getContentResolver();
		String[] projection = new String[] {
				LigneBaseColumns.NOM_TABLE.concat("."
						.concat(LigneBaseColumns._ID)),
				TypeLigneBaseColumns.TYPE_LIGNE, LigneBaseColumns.NOM_LIGNE,
				LigneBaseColumns.IS_FAVORIS };
		String selection = null;

		if (!typeLigne.equals("")) {
			selection = "type_ligne = '" + typeLigne + "'";
		}

		Cursor c = cr.query(
				Uri.parse(DefaultContentProvider.CONTENT_URI + "/lignes"),
				projection, selection, null, null);

		ArrayList<LigneModel> lignes = new ArrayList<LigneModel>();
		if (c.moveToFirst()) {
			do {
				LigneModel ligne = new LigneModel(
						c.getInt(c.getColumnIndex(LigneBaseColumns._ID)),
						c.getString(c
								.getColumnIndex(TypeLigneBaseColumns.TYPE_LIGNE)),
						c.getString(c
								.getColumnIndex(LigneBaseColumns.NOM_LIGNE)),
						c.getInt(c.getColumnIndex(LigneBaseColumns.IS_FAVORIS)) != 0);
				lignes.add(ligne);
			} while (c.moveToNext());
		}
		return lignes;
	}
	
	private List<LigneModel> getFavorisFromProvider() {
		ContentResolver cr = getContentResolver();
		String[] projection = new String[] {
				LigneBaseColumns.NOM_TABLE.concat("."
						.concat(LigneBaseColumns._ID)),
				TypeLigneBaseColumns.TYPE_LIGNE, LigneBaseColumns.NOM_LIGNE,
				LigneBaseColumns.IS_FAVORIS };
		String selection = null;		
			
		Cursor c = cr.query(
				Uri.parse(DefaultContentProvider.CONTENT_URI + "/favoris"),
				projection, selection, null, null);

		ArrayList<LigneModel> lignes = new ArrayList<LigneModel>();
		if (c.moveToFirst()) {
			do {
				LigneModel ligne = new LigneModel(
						c.getInt(c.getColumnIndex(LigneBaseColumns._ID)),
						c.getString(c
								.getColumnIndex(TypeLigneBaseColumns.TYPE_LIGNE)),
						c.getString(c
								.getColumnIndex(LigneBaseColumns.NOM_LIGNE)),
						c.getInt(c.getColumnIndex(LigneBaseColumns.IS_FAVORIS)) != 0);
				lignes.add(ligne);
			} while (c.moveToNext());
		}
		return lignes;
	}

	private String createIncident(String typeLigne, String numLigne,
			String raison) throws UnsupportedEncodingException, JSONException {
		HttpPost request = new HttpPost(this.urlService + "/incident");

		JSONObject json = new JSONObject();
		json.put("line_name", typeLigne + " " + numLigne);
		json.put("reason", raison);
		json.put("source", "ResteAssisTesPrevenu");

		request.setHeader("Content-Type", "application/json; charset=UTF-8");
		request.setEntity(new StringEntity(json.toString()));

		request.setHeader("Accept", "application/json");

		String result = postToService(request);
		Log.i("ResteAssisTesPrevenu", result);

		return result;
	}

	private Boolean voteIncident(int incidentId, String action)
			throws UnsupportedEncodingException, JSONException {
		String url;

		url = this.urlService + INCIDENT_JSON_URL + "/vote/" + incidentId + "/" + action;
		
		HttpPost request = new HttpPost(url);
		
		try {
			postToService(request);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void registerFavoris(LigneModel ligne) {
		ContentResolver cr = getContentResolver();
		ContentValues editedValues = new ContentValues();

		editedValues
				.put(LigneBaseColumns.IS_FAVORIS, ligne.isFavoris() ? 1 : 0);

		cr.update(
				Uri.parse(DefaultContentProvider.CONTENT_URI + "/favoris/"
						+ ligne.getId()), editedValues, null, null);
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

	private String postToService(HttpPost request) {
		HttpClient httpclient = new DefaultHttpClient();

		String result = null;

		ResponseHandler<String> handler = new BasicResponseHandler();
		try {
			result = httpclient.execute(request, handler);
			Log.i("ResteAssisTesPrevenu : ", result);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		httpclient.getConnectionManager().shutdown();

		return result;
	}

	@Override
	public void startGetIncidentsAsync(String scope) {
		new LoadIncidentsAsyncTask().execute(scope);
	}

	private List<IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener> getIncidentslisteners = null;

	// Ajout d'un listener
	public void addGetIncidentsListener(
			IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener) {
		if (getIncidentslisteners == null) {
			getIncidentslisteners = new ArrayList<IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener>();
		}
		getIncidentslisteners.add(listener);
	}

	// Suppression d'un listener
	public void removeGetIncidentsListener(
			IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener) {
		if (getIncidentslisteners != null) {
			getIncidentslisteners.remove(listener);
		}
	}

	// Notification des listeners
	private void fireIncidentsChanged(List<IncidentModel> data) {
		if (getIncidentslisteners != null) {
			for (IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener listener : getIncidentslisteners) {
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
	public void addGetTypeLignesListener(
			IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener) {
		if (getTypeLigneslisteners == null) {
			getTypeLigneslisteners = new ArrayList<IIncidentsTransportsBackgroundServiceGetTypeLignesListener>();
		}
		getTypeLigneslisteners.add(listener);
	}

	// Suppression d'un listener
	public void removeGetTypeLignesListener(
			IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener) {
		if (getTypeLigneslisteners != null) {
			getTypeLigneslisteners.remove(listener);
		}
	}

	// Notification des listeners
	private void fireTypeLignesChanged(List<String> data) {
		if (getTypeLigneslisteners != null) {
			for (IIncidentsTransportsBackgroundServiceGetTypeLignesListener listener : getTypeLigneslisteners) {
				listener.dataChanged(data);
			}
		}
	}

	private List<IIncidentsTransportsBackgroundServiceGetLignesListener> getLigneslisteners = null;

	// Ajout d'un listener
	public void addGetLignesListener(
			IIncidentsTransportsBackgroundServiceGetLignesListener listener) {
		if (getLigneslisteners == null) {
			getLigneslisteners = new ArrayList<IIncidentsTransportsBackgroundServiceGetLignesListener>();
		}
		getLigneslisteners.add(listener);
	}

	// Suppression d'un listener
	public void removeGetLignesListener(
			IIncidentsTransportsBackgroundServiceGetLignesListener listener) {
		if (getLigneslisteners != null) {
			getLigneslisteners.remove(listener);
		}
	}

	// Notification des listeners
	private void fireLignesChanged(List<LigneModel> data) {
		if (getLigneslisteners != null) {
			for (IIncidentsTransportsBackgroundServiceGetLignesListener listener : getLigneslisteners) {
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
		new ReportIncidentAsyncTask().execute(typeLigne, numLigne, raison);
	}

	private List<IIncidentsTransportsBackgroundServiceReportNewIncidentListener> getReportNewIncidentlisteners = null;

	// Ajout d'un listener
	public void addReportNewIncidentListener(
			IIncidentsTransportsBackgroundServiceReportNewIncidentListener listener) {
		if (getReportNewIncidentlisteners == null) {
			getReportNewIncidentlisteners = new ArrayList<IIncidentsTransportsBackgroundServiceReportNewIncidentListener>();
		}
		getReportNewIncidentlisteners.add(listener);
	}

	// Suppression d'un listener
	public void removeReportNewIncidentListener(
			IIncidentsTransportsBackgroundServiceReportNewIncidentListener listener) {
		if (getReportNewIncidentlisteners != null) {
			getReportNewIncidentlisteners.remove(listener);
		}
	}

	// Notification des listeners
	private void fireReportNewIncidentChanged(String data) {
		if (getReportNewIncidentlisteners != null) {
			for (IIncidentsTransportsBackgroundServiceReportNewIncidentListener listener : getReportNewIncidentlisteners) {
				listener.dataChanged(data);
			}
		}
	}

	@Override
	public void startVoteIncident(int idIncident, IncidentAction action) {
		new VoteIncidentAsyncTask().execute(String.valueOf(idIncident),
				action.toString());
	}

	private List<IIncidentsTransportsBackgroundServiceVoteIncidentListener> getVoteIncidentlisteners = null;

	// Ajout d'un listener
	public void addVoteIncidentListener(
			IIncidentsTransportsBackgroundServiceVoteIncidentListener listener) {
		if (getVoteIncidentlisteners == null) {
			getVoteIncidentlisteners = new ArrayList<IIncidentsTransportsBackgroundServiceVoteIncidentListener>();
		}
		getVoteIncidentlisteners.add(listener);
	}

	// Suppression d'un listener
	public void removeVoteIncidentListener(
			IIncidentsTransportsBackgroundServiceVoteIncidentListener listener) {
		if (getVoteIncidentlisteners != null) {
			getVoteIncidentlisteners.remove(listener);
		}
	}

	// Notification des listeners
	private void fireVoteIncidentChanged(boolean voteSent) {
		if (getVoteIncidentlisteners != null) {
			for (IIncidentsTransportsBackgroundServiceVoteIncidentListener listener : getVoteIncidentlisteners) {
				listener.dataChanged(voteSent);
			}
		}
	}

	public void startRegisterFavoris(LigneModel ligne) {
		new RegisterFavorisAsyncTask().execute(ligne);
	}
	
	public void startGetFavorisAsync() {
		new GetFavorisAsyncTask().execute();
	}

	private List<IIncidentsTransportsBackgroundServiceGetFavorisListener> getFavorislisteners = null;

	// Ajout d'un listener
	public void addGetFavorisListener(
			IIncidentsTransportsBackgroundServiceGetFavorisListener listener) {
		if (getFavorislisteners == null) {
			getFavorislisteners = new ArrayList<IIncidentsTransportsBackgroundServiceGetFavorisListener>();
		}
		getFavorislisteners.add(listener);
	}

	// Suppression d'un listener
	public void removeGetFavorisListener(
			IIncidentsTransportsBackgroundServiceGetFavorisListener listener) {
		if (getFavorislisteners != null) {
			getFavorislisteners.remove(listener);
		}
	}

	// Notification des listeners
	private void fireFavorisChanged(List<LigneModel> data) {
		if (getFavorislisteners != null) {
			for (IIncidentsTransportsBackgroundServiceGetFavorisListener listener : getFavorislisteners) {
				listener.dataChanged(data);
			}
		}
	}

	@Override
	public boolean isProduction() {
		return this.urlService.equals(SERVICE_URL_BASE_PRODUCTION);
	}

	@Override
	public void setProduction(boolean isProduction) {
		if (isProduction) {
			this.urlService = SERVICE_URL_BASE_PRODUCTION;
		} else {
			this.urlService = SERVICE_URL_BASE_PRE_PRODUCTION;
		}
	}
}
