package com.resteassistesprevenu.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.LigneModel;
import com.resteassistesprevenu.model.adapters.ImageNumLineSpinnerListAdapter;
import com.resteassistesprevenu.model.adapters.ImageTypeLineSpinnerListAdapter;
import com.resteassistesprevenu.services.IIncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetFavorisListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetLignesListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetTypeLignesListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceReportNewIncidentListener;

/**
 * Activité de déclaration d'un incident
 * 
 * @author Arnaud
 * 
 */
public class NewIncidentActivity extends BaseActivity {
	/**
	 * TAG de log propre à l'activité
	 */
	private static final String TAG_ACTIVITY = "NewIncidentActivity";

	/**
	 * Service
	 */
	private IIncidentsTransportsBackgroundService mBoundService;

	/**
	 * Spinner contenant les types de ligne
	 */
	private Spinner mSpinTypeLignes;

	/**
	 * Spinner contenant les lignes
	 */
	private Spinner mSpinLignes;

	/**
	 * L'adapteur de LigneModel en image
	 */
	private ImageNumLineSpinnerListAdapter mImgLignesAdapter;

	/**
	 * L'adapteur de type de ligne en image
	 */
	private ImageTypeLineSpinnerListAdapter mImgTypeLignesAdapter;

	/**
	 * RadioButton pour filtrer uniquement par favoris
	 */
	private RadioButton radioFavoris;

	/**
	 * RadioButton pour afficher toutes les lignes
	 */
	private RadioButton radioAll;

	/**
	 * Texte contenant la raison
	 */
	private EditText mTxtRaison;

	/**
	 * Bouton de création de l'incident
	 */
	private Button mBtnRapporter;

	/**
	 * ProgressDialog de création de l'incident
	 */
	private ProgressDialog mPdRapporter;

	/**
	 * Ensemble des lignes affichés
	 */
	private List<LigneModel> lignes;

	/**
	 * Ensemble des types lignes affichés
	 */
	private List<String> typeLignes;

	/**
	 * Le gestionnaire de connexion au service
	 */
	private ServiceIncidentConnection conn;
	/**
	 * Listener de récupération des types de ligne
	 */
	private IIncidentsTransportsBackgroundServiceGetTypeLignesListener getTypeLignesListener;

	/**
	 * Listener de récupération des lignes
	 */
	private IIncidentsTransportsBackgroundServiceGetLignesListener getLignesListener;

	/**
	 * Listener de récupération des favoris
	 */
	private IIncidentsTransportsBackgroundServiceGetFavorisListener getFavorisListener;

	/**
	 * Listener de création d'un incident
	 */
	private IIncidentsTransportsBackgroundServiceReportNewIncidentListener reportNewIncidentListener;

