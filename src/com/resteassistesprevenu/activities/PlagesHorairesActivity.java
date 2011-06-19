package com.resteassistesprevenu.activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.PlageHoraireModel;
import com.resteassistesprevenu.services.BackgroundService;
import com.resteassistesprevenu.services.IBackgroundService;
import com.resteassistesprevenu.services.IncidentsTransportsBackgroundServiceBinder;
import com.resteassistesprevenu.services.listeners.IBackgroundServiceRegisterParametrageListener;

public class PlagesHorairesActivity extends Activity implements IBackgroundServiceRegisterParametrageListener {
	/**
	 * Bouton d'ajout de plage horaire
	 */
	private ImageButton mBtnAddPlageHoraire;
	
	/**
	 * Binder au service
	 */
	private IBackgroundService mBoundService;
	
	/**
	 * Connection au service
	 */
	private ServiceIncidentConnection conn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.plages_horaires_view);

		this.mBtnAddPlageHoraire = (ImageButton) findViewById(R.id.btnAddPlageHoraire);
		this.mBtnAddPlageHoraire.setOnClickListener(new View.OnClickListener() {
			private int heureDebut;
			private int minuteDebut;
			private int heureFin;
			private int minuteFin;
			
			@Override
			public void onClick(View v) {
				final OnTimeSetListener callBackFin = new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						heureFin = hourOfDay;
						minuteFin = minute;
						
						PlageHoraireModel plage = new PlageHoraireModel(heureDebut, minuteDebut, heureFin, minuteFin);
						if(mBoundService != null) {
							mBoundService.startRegisterParametreAsync("plage", plage.toString(), PlagesHorairesActivity.this);
						}
					}
				};

				OnTimeSetListener callBackDebut = new OnTimeSetListener() {

					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						heureDebut = hourOfDay;
						minuteDebut = minute;		
						
						new TimePickerDialog(PlagesHorairesActivity.this, callBackFin, 8, 0, true).show();
					}
				};
				
				new TimePickerDialog(PlagesHorairesActivity.this, callBackDebut, 7, 0, true).show();
			}
		});
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		this.conn = new ServiceIncidentConnection();
		bindService(new Intent(getApplicationContext(),
				BackgroundService.class), this.conn,
				Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop() {
		super.onStop();

		unbindService(conn);
	}
	
	private class ServiceIncidentConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(getString(R.string.log_tag_name), "Service Connected!");

			mBoundService = ((IncidentsTransportsBackgroundServiceBinder) service)
					.getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};
}
