package com.resteassistesprevenu.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
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

import com.resteassistesprevenu.R;
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

/**
 * Service de communication avec les WebServices et le ContentProvider
 * 
 * @author Arnaud
 * 
 */
public class IncidentsTransportsBackgroundService extends Service implements
		IIncidentsTransportsBackgroundService {
	private static final String TAG_SERVICE = "IncidentsTransportsBackgroundService";

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
				Log.e(getString(R.string.log_tag_name),
						"Erreur au chargement des incidents par le service", e);
				return null;
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
				return getTypeLignesFromProvider();
			} catch (Exception e) {
				Log.e(getString(R.string.log_tag_name),
						"Erreur au chargement des types de ligne", e);
				return null;
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
				Log.e(getString(R.string.log_tag_name),
						"Erreur au chargement des lignes du type " + params[0]
								+ " par le service.", e);
				return null;
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
				Log.e(getString(R.string.log_tag_name),
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
				Log.e(getString(R.string.log_tag_name),
						"Erreur lors du chargement des favoris par le service",
						e);
				return null;
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
				Log.e(getString(R.string.log_tag_name),
						"Erreur lors de l'enregistrement du favoris "
								+ params[0], e);
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
				Log.e(getString(R.string.log_tag_name),
						"Erreur lors du vote pour l'incident n°" + params[0]
								+ " action : " + params[1], e);
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

	private final static int TIMEOUT_CONNECTION = 10000;
	private final static int TIMEOUT_SOCKET = 5000;

	private final static String SERVICE_URL_BASE_PRE_PRODUCTION = "http://openreact.alwaysdata.net/";
	private final static String SERVICE_URL_BASE_PRODUCTION = "http://www.incidents-transports.com/";

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
		
		if(this.getFavorislisteners != null) 
			this.getFavorislisteners.clear();
		
		if(this.getIncidentslisteners != null)
			this.getIncidentslisteners.clear();
		
		if(this.getLigneslisteners != null)
			this.getLigneslisteners.clear();
		
		if(this.getReportNewIncidentlisteners != null)
			this.getReportNewIncidentlisteners.clear();
		
		if(this.getTypeLigneslisteners != null)
			this.getTypeLigneslisteners.clear();
		
		if(this.getVoteIncidentlisteners != null)
			this.getVoteIncidentlisteners.clear();
	}

	private String getIncidentsEnCoursFromService(String scope)
			throws IOException {
		return requestToService(new HttpGet(this.urlService + "/api" + INCIDENTS_JSON_URL
				+ "/" + scope));
	}

	/**
	 * Récupération des types de ligne.
	 * 
	 * @return Les types de lignes
	 */
	private List<String> getTypeLignesFromProvider() {
		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Début récupération type lignes");
		ContentResolver cr = getContentResolver();
		String[] projection = new String[] { TypeLigneBaseColumns.TYPE_LIGNE };
		Log.d(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Envoi d'une requête au service");
		Cursor c = cr.query(
				Uri.parse(DefaultContentProvider.CONTENT_URI + "/type_lignes"),
				projection, null, null, null);

		ArrayList<String> lignes = new ArrayList<String>();
		if (c.moveToFirst()) {
			do {
				Log.d(getApplicationContext().getString(R.string.log_tag_name)
						+ " " + TAG_SERVICE,
						"Ajout de la ligne : "
								+ c.getString(c
										.getColumnIndex(TypeLigneBaseColumns.TYPE_LIGNE)));
				lignes.add(c.getString(c
						.getColumnIndex(TypeLigneBaseColumns.TYPE_LIGNE)));
			} while (c.moveToNext());
		}
		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Fin récupération type lignes");
		return lignes;
	}

	/**
	 * Récupération des lignes.
	 * 
	 * @return Une liste de modèle de ligne
	 */
	private List<LigneModel> getLignesFromProvider(String typeLigne) {
		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Début récupération lignes");
		ContentResolver cr = getContentResolver();
		String[] projection = new String[] {
				LigneBaseColumns.NOM_TABLE.concat("."
						.concat(LigneBaseColumns._ID)),
				TypeLigneBaseColumns.TYPE_LIGNE, LigneBaseColumns.NOM_LIGNE,
				LigneBaseColumns.IS_FAVORIS };
		String selection = null;

		if (!typeLigne.equals("")) {
			Log.d(getApplicationContext().getString(R.string.log_tag_name)
					+ " " + TAG_SERVICE, "Ligne du type : " + typeLigne);
			selection = "type_ligne = '" + typeLigne + "'";
		}

		Log.d(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Envoi d'une requête au ContentProvider");
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
				Log.d(getApplicationContext().getString(R.string.log_tag_name)
						+ " " + TAG_SERVICE, "Ajout de la ligne : " + ligne);
				lignes.add(ligne);
			} while (c.moveToNext());
		}
		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Fin récupération lignes");
		return lignes;
	}

	/**
	 * Récupération des favoris, du provider
	 * 
	 * @return Les lignes favorites
	 */
	private List<LigneModel> getFavorisFromProvider() {
		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Début récupération des favoris");
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
				Log.d(getApplicationContext().getString(R.string.log_tag_name)
						+ " " + TAG_SERVICE, "Ajout de la ligne : " + ligne);
				lignes.add(ligne);
			} while (c.moveToNext());
		}
		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Fin récupération des favoris");
		return lignes;
	}

	/**
	 * Création d'un incident
	 * 
	 * @return Le numéro d'incident
	 * @throws IOException
	 */
	private String createIncident(String typeLigne, String numLigne,
			String raison) throws JSONException, IOException {
		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Début de création d'un incident.");
		HttpPost request = new HttpPost(this.urlService + "/api/incident");

		JSONObject json = new JSONObject();
		json.put("line_name", typeLigne + " " + numLigne);
		json.put("reason", raison);
		json.put("source", getString(R.string.log_tag_name));

		Log.d(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Incident JSON : " + json);

		request.setHeader("Content-Type", "application/json");
		request.setEntity(new StringEntity(json.toString(), HTTP.UTF_8));

		request.setHeader("Accept", "application/json;charset=UTF-8");

		String result = requestToService(request);
		Log.d(getString(R.string.log_tag_name), "Résultat du serveur :"
				+ result);
		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Fin de création d'un incident.");
		return result;
	}

	/**
	 * Vote pour un incident
	 * 
	 * @param incidentId
	 *            Numéro de l'incident
	 * @param action
	 *            Action à réaliser (plus, moins, end)
	 * @return Vrai si tout s'est bien passé, faux sinon
	 * @throws UnsupportedEncodingException
	 * @throws JSONException
	 */
	private Boolean voteIncident(int incidentId, String action)
			throws UnsupportedEncodingException, JSONException {
		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Début de vote pour un incident");

		String url;
		url = this.urlService  + "/api" + INCIDENT_JSON_URL + "/vote/" + incidentId + "/"
				+ action;

		HttpPost request = new HttpPost(url);

		try {
			requestToService(request);
			Log.i(getApplicationContext().getString(R.string.log_tag_name)
					+ " " + TAG_SERVICE, "Fin de vote pour un incident");
			return true;
		} catch (Exception e) {
			Log.e(getApplicationContext().getString(R.string.log_tag_name)
					+ " " + TAG_SERVICE, "Vote pour un incident en erreur", e);
			return false;
		}

	}

	/**
	 * Enregistrement d'un favoris
	 * 
	 * @param ligne
	 *            La ligne à mettre à jour
	 */
	public void registerFavoris(LigneModel ligne) {
		ContentResolver cr = getContentResolver();
		ContentValues editedValues = new ContentValues();

		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Début enregistrement d'un favoris");
		editedValues
				.put(LigneBaseColumns.IS_FAVORIS, ligne.isFavoris() ? 1 : 0);
		Log.d(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Mise à jour d'un favoris : " + ligne + " "
				+ ligne.isFavoris());
		cr.update(
				Uri.parse(DefaultContentProvider.CONTENT_URI + "/favoris/"
						+ ligne.getId()), editedValues, null, null);
		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Fin enregistrement d'un favoris");
	}

	/**
	 * Envoi d'une requête HTTP au serveur
	 * 
	 * @param request
	 *            La requête à envoyer
	 * @return La réponse du serveur
	 * @throws IOException
	 */
	private String requestToService(HttpUriRequest request) throws IOException {
		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Début de requête.");	

		String result = null;
		
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);
		
		HttpClient httpclient = new DefaultHttpClient(httpParameters);

		ResponseHandler<String> handler = new BasicResponseHandler();
		result = httpclient.execute(request, handler);
		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Fin de requête : " + result);

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
	
	@Override
	public boolean onUnbind(Intent intent) {
		if(this.getFavorislisteners != null && this.getFavorislisteners.contains(intent)) {
			this.getFavorislisteners.remove(intent);
		}
		
		if(this.getIncidentslisteners != null && this.getIncidentslisteners.contains(intent)) {
			this.getIncidentslisteners.remove(intent);
		}
		
		if(this.getLigneslisteners != null && this.getLigneslisteners.contains(intent)) {
			this.getLigneslisteners.remove(intent);
		}
		
		if(this.getReportNewIncidentlisteners != null && this.getReportNewIncidentlisteners.contains(intent)) {
			this.getReportNewIncidentlisteners.remove(intent);
		}
		
		if(this.getTypeLigneslisteners != null && this.getTypeLigneslisteners.contains(intent)) {
			this.getTypeLigneslisteners.remove(intent);
		}
		
		if(this.getVoteIncidentlisteners != null && this.getVoteIncidentlisteners.contains(intent)) {
			this.getVoteIncidentlisteners.remove(intent);
		}
		
		return true;
	}
	
	@Override
	public String getUrlService() {
		return urlService;
	}
}
