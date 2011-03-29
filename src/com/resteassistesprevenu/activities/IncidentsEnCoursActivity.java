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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.activities.listeners.IIncidentActionListener;
import com.resteassistesprevenu.model.IncidentAction;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.model.LigneModel;
import com.resteassistesprevenu.model.adapters.IncidentModelArrayAdapter;
import com.resteassistesprevenu.services.IIncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetFavorisListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceVoteIncidentListener;

public class IncidentsEnCoursActivity extends Activity implements
		IIncidentActionListener {

	private List<IncidentModel> incidentsService;
	
	private IncidentModelArrayAdapter mAdapter;
	private IIncidentsTransportsBackgroundService mBoundService;
	private List<LigneModel> lignesFavoris;

	private RadioButton mBtnEnCours;
	private RadioButton mBtnHeure;
	private RadioButton mBtnMinute;
	private Button mBtnAddIncident;

	private ProgressDialog loadingDialog;

	private String mCurrentScope;
	
	private static final int REQUEST_FAVORIS = 100; 
	private static final int REQUEST_CHOOSE_SERVEUR = 101;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initialize();

		ServiceConnection connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName className,
					IBinder service) {
				Log.i(getString(R.string.log_tag_name), "Service Connected!");

				mBoundService = ((IncidentsTransportsBackgroundServiceBinder) service)
						.getService();

				mBoundService
						.addGetIncidentsListener(new IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener() {
							@Override
							public void dataChanged(
									List<IncidentModel> incidentsService) {
								try {
									setIncidents(incidentsService);
									loadingDialog.dismiss();
								} catch (Exception e) {
									Log.e("ResteAssisTesPrevenu : ",
											"Problème de conversion en retour du service",
											e);
								}
							}
						});

				mBoundService
						.addVoteIncidentListener(new IIncidentsTransportsBackgroundServiceVoteIncidentListener() {

							@Override
							public void dataChanged(boolean voteSent) {
								if (voteSent) {
									Toast.makeText(
											IncidentsEnCoursActivity.this,
											R.string.msg_vote_OK,
											Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(
											IncidentsEnCoursActivity.this,
											R.string.msg_vote_KO,
											Toast.LENGTH_SHORT).show();
								}
							}
						});

				mBoundService
						.addGetFavorisListener(new IIncidentsTransportsBackgroundServiceGetFavorisListener() {

							@Override
							public void dataChanged(List<LigneModel> lignes) {
								lignesFavoris = new ArrayList<LigneModel>();
								lignesFavoris.addAll(lignes);

								startGetIncidentsFromServiceAsync(mCurrentScope);
							}
						});

				mBoundService.startGetFavorisAsync();
			}

			public void onServiceDisconnected(ComponentName className) {
			}
		};

		bindService(new Intent(getApplicationContext(),
				IncidentsTransportsBackgroundService.class), connection,
				Context.BIND_AUTO_CREATE);
	}

	private void initialize() {
		this.mBtnEnCours = (RadioButton) this.findViewById(R.id.radioEnCours);
		this.mBtnHeure = (RadioButton) this.findViewById(R.id.radioHeure);
		this.mBtnMinute = (RadioButton) this.findViewById(R.id.radioMinute);
		this.mBtnAddIncident = (Button) this
				.findViewById(R.id.btnAjouterIncident);

		this.incidentsService = new ArrayList<IncidentModel>();
		
		this.mCurrentScope = IncidentModel.SCOPE_CURRENT;
		
		this.mAdapter = new IncidentModelArrayAdapter(this,
				R.id.listViewIncidentEnCours, this.incidentsService, this);
		((android.widget.ListView) this
				.findViewById(R.id.listViewIncidentEnCours))
				.setAdapter(mAdapter);

		this.mBtnAddIncident.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IncidentsEnCoursActivity.this.startActivityForResult(
						new Intent(IncidentsEnCoursActivity.this,
								NewIncidentActivity.class), 1);
			}
		});

		this.mBtnEnCours.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentScope = IncidentModel.SCOPE_CURRENT;
				startGetIncidentsFromServiceAsync(mCurrentScope);
			}
		});

		this.mBtnHeure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentScope = IncidentModel.SCOPE_HOUR;
				startGetIncidentsFromServiceAsync(mCurrentScope);
			}
		});

		this.mBtnMinute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentScope = IncidentModel.SCOPE_MINUTE;
				startGetIncidentsFromServiceAsync(mCurrentScope);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.incidents_en_cours_activity_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.choose_serveur:
			chooseServeur();
			return true;
		case R.id.menu_favoris:
			startActivityForResult(new Intent(this, FavorisActivity.class), REQUEST_FAVORIS);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void chooseServeur() {
		final CharSequence[] items = { "Production", "Pré-Production" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(
				R.string.title_choose_serveur).setSingleChoiceItems(items,
				mBoundService.isProduction() ? 0 : 1,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mBoundService != null) {
							if (which == 0) {
								mBoundService.setProduction(true);
							} else {
								mBoundService.setProduction(false);
							}

							IncidentsEnCoursActivity.this
									.startGetIncidentsFromServiceAsync(mCurrentScope);
						}

						dialog.dismiss();
					}
				});

		builder.show();
	}

	/**
	 * @return the incidents
	 */
	public List<IncidentModel> getIncidents() {
		return incidentsService;
	}

	/**
	 * @param incidents
	 *            the incidents to set
	 */
	public void setIncidents(List<IncidentModel> incidents) {
		if (this.incidentsService == null) {
			this.incidentsService = new ArrayList<IncidentModel>();
		}

		this.incidentsService.clear();
		
		if (lignesFavoris != null && lignesFavoris.size() > 0) {
			for (IncidentModel incident : incidents) {
				if(this.lignesFavoris.contains(incident.getLigne())) {
					this.incidentsService.add(incident);
				}
			}
		}
		else {			
			this.incidentsService.addAll(incidents);
		}
		
		mAdapter.notifyDataSetChanged();
	}

	private void startGetIncidentsFromServiceAsync(String scope) {
		loadingDialog = ProgressDialog
				.show(IncidentsEnCoursActivity.this,
						"",
						getString(R.string.msg_incident_en_cours_list_loading_incidents));
		mBoundService.startGetIncidentsAsync(scope);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_FAVORIS) {
			if (resultCode == Activity.RESULT_OK) {	
				mBoundService.startGetFavorisAsync();
			}
		}
	}

	@Override
	public void actionPerformed(IncidentModel incident, IncidentAction action) {
		if (action.equals(IncidentAction.SHARE)) {
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("text/plain");

			share.putExtra(Intent.EXTRA_TEXT, String.format(
					getString(R.string.msg_share), incident.getLigne().toString(),
					"http://openreact.alwaysdata.net/incident/detail/"
							+ incident.getId()));

			startActivity(Intent.createChooser(share,
					getString(R.string.msg_share)));
		} else {
			mBoundService.startVoteIncident(incident.getId(), action);
		}
	}
}