	/**
	 * Indicateur de filtre par favoris
	 */
	private boolean isFavorisFiltered;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_incident_view);

		super.startAd();

		this.lignes = new ArrayList<LigneModel>();
		this.typeLignes = new ArrayList<String>();

		this.isFavorisFiltered = true;

		this.radioFavoris = (RadioButton) this.findViewById(R.id.radioFavoris);
		this.radioFavoris.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isFavorisFiltered = true;
				mBoundService.startGetFavorisAsync();
			}
		});

		this.radioAll = (RadioButton) this.findViewById(R.id.radioAll);
		this.radioAll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				isFavorisFiltered = false;
				mBoundService.startGetTypeLignesAsync();
			}
		});

		this.mSpinTypeLignes = (Spinner) this
				.findViewById(R.id.spinnerTypeLigne);
		this.mImgTypeLignesAdapter = new ImageTypeLineSpinnerListAdapter(
				NewIncidentActivity.this, R.layout.new_incident_view,
				this.typeLignes);
		this.mSpinTypeLignes.setAdapter(mImgTypeLignesAdapter);

		this.mSpinLignes = (Spinner) this.findViewById(R.id.spinnerNumeroLigne);
		this.mImgLignesAdapter = new ImageNumLineSpinnerListAdapter(
				NewIncidentActivity.this, R.layout.new_incident_view,
				this.lignes);
		this.mSpinLignes.setAdapter(mImgLignesAdapter);

		this.mSpinTypeLignes
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View arg1, int arg2, long arg3) {
						Log.i(getString(R.string.log_tag_name) + " "
								+ TAG_ACTIVITY,
								"Chargement des lignes du type : "
										+ parent.getSelectedItem().toString());
						String typeLigne = mImgTypeLignesAdapter
								.getItem(mSpinTypeLignes
										.getSelectedItemPosition());

						if (isFavorisFiltered) {
							mImgLignesAdapter.getFilter().filter(typeLigne);
						} else {
							mBoundService.startGetLignesAsync(typeLigne);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

		this.mTxtRaison = (EditText) this.findViewById(R.id.txtRaison);

		this.mBtnRapporter = (Button) this
				.findViewById(R.id.btnRapporterIncident);
		this.mBtnRapporter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String typeLigne = null;
				String numLigne = null;
				String raison = null;

				if (mSpinTypeLignes != null
						&& mSpinTypeLignes.getSelectedItem() != null) {
					typeLigne = mImgTypeLignesAdapter.getItem(mSpinTypeLignes
							.getSelectedItemPosition());
				}

				if (mSpinLignes != null
						&& mSpinLignes.getSelectedItem() != null) {
					numLigne = mImgLignesAdapter.getItem(
							mSpinLignes.getSelectedItemPosition())
							.getNumLigne();
				}

				if (mTxtRaison != null) {
					raison = mTxtRaison.getText().toString();
				}

				if (raison != null && numLigne != null && typeLigne != null
						&& !raison.equals("") && !numLigne.equals("")
						&& !typeLigne.equals("")) {
					mPdRapporter = ProgressDialog
							.show(NewIncidentActivity.this,
									"",
									getString(R.string.msg_report_new_incident_reporting_incident));
					mBoundService.startReportIncident(typeLigne, numLigne,
							raison);
				} else {
					if (raison == null || raison.equals("")) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								NewIncidentActivity.this);
						builder.setMessage(
								getString(R.string.msg_report_new_incident_KO_no_reason))
								.setCancelable(false)
								.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
											}
										});
						builder.show();
					}
				}
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
		"Début de lien au service.");
		this.conn = new ServiceIncidentConnection();
		bindService(
				new Intent(this, IncidentsTransportsBackgroundService.class),
				this.conn, Context.BIND_AUTO_CREATE);
	}

	private class ServiceIncidentConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
					"Service connected");

			mBoundService = ((IncidentsTransportsBackgroundServiceBinder) service)
					.getService();

			getTypeLignesListener = new IIncidentsTransportsBackgroundServiceGetTypeLignesListener() {
				@Override
				public void dataChanged(List<String> data) {
					Log.i(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
							"Chargement des types de lignes");

					typeLignes.clear();
					typeLignes.addAll(data);
					mImgTypeLignesAdapter.notifyDataSetChanged();					
					
					mSpinTypeLignes.setSelection(0);
					mBoundService.startGetLignesAsync(LigneModel.TYPE_LIGNE_RER);
				}
			};
			mBoundService.addGetTypeLignesListener(getTypeLignesListener);

			getFavorisListener = new IIncidentsTransportsBackgroundServiceGetFavorisListener() {
				@Override
				public void dataChanged(List<LigneModel> lignesData) {
					// S'il existe des favoris, on les affiche
					if (lignesData.size() > 0) {
						typeLignes.clear();
						for (LigneModel ligne : lignesData) {
							if (!typeLignes.contains(ligne.getTypeLigne())) {
								typeLignes.add(ligne.getTypeLigne());
							}
						}

						lignes.clear();
						lignes.addAll(lignesData);						
						mImgLignesAdapter.notifyDataSetChanged();
						
						mImgTypeLignesAdapter.notifyDataSetChanged();
						mImgLignesAdapter.getFilter().filter(typeLignes.get(0));
						
						mSpinTypeLignes.setSelection(0);
						mSpinLignes.setSelection(0);
						
						isFavorisFiltered = true;
					}
					// Sinon on affiche toutes les lignes
					else {
						LinearLayout layout = (LinearLayout) findViewById(R.id.layout_radioGroupNewIncident);
						layout.setVisibility(View.GONE);
						
						isFavorisFiltered = false;
												
						mBoundService.startGetTypeLignesAsync();
					}
				}
			};
			mBoundService.addGetFavorisListener(getFavorisListener);

			getLignesListener = new IIncidentsTransportsBackgroundServiceGetLignesListener() {
				@Override
				public void dataChanged(List<LigneModel> data) {
					Log.i(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
							"Début de chargement des lignes.");			
					
					lignes.clear();
					lignes.addAll(data);
					
					mImgLignesAdapter.getFilter().filter(null);
					
					mImgTypeLignesAdapter.notifyDataSetChanged();
					mImgLignesAdapter.notifyDataSetChanged();					
					
					mSpinLignes.setSelection(0);

					Log.i(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
							"Fin de chargement des lignes.");

				}

			};
			mBoundService.addGetLignesListener(getLignesListener);

			reportNewIncidentListener = new IIncidentsTransportsBackgroundServiceReportNewIncidentListener() {
				@Override
				public void dataChanged(String idIncident) {
					Log.i(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
							"Début de création d'un incident.");
					if (mPdRapporter != null) {
						mPdRapporter.dismiss();
					}

					if (idIncident == null) {
						Log.e(getString(R.string.log_tag_name) + " "
								+ TAG_ACTIVITY,
								"Erreur de création de l'incident : numéro null.");
						AlertDialog.Builder builder = new AlertDialog.Builder(
								NewIncidentActivity.this);
						builder.setMessage(
								getString(R.string.msg_report_new_incident_KO))
								.setCancelable(false)
								.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {
											}
										});
						builder.show();
					} else {
						Log.i(getString(R.string.log_tag_name) + " "
								+ TAG_ACTIVITY, "Création de l'incident OK.");
						Toast.makeText(
								NewIncidentActivity.this,
								String.format(getString(
										R.string.msg_report_new_incident_OK,
										idIncident)), Toast.LENGTH_LONG).show();
						NewIncidentActivity.this.setResult(RESULT_OK);
						NewIncidentActivity.this.finish();
					}
					Log.i(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
							"Fin de création d'un incident.");
				}
			};
			mBoundService
					.addReportNewIncidentListener(reportNewIncidentListener);

			// mBoundService.startGetTypeLignesAsync();
			mBoundService.startGetFavorisAsync();
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService.removeGetTypeLignesListener(getTypeLignesListener);
			mBoundService.removeGetLignesListener(getLignesListener);
			mBoundService
					.removeReportNewIncidentListener(reportNewIncidentListener);
			mBoundService = null;
		}
	};

	@Override
	protected void onStop() {
		super.onStop();

		unbindService(conn);
	}
}
