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

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.model.adapters.IncidentModelArrayAdapter;
import com.resteassistesprevenu.services.IIncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;
import com.resteassistesprevenu.services.listeners.IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener;

public class IncidentsEnCoursActivity extends Activity {

	private List<IncidentModel> incidents;
	private IncidentModelArrayAdapter mAdapter;
	private IIncidentsTransportsBackgroundService mBoundService;

	private RadioButton mBtnEnCours;
	private RadioButton mBtnHeure;
	private RadioButton mBtnMinute;
	private Button mBtnAddIncident;

	private ProgressDialog loadingDialog;

	private String mCurrentScope;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.incidents_en_cours_view);

		initialize();

		ServiceConnection connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName className,
					IBinder service) {
				Log.i("BackgroundService", "Connected!");
				mBoundService = ((IncidentsTransportsBackgroundServiceBinder) service)
						.getService();

				startGetIncidentsFromServiceAsync(IncidentModel.SCOPE_CURRENT);

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
											"Probl�me de conversion en retour du service",
											e);
								}
							}
						});
			}

			public void onServiceDisconnected(ComponentName className) {
			}
		};

		getApplicationContext().bindService(
				new Intent(getApplicationContext(),
						IncidentsTransportsBackgroundService.class),
				connection, Context.BIND_AUTO_CREATE);
	}

	private void initialize() {
		this.mBtnEnCours = (RadioButton) this.findViewById(R.id.radioEnCours);
		this.mBtnHeure = (RadioButton) this.findViewById(R.id.radioHeure);
		this.mBtnMinute = (RadioButton) this.findViewById(R.id.radioMinute);
		this.mBtnAddIncident = (Button) this
				.findViewById(R.id.btnAjouterIncident);

		this.incidents = new ArrayList<IncidentModel>();
		this.mCurrentScope = IncidentModel.SCOPE_CURRENT;
		this.mAdapter = new IncidentModelArrayAdapter(
				IncidentsEnCoursActivity.this, R.id.listViewIncidentEnCours,
				this.incidents);
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
				startGetIncidentsFromServiceAsync(IncidentModel.SCOPE_CURRENT);
			}
		});

		this.mBtnHeure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentScope = IncidentModel.SCOPE_HOUR;
				startGetIncidentsFromServiceAsync(IncidentModel.SCOPE_HOUR);
			}
		});

		this.mBtnMinute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentScope = IncidentModel.SCOPE_MINUTE;
				startGetIncidentsFromServiceAsync(IncidentModel.SCOPE_MINUTE);
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void chooseServeur() {
		final CharSequence[] items = { "Production", "Pr�-Production" };
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
		return incidents;
	}

	/**
	 * @param incidents
	 *            the incidents to set
	 */
	public void setIncidents(List<IncidentModel> incidents) {
		if (this.incidents == null) {
			this.incidents = new ArrayList<IncidentModel>();
		}

		this.incidents.clear();
		this.incidents.addAll(incidents);
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
		if (resultCode == Activity.RESULT_OK) {
			startGetIncidentsFromServiceAsync(mCurrentScope);
		}
	}
}