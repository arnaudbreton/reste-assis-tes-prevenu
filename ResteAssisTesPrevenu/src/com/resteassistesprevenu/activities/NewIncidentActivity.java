package com.resteassistesprevenu.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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

import com.google.ads.AdRequest;
import com.google.ads.AdView;
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
 * Activit� de d�claration d'un incident
 * 
 * @author Arnaud
 * 
 */
public class NewIncidentActivity extends Activity {
	/**
	 * TAG de log propre � l'activit�
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
	 * Texte contenant la raison
	 */
	private EditText mTxtRaison;

	/**
	 * Bouton de cr�ation de l'incident
	 */
	private Button mBtnRapporter;

	/**
	 * ProgressDialog de cr�ation de l'incident
	 */
	private ProgressDialog mPdRapporter;
	
	/**
	 * Ensemble des lignes affich�s
	 */
	private List<LigneModel> lignes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_incident_view);

		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView) this.findViewById(R.id.adViewBanner);
		AdRequest request = new AdRequest();
		request.setTesting(true);
		adView.loadAd(request);

		mSpinTypeLignes = (Spinner) this.findViewById(R.id.spinnerTypeLigne);

		mSpinLignes = (Spinner) this.findViewById(R.id.spinnerNumeroLigne);
		
		this.lignes = new ArrayList<LigneModel>();
		mImgLignesAdapter = new ImageNumLineSpinnerListAdapter(
				NewIncidentActivity.this,
				R.layout.new_incident_view,
				this.lignes);
		mImgLignesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
					typeLigne = mSpinTypeLignes.getSelectedItem().toString();
				}

				if (mSpinLignes != null
						&& mSpinLignes.getSelectedItem() != null) {
					numLigne = mSpinLignes.getSelectedItem().toString();
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
				"D�but de lien au service.");
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
									"Chargement des types de lignes : ");

							ImageTypeLineSpinnerListAdapter adapter = new ImageTypeLineSpinnerListAdapter(
									NewIncidentActivity.this,
									R.layout.new_incident_view,
									data);
							adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							mSpinTypeLignes.setAdapter(adapter);

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
									"D�but de chargement des lignes.");

							lignes = data;
							mImgLignesAdapter.notifyDataSetChanged(); 
							
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
													.startGetLignesAsync(parent
															.getSelectedItem()
															.toString());
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
									"D�but de cr�ation d'un incident.");
							if (mPdRapporter != null) {
								mPdRapporter.dismiss();
							}

							if (idIncident == null) {
								Log.e(getString(R.string.log_tag_name) + " "
										+ TAG_ACTIVITY,
										"Erreur de cr�ation de l'incident : num�ro null.");
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
										"Cr�ation de l'incident OK.");
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
									"Fin de cr�ation d'un incident.");
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
