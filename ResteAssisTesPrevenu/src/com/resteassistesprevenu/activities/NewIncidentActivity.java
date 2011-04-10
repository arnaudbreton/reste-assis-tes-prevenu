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
import android.widget.Spinner;
import android.widget.Toast;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.LigneModel;
import com.resteassistesprevenu.model.LigneModelService;
import com.resteassistesprevenu.model.adapters.ImageNumLineSpinnerListAdapter;
import com.resteassistesprevenu.model.adapters.ImageTypeLineSpinnerListAdapter;
import com.resteassistesprevenu.services.IIncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;
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
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_incident_view);
		
		startAd();

		mSpinTypeLignes = (Spinner) this.findViewById(R.id.spinnerTypeLigne);
		this.typeLignes = new ArrayList<String>();
		mImgTypeLignesAdapter = new ImageTypeLineSpinnerListAdapter(
				NewIncidentActivity.this, R.layout.new_incident_view,
				this.typeLignes);
		mSpinTypeLignes.setAdapter(mImgTypeLignesAdapter);

		mSpinLignes = (Spinner) this.findViewById(R.id.spinnerNumeroLigne);
		this.lignes = new ArrayList<LigneModel>();
		mImgLignesAdapter = new ImageNumLineSpinnerListAdapter(
				NewIncidentActivity.this, R.layout.new_incident_view,
				this.lignes);
		mSpinLignes.setAdapter(mImgLignesAdapter);

		mTxtRaison = (EditText) this.findViewById(R.id.txtRaison);


		mBtnRapporter = (Button) this.findViewById(R.id.btnRapporterIncident);
		mBtnRapporter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String typeLigne = null;
				String numLigne = null;
				String raison = null;

				if (mSpinTypeLignes != null
						&& mSpinTypeLignes.getSelectedItem() != null) {
					typeLigne = mImgTypeLignesAdapter.getItem(mSpinTypeLignes.getSelectedItemPosition());
				}

				if (mSpinLignes != null
						&& mSpinLignes.getSelectedItem() != null) {
					numLigne = mImgLignesAdapter.getItem(mSpinLignes.getSelectedItemPosition()).getNumLigne();
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

		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"Début de lien au service.");
		bindService(
				new Intent(this, IncidentsTransportsBackgroundService.class),
				new ServiceIncidentConnection(), Context.BIND_AUTO_CREATE);
	}

	private class ServiceIncidentConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
					"Service connected");

			mBoundService = ((IncidentsTransportsBackgroundServiceBinder) service)
					.getService();

			mBoundService
					.addGetTypeLignesListener(new IIncidentsTransportsBackgroundServiceGetTypeLignesListener() {
						@Override
						public void dataChanged(List<String> data) {
							Log.i(getString(R.string.log_tag_name) + " "
									+ TAG_ACTIVITY,
									"Chargement des types de lignes");

							typeLignes.clear();
							typeLignes.addAll(data);
							mImgTypeLignesAdapter.notifyDataSetChanged();
							mSpinTypeLignes.setAdapter(mImgTypeLignesAdapter);

							mBoundService
									.startGetLignesAsync(LigneModelService.TYPE_LIGNE_RER);
						}
					});

			mBoundService
					.addGetLignesListener(new IIncidentsTransportsBackgroundServiceGetLignesListener() {
						@Override
						public void dataChanged(List<LigneModel> data) {
							Log.i(getString(R.string.log_tag_name) + " "
									+ TAG_ACTIVITY,
									"Début de chargement des lignes.");

							lignes.clear();
							lignes.addAll(data);
							mImgLignesAdapter.notifyDataSetChanged();
							
							mSpinLignes.setSelection(0);

							mSpinTypeLignes
									.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

										@Override
										public void onItemSelected(
												AdapterView<?> parent,
												View arg1, int arg2, long arg3) {
											Log.i(getString(R.string.log_tag_name)
													+ " " + TAG_ACTIVITY,
													"Chargement des lignes du type : "
															+ parent.getSelectedItem()
																	.toString());
											mBoundService
													.startGetLignesAsync(mImgTypeLignesAdapter
															.getItem(mSpinTypeLignes
																	.getSelectedItemPosition()));
										}

										@Override
										public void onNothingSelected(
												AdapterView<?> arg0) {
										}
									});

							Log.i(getString(R.string.log_tag_name) + " "
									+ TAG_ACTIVITY,
									"Fin de chargement des lignes.");

						}

					});

			mBoundService
					.addReportNewIncidentListener(new IIncidentsTransportsBackgroundServiceReportNewIncidentListener() {
						@Override
						public void dataChanged(String idIncident) {
							Log.i(getString(R.string.log_tag_name) + " "
									+ TAG_ACTIVITY,
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
										.setPositiveButton(
												"Ok",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {
													}
												});
								builder.show();
							} else {
								Log.i(getString(R.string.log_tag_name) + " "
										+ TAG_ACTIVITY,
										"Création de l'incident OK.");
								Toast.makeText(
										NewIncidentActivity.this,
										String.format(getString(
												R.string.msg_report_new_incident_OK,
												idIncident)), Toast.LENGTH_LONG)
										.show();
								NewIncidentActivity.this.setResult(RESULT_OK);
								NewIncidentActivity.this.finish();
							}
							Log.i(getString(R.string.log_tag_name) + " "
									+ TAG_ACTIVITY,
									"Fin de création d'un incident.");
						}
					});

			mBoundService.startGetTypeLignesAsync();
		}

		public void onServiceDisconnected(ComponentName className) {
		}
	};

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		mSpinTypeLignes.setOnItemSelectedListener(null);
	}
}
