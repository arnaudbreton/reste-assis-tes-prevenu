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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.LigneModel;
import com.resteassistesprevenu.model.LigneModelService;
import com.resteassistesprevenu.services.IIncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetLignesListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetTypeLignesListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceReportNewIncidentListener;

/**
 * Activité de déclaration d'un incident
 * @author Arnaud
 *
 */
public class NewIncidentActivity extends Activity {
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
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_incident_view);

		mSpinTypeLignes = (Spinner) this.findViewById(R.id.spinnerTypeLigne);
		
		mSpinLignes = (Spinner) this.findViewById(R.id.spinnerNumeroLigne);
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
									"Chargement des types de lignes : ");

							Spinner spinTypeLigne = (Spinner) NewIncidentActivity.this
									.findViewById(R.id.spinnerTypeLigne);
							ArrayAdapter<String> adapter = new ArrayAdapter<String>(
									NewIncidentActivity.this,
									android.R.layout.simple_spinner_item, data
											.toArray(new String[data.size()]));
							adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							spinTypeLigne.setAdapter(adapter);

							if (spinTypeLigne.getSelectedItem() != null) {
								mBoundService.startGetLignesAsync(LigneModelService.TYPE_LIGNE_RER);
								int imageResource = NewIncidentActivity.this.getResources().getIdentifier(LigneModelService.getTypeLigneImage(LigneModelService.TYPE_LIGNE_RER), "drawable", NewIncidentActivity.this.getPackageName());
								if(imageResource != 0) {
									Drawable image = NewIncidentActivity.this.getResources().getDrawable(imageResource);
									((ImageView)findViewById(R.id.imgTypeLigne)).setImageDrawable(image);
								}	
							}
						}
					});

			mBoundService
					.addGetLignesListener(new IIncidentsTransportsBackgroundServiceGetLignesListener() {
						@Override
						public void dataChanged(List<LigneModel> data) {
							Log.i(getString(R.string.log_tag_name) + " "
									+ TAG_ACTIVITY,
									"Début de chargement des lignes.");							

							ArrayList<String> lignes = new ArrayList<String>();
							if (data != null) {
								Log.d(getString(R.string.log_tag_name) + " "
										+ TAG_ACTIVITY,
										"Chargement des lignes de "
												+ data.size() + " lignes.");
								for (LigneModel ligne : data) {
									lignes.add(ligne.getNumLigne());
								}
								Log.d(getString(R.string.log_tag_name) + " "
										+ TAG_ACTIVITY,
										"Fin de chargement des lignes.");
							}

							ArrayAdapter<String> adapter = new ArrayAdapter<String>(
									NewIncidentActivity.this,
									android.R.layout.simple_spinner_item,
									lignes.toArray(new String[lignes.size()]));
							adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							mSpinLignes.setAdapter(adapter);
							
							int imageResource = NewIncidentActivity.this.getResources().getIdentifier(LigneModelService.getNumLigneImage(mSpinTypeLignes.getSelectedItem().toString(), mSpinLignes.getSelectedItem().toString()), "drawable", NewIncidentActivity.this.getPackageName());
							if(imageResource != 0) {
								Drawable image = NewIncidentActivity.this.getResources().getDrawable(imageResource);
								((ImageView)findViewById(R.id.imgNumLigne)).setImageDrawable(image);
							}	
							
							mSpinTypeLignes
							.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

								@Override
								public void onItemSelected(AdapterView<?> parent,
										View arg1, int arg2, long arg3) {
									Log.i(getString(R.string.log_tag_name) + " "
											+ TAG_ACTIVITY,
											"Chargement des lignes du type : "
													+ parent.getSelectedItem().toString());
									mBoundService.startGetLignesAsync(parent
											.getSelectedItem().toString());
								}

								@Override
								public void onNothingSelected(AdapterView<?> arg0) {
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
