package com.resteassistesprevenu.services;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
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
import com.resteassistesprevenu.provider.IncidentsBDDHelper;
import com.resteassistesprevenu.provider.LigneBDDHelper;
import com.resteassistesprevenu.provider.TypeLigneBDDHelper;
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

	private static final Object lockObject = new Object();

	/**
	 * AsyncTask de récupération des incidents
	 * 
	 */
	private class LoadIncidentsAsyncTask extends
			AsyncTask<String, Void, List<IncidentModel>> {

		private IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener callback;

		public LoadIncidentsAsyncTask(
				IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener callback) {
			this.callback = callback;
		}

		@Override
		protected List<IncidentModel> doInBackground(String... params) {
			try {
				synchronized (lockObject) {
					return getIncidentsEnCoursFromProviderOrService(params[0]);
				}
			} catch (Exception e) {
				Log.e(getString(R.string.log_tag_name),
						"Erreur au chargement des incidents par le service", e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<IncidentModel> result) {
			super.onPostExecute(result);

			if (this.callback != null) {
				this.callback.dataChanged(result);
			}
		}
	}

	/**
	 * AsyncTask de récupération des types de lignes
	 * 
	 */
	private class LoadTypeLignesAsyncTask extends
			AsyncTask<Void, Void, List<String>> {
		private IIncidentsTransportsBackgroundServiceGetTypeLignesListener callback;

		public LoadTypeLignesAsyncTask(
				IIncidentsTransportsBackgroundServiceGetTypeLignesListener callback) {
			this.callback = callback;
		}

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
			this.callback.dataChanged(result);
		}
	}

	/**
	 * AsyncTask de récupération des lignes
	 * 
	 */
	private class LoadLignesAsyncTask extends
			AsyncTask<String, Void, List<LigneModel>> {
		private IIncidentsTransportsBackgroundServiceGetLignesListener callback;

		public LoadLignesAsyncTask(
				IIncidentsTransportsBackgroundServiceGetLignesListener callback) {
			this.callback = callback;
		}

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
			this.callback.dataChanged(result);
		}
	}

	/**
	 * AsyncTask de report d'un incident
	 * 
	 */
	private class ReportIncidentAsyncTask extends
			AsyncTask<String, Void, String> {
		private IIncidentsTransportsBackgroundServiceReportNewIncidentListener callback;

		public ReportIncidentAsyncTask(
				IIncidentsTransportsBackgroundServiceReportNewIncidentListener callback) {
			this.callback = callback;
		}

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
			this.callback.dataChanged(result);
		}
	}

	/**
	 * AsyncTask de récupération des favoris
	 * 
	 */
	private class GetFavorisAsyncTask extends
			AsyncTask<Void, Void, List<LigneModel>> {
		private IIncidentsTransportsBackgroundServiceGetFavorisListener callback;

		public GetFavorisAsyncTask(
				IIncidentsTransportsBackgroundServiceGetFavorisListener callback) {
			this.callback = callback;
		}

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
			this.callback.dataChanged(result);
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
			AsyncTask<IncidentAction, Void, Boolean> {

		private IIncidentsTransportsBackgroundServiceVoteIncidentListener callback;
		private IncidentModel incident;
		private IncidentAction action;

		public VoteIncidentAsyncTask(
				IncidentModel incident,
				IIncidentsTransportsBackgroundServiceVoteIncidentListener callback) {
			this.incident = incident;
			this.callback = callback;
		}

		@Override
		protected Boolean doInBackground(IncidentAction... params) {
			try {
				this.action = params[0];
				return voteIncident(this.incident.getId(), this.action);
			} catch (Exception e) {
				Log.e(getString(R.string.log_tag_name),
						"Erreur lors du vote pour l'incident n°"
								+ this.incident.getId() + " action : "
								+ params[0], e);
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result) {
				switch (this.action) {
				case VOTE_PLUS:
					this.incident.addVotePlus();
					break;
				case VOTE_MINUS:
					this.incident.addVoteMinus();
					break;
				case VOTE_END:
					this.incident.addVoteEnded();
					break;

				default:
					break;
				}
			}

			this.callback.dataChanged(result);
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

	/**
	 * Dernière mise à jour des données
	 */
	private long lastTimeUpdate;

	@Override
	public IBinder onBind(Intent arg0) {
		return this.mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		this.mBinder = new IncidentsTransportsBackgroundServiceBinder(this);
		this.urlService = SERVICE_URL_BASE_PRODUCTION;
		this.lastTimeUpdate = 0;
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

	private List<IncidentModel> getIncidentsEnCoursFromService(String scope)
			throws IOException, JSONException, ParseException {
		return IncidentModel.deserializeFromArray(requestToService(new HttpGet(
				this.urlService + "/api" + INCIDENTS_JSON_URL + "/" + scope)));
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
		String[] projection = new String[] { TypeLigneBDDHelper.NOM_TABLE };

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
										.getColumnIndex(TypeLigneBDDHelper.NOM_TABLE)));
				lignes.add(c.getString(c
						.getColumnIndex(TypeLigneBDDHelper.NOM_TABLE)));
			} while (c.moveToNext());
		}

		c.close();

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
				LigneBDDHelper.ID,
				TypeLigneBDDHelper.COL_TYPE_LIGNE,
				LigneBDDHelper.COL_NOM_LIGNE, LigneBDDHelper.COL_IS_FAVORIS };

		String selection = null;
		if (!typeLigne.equals("")) {
			Log.d(getApplicationContext().getString(R.string.log_tag_name)
					+ " " + TAG_SERVICE, "Ligne du type : " + typeLigne);
			selection = "type_ligne = '" + typeLigne + "'";

			Log.d(getApplicationContext().getString(R.string.log_tag_name)
					+ " " + TAG_SERVICE,
					"Envoi d'une requête au ContentProvider");
			Cursor c = cr.query(Uri.withAppendedPath(
					DefaultContentProvider.CONTENT_URI,
					DefaultContentProvider.LIGNES_URI), projection, selection,
					null, null);

			ArrayList<LigneModel> lignes = new ArrayList<LigneModel>();
			if (c.moveToFirst()) {
				do {
					LigneModel ligne = LigneBDDHelper.cursorToLigneModel(c);
					Log.d(getApplicationContext().getString(
							R.string.log_tag_name)
							+ " " + TAG_SERVICE, "Ajout de la ligne : " + ligne);
					lignes.add(ligne);
				} while (c.moveToNext());
			}

			c.close();
			Log.i(getApplicationContext().getString(R.string.log_tag_name)
					+ " " + TAG_SERVICE, "Fin récupération lignes");

			return lignes;
		} else {
			return null;
		}
	}

	/**
	 * Retourne les incidents en cours, du provider ou du service si les données
	 * sont expirées
	 * 
	 * @param scope
	 *            Le scope des incidents (jour, heure, minute)
	 * @return La liste des incidents
	 * @throws ParseException
	 * @throws JSONException
	 * @throws IOException
	 */
	private List<IncidentModel> getIncidentsEnCoursFromProviderOrService(
			String scope) throws IOException, JSONException, ParseException {
		boolean shouldUpdate;

		List<IncidentModel> incidentsService = null;

		Uri uriContentProvider = Uri.withAppendedPath(
				DefaultContentProvider.CONTENT_URI,
				DefaultContentProvider.INCIDENTS_URI);
		ContentResolver cr = getContentResolver();

		shouldUpdate = lastTimeUpdate == 0
				|| (lastTimeUpdate + 60 * 1000 < System.currentTimeMillis());
		if (shouldUpdate) {
			incidentsService = getIncidentsEnCoursFromService(scope);

			// Suppression des anciens incidents
			cr.delete(uriContentProvider, null, null);

			// Ajout des nouveaux incidents
			String[] projectionIdLigne = new String[] { LigneBDDHelper.ID };

			String[] selectionArgs;
			int ligneId = 0;
			for (IncidentModel incidentService : incidentsService) {
				selectionArgs = new String[] { incidentService.getLigne()
						.getNumLigne() };

				Cursor c = cr.query(Uri.withAppendedPath(
						DefaultContentProvider.CONTENT_URI,
						DefaultContentProvider.LIGNES_URI), projectionIdLigne,
						 LigneBDDHelper.COL_NOM_LIGNE.concat("=?"), selectionArgs, null);
				if (c.moveToFirst()) {
					ligneId = c.getInt(c.getColumnIndex(LigneBDDHelper.ID));
				}
				c.close();

				ContentValues cvIncident = IncidentsBDDHelper.getContentValues(
						incidentService, ligneId);
				cr.insert(uriContentProvider, cvIncident);
			}

			lastTimeUpdate = System.currentTimeMillis();
		} else {
			String[] projection = new String[] {
					IncidentsBDDHelper.ID,
					IncidentsBDDHelper.COL_RAISON,
					IncidentsBDDHelper.COL_LAST_MODIFIED_TIME,
					IncidentsBDDHelper.COL_STATUT,
					IncidentsBDDHelper.COL_NB_VOTE_PLUS,
					IncidentsBDDHelper.COL_NB_VOTE_MINUS,
					IncidentsBDDHelper.COL_NB_VOTE_ENDED,
					LigneBDDHelper.ID,
					LigneBDDHelper.COL_NOM_LIGNE,
					LigneBDDHelper.COL_IS_FAVORIS,
					TypeLigneBDDHelper.COL_TYPE_LIGNE
			};

			Cursor cIncidentsDB = cr.query(uriContentProvider, projection,
					null, null, null);
			
			incidentsService = new ArrayList<IncidentModel>();
			if (cIncidentsDB.moveToFirst()) {
				while (cIncidentsDB.moveToNext()) {
					incidentsService.add(IncidentsBDDHelper
							.getIncidentModelFromCursor(cIncidentsDB));
				}
			}
			cIncidentsDB.close();
		}

		return incidentsService;
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
				LigneBDDHelper.ID,
				TypeLigneBDDHelper.NOM_TABLE, LigneBDDHelper.COL_NOM_LIGNE,
				LigneBDDHelper.COL_IS_FAVORIS };

		Cursor c = cr.query(Uri.withAppendedPath(
				DefaultContentProvider.CONTENT_URI,
				DefaultContentProvider.FAVORIS_URI), projection, null, null,
				null);

		ArrayList<LigneModel> lignes = new ArrayList<LigneModel>();
		if (c.moveToFirst()) {
			do {
				LigneModel ligne = LigneBDDHelper.cursorToLigneModel(c);
				Log.d(getApplicationContext().getString(R.string.log_tag_name)
						+ " " + TAG_SERVICE, "Ajout de la ligne : " + ligne);
				lignes.add(ligne);
			} while (c.moveToNext());
		}
		c.close();

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
		lastTimeUpdate = 0;
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
	private Boolean voteIncident(int incidentId, IncidentAction action)
			throws UnsupportedEncodingException, JSONException {
		Log.i(getApplicationContext().getString(R.string.log_tag_name) + " "
				+ TAG_SERVICE, "Début de vote pour un incident");

		String url;
		url = this.urlService + "/api" + INCIDENT_JSON_URL + "/vote/"
				+ incidentId + "/" + action.toString();

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
		editedValues.put(LigneBDDHelper.COL_IS_FAVORIS, ligne.isFavoris() ? 1
				: 0);
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
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				TIMEOUT_CONNECTION);
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
	public void startGetIncidentsAsync(
			String scope,
			IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener callback) {
		new LoadIncidentsAsyncTask(callback).execute(scope);
	}

	@Override
	public void startGetTypeLignesAsync(
			IIncidentsTransportsBackgroundServiceGetTypeLignesListener callback) {
		new LoadTypeLignesAsyncTask(callback).execute();
	}

	@Override
	public void startGetLignesAsync(String typeLigne,
			IIncidentsTransportsBackgroundServiceGetLignesListener callback) {
		new LoadLignesAsyncTask(callback).execute(typeLigne);
	}

	@Override
	public void startReportIncident(
			String typeLigne,
			String numLigne,
			String raison,
			IIncidentsTransportsBackgroundServiceReportNewIncidentListener callback) {
		new ReportIncidentAsyncTask(callback).execute(typeLigne, numLigne,
				raison);
	}

	@Override
	public void startVoteIncident(IncidentModel incident,
			IncidentAction action,
			IIncidentsTransportsBackgroundServiceVoteIncidentListener callback) {
		new VoteIncidentAsyncTask(incident, callback).execute(action);
	}

	public void startRegisterFavoris(LigneModel ligne) {
		new RegisterFavorisAsyncTask().execute(ligne);
	}

	public void startGetFavorisAsync(
			IIncidentsTransportsBackgroundServiceGetFavorisListener callback) {
		new GetFavorisAsyncTask(callback).execute();
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

		lastTimeUpdate = 0;
	}

	@Override
	public String getUrlService() {
		return urlService;
	}
}
