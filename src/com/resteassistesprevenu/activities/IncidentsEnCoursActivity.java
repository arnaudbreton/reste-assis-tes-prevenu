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
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
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

/**
 * Activité listant les incidents
 * 
 * @author Arnaud
 * 
 */
public class IncidentsEnCoursActivity extends BaseActivity implements
		IIncidentActionListener {
	private enum ModeChargement {
		NORMAL, IGNORER_FAVORIS
	};

	private static final String TAG_ACTIVITY = "IncidentEnCoursActivity";

	/**
	 * Ensemble des incidents affichés
	 */
	private List<IncidentModel> incidentsServiceDisplay;
	
	/**
	 * Liste des incidents chargés
	 */
	private List<IncidentModel> incidentsService;

	/**
	 * Adapter des incidents modèle => listeView
	 */
	private IncidentModelArrayAdapter mAdapter;

	/**
	 * Service
	 */
	private IIncidentsTransportsBackgroundService mBoundService;

	/**
	 * Lignes favorites
	 */
	private List<LigneModel> lignesFavoris;

	/**
	 * Bouton scope "day"
	 */
	private RadioButton mBtnJour;

	/**
	 * Bouton scope "hour"
	 */
	private RadioButton mBtnHeure;

	/**
	 * Bouton scope "minute"
	 */
	private RadioButton mBtnMinute;

	/**
	 * Bouton d'ajout d'incident
	 */
	private Button mBtnAddIncident;

	/**
	 * Texte indiquant qu'il n'y a aucun incident (parmis les favoris ou sur le
	 * service)
	 */
	private TextView mTxtAucunIncident;

	/**
	 * Bouton pour ignorer les favoris dans le chargement des incidents
	 */
	private Button mBtnIgnorerFavoris;

	/**
	 * ProgressDialog de chargement des incidents
	 */
	private ProgressDialog loadingDialog;

	/**
	 * Scope courant
	 */
	private String mCurrentScope;

	/**
	 * RequestCode FavorisActivity
	 */
	private static final int REQUEST_FAVORIS = 100;
	
	/**
	 * RequestCode NewIncidentActivity
	 */
	private static final int REQUEST_NEW_INCIDENT = 100;

	/**
	 * Mode de chargement (avec/sans favoris)
	 */
	private ModeChargement mModeChargement;
	
	/**
	 * Le gestionnaire de connexion
	 */
	private ServiceIncidentConnection conn;
	
	/**
	 * Listener de récupération des incidents
	 */
	private IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener getIncidentsEnCoursListener;
	
	/**
	 * Listener de vote pour les incidents
	 */
	private IIncidentsTransportsBackgroundServiceVoteIncidentListener voteIncidentListener;
	
	/**
	 * Listener de récupération des favoris
	 */
	private IIncidentsTransportsBackgroundServiceGetFavorisListener getFavorisListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initialize();   
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		this.conn = new ServiceIncidentConnection();
		bindService(new Intent(getApplicationContext(),
				IncidentsTransportsBackgroundService.class),
				this.conn, Context.BIND_AUTO_CREATE);
	}

	private void initialize() {

		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"Début initialisation de l'activité.");
		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"Récupération des contrôles.");

		this.mBtnJour = (RadioButton) this.findViewById(R.id.radioJour);
		this.mBtnHeure = (RadioButton) this.findViewById(R.id.radioHeure);
		this.mBtnMinute = (RadioButton) this.findViewById(R.id.radioMinute);
		this.mBtnAddIncident = (Button) this
				.findViewById(R.id.btnAjouterIncident);
		this.mBtnIgnorerFavoris = (Button) this
				.findViewById(R.id.btnIgnorerFavoris);

		this.mTxtAucunIncident = (TextView) findViewById(R.id.txtAucunIncident);

		this.incidentsServiceDisplay = new ArrayList<IncidentModel>();
		this.incidentsService = new ArrayList<IncidentModel>();

		this.mCurrentScope = IncidentModel.SCOPE_HOUR;

		this.mModeChargement = ModeChargement.NORMAL;
		
		startAd();

		this.mAdapter = new IncidentModelArrayAdapter(this,
				R.id.listViewIncidentEnCours, this.incidentsServiceDisplay, this);
		((android.widget.ListView) this
				.findViewById(R.id.listViewIncidentEnCours))
				.setAdapter(mAdapter);

		this.mBtnAddIncident.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IncidentsEnCoursActivity.this.startActivityForResult(
						new Intent(IncidentsEnCoursActivity.this,
								NewIncidentActivity.class), REQUEST_NEW_INCIDENT);
			}
		});

		this.mBtnJour.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mModeChargement = ModeChargement.NORMAL;
				mCurrentScope = IncidentModel.SCOPE_JOUR;
				startGetIncidentsFromServiceAsync(mCurrentScope);
			}
		});

		this.mBtnHeure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mModeChargement = ModeChargement.NORMAL;
				mCurrentScope = IncidentModel.SCOPE_HOUR;
				startGetIncidentsFromServiceAsync(mCurrentScope);
			}
		});

		this.mBtnMinute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCurrentScope = IncidentModel.SCOPE_MINUTE;
				mModeChargement = ModeChargement.NORMAL;
				startGetIncidentsFromServiceAsync(mCurrentScope);
			}
		});

		this.mBtnIgnorerFavoris.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mModeChargement = ModeChargement.IGNORER_FAVORIS;
				//startGetIncidentsFromServiceAsync(mCurrentScope);
				setIncidents(incidentsService);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"Début création du menu");
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.incidents_en_cours_activity_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"Choix d'un menu");
		switch (item.getItemId()) {
		case R.id.menu_choose_serveur:
			Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
					"Menu : choix du serveur");
			chooseServeur();
			return true;
		case R.id.menu_favoris:
			Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
					"Menu : favoris");
			startActivityForResult(new Intent(this, FavorisActivity.class),
					REQUEST_FAVORIS);
			return true;
		case R.id.menu_about:
			Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
					"Menu : about");
			about();
			return true;
		case R.id.menu_voter:
			Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
					"Menu : vote");
			vote();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void vote() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.title_menu_voter);
		builder.setMessage(getString(R.string.msg_voter)).setCancelable(true)
				.setPositiveButton(getString(R.string.msg_btn_voter), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://details?id=com.resteassistesprevenu"));
						startActivity(intent);	
					}
				}).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {						
					}
				});
		builder.show();		
	}

	private void about() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.title_menu_about);
		builder.setMessage(getString(R.string.msg_about)).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});
		builder.show();
	}

	private void chooseServeur() {
		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"Début création AlertDialog choix du serveur");
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

		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"Affichage du choix du serveur");
		builder.show();
	}

	/**
	 * @return the incidents
	 */
	public List<IncidentModel> getIncidents() {
		return incidentsServiceDisplay;
	}

	/**
	 * @param incidents
	 *            the incidents to set
	 */
	public void setIncidents(List<IncidentModel> incidents) {
		if (this.incidentsServiceDisplay == null) {
			this.incidentsServiceDisplay = new ArrayList<IncidentModel>();
		}

		this.incidentsServiceDisplay.clear();
		this.incidentsService.clear();
		
		if (incidents != null && incidents.size() > 0) {		
			// Présence de favoris
			if (this.mModeChargement.equals(ModeChargement.NORMAL)
					&& lignesFavoris != null && lignesFavoris.size() > 0) {
				Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
						"Favoris présent");

				for (IncidentModel incident : incidents) {
					if (this.lignesFavoris.contains(incident.getLigne())) {
						this.incidentsServiceDisplay.add(incident);
					}
				}

				// Si moins d'incidents que ceux du service, on donne la possibilité de tout voir
				if (this.incidentsServiceDisplay.size() < incidents.size()) {
					this.incidentsService.addAll(incidents);
					this.mTxtAucunIncident
							.setText(getString(R.string.msg_no_incident_favoris));
					this.mBtnIgnorerFavoris.setVisibility(View.VISIBLE);
				}
			} else {
				Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
						"Aucun favoris");
				this.incidentsServiceDisplay.addAll(this.incidentsService);
				
				if (this.incidentsServiceDisplay.size() == 0) {
					this.mTxtAucunIncident.setText(getString(R.string.msg_no_incident));
				}
			}
		} else {
			this.mTxtAucunIncident.setText(getString(R.string.msg_no_incident));
		}

		if (this.incidentsServiceDisplay.size() == 0) {
			this.mTxtAucunIncident.setVisibility(View.VISIBLE);
		} else {
			this.mTxtAucunIncident.setVisibility(View.GONE);
		}	

		this.mAdapter.notifyDataSetChanged();
	}

	private void startGetIncidentsFromServiceAsync(String scope) {
		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"Début chargement des incidents.");
		
		if(this.mBoundService != null) {
			this.loadingDialog = ProgressDialog
					.show(IncidentsEnCoursActivity.this,
							"",
							getString(R.string.msg_incident_en_cours_list_loading_incidents));
			
			this.mBtnIgnorerFavoris.setVisibility(View.GONE);
			this.mTxtAucunIncident.setVisibility(View.GONE);
			
			this.mBoundService.startGetIncidentsAsync(scope);
		}
		else {
			Toast.makeText(this, R.string.msg_incident_en_cours_list_loading_incidents, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_FAVORIS) {
			Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
					"Fin activité Favoris : " + resultCode);
			if (resultCode == Activity.RESULT_OK) {
				mBoundService.startGetFavorisAsync();
			}
		}
		else if (requestCode == REQUEST_NEW_INCIDENT) {
			Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
					"Fin activité NewIncident : " + resultCode);
			if (resultCode == Activity.RESULT_OK) {
				mBoundService.startGetIncidentsAsync(mCurrentScope);
			}
		}
	}

	@Override
	public void actionPerformed(IncidentModel incident, IncidentAction action) {
		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"Choix d'une action :" + action);
		if (action.equals(IncidentAction.SHARE)) {
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("text/plain");

			share.putExtra(Intent.EXTRA_TEXT, String.format(
					getString(R.string.msg_share), incident.getLigne()
							.toString(),
					mBoundService.getUrlService() + "incident/detail/"
							+ incident.getId()));

			startActivity(Intent.createChooser(share,
					getString(R.string.msg_share_title)));
		} else {
			if(this.mBoundService != null) {
				this.mBoundService.startVoteIncident(incident.getId(), action);
			}
			else {
				Toast.makeText(this, R.string.msg_incident_en_cours_list_loading_incidents, Toast.LENGTH_LONG).show();
			}
		}
	}

	private class ServiceIncidentConnection implements ServiceConnection {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(getString(R.string.log_tag_name), "Service Connected!");

			mBoundService = ((IncidentsTransportsBackgroundServiceBinder) service)
					.getService();

			getIncidentsEnCoursListener = new IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener() {
				@Override
				public void dataChanged(
						List<IncidentModel> incidentsService) {
					try {
						Log.i(getString(R.string.log_tag_name),
								"Début du chargement des incidents.");
						if(incidentsService == null) {
							Toast.makeText(IncidentsEnCoursActivity.this, R.string.msg_incident_en_cours_list_load_incidents_KO, Toast.LENGTH_LONG).show();
						}
						else {
							setIncidents(incidentsService);
						}
					
						if(loadingDialog != null && loadingDialog.isShowing())
							loadingDialog.dismiss();
						
						Log.i(getString(R.string.log_tag_name),
								"Chargement des incidents réussi.");
					} catch (Exception e) {
						Log.e(getString(R.string.log_tag_name),
								"Problème de chargement des incidents",
								e);
						AlertDialog.Builder builder = new AlertDialog.Builder(
								IncidentsEnCoursActivity.this);
						builder.setMessage(
								getString(R.string.msg_incident_en_cours_list_load_incidents_KO))
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
					}
				}
			};
			mBoundService.addGetIncidentsListener(getIncidentsEnCoursListener);

			voteIncidentListener = new IIncidentsTransportsBackgroundServiceVoteIncidentListener() {

				@Override
				public void dataChanged(boolean voteSent) {
					Log.i(getString(R.string.log_tag_name) + " "
							+ TAG_ACTIVITY,
							"Retour de demande de vote.");
					if (voteSent) {
						Log.i(getString(R.string.log_tag_name),
								"Demande de vote réussi.");
						Toast.makeText(IncidentsEnCoursActivity.this,
								R.string.msg_vote_OK,
								Toast.LENGTH_SHORT).show();
						startGetIncidentsFromServiceAsync(mCurrentScope);
					} else {
						Log.i(getString(R.string.log_tag_name) + " "
								+ TAG_ACTIVITY,
								"Echec de la demande de vote.");
						Toast.makeText(IncidentsEnCoursActivity.this,
								R.string.msg_vote_KO,
								Toast.LENGTH_SHORT).show();
					}
				}
			};
			mBoundService.addVoteIncidentListener(voteIncidentListener);

			getFavorisListener = new IIncidentsTransportsBackgroundServiceGetFavorisListener() {
				@Override
				public void dataChanged(List<LigneModel> lignes) {
					Log.i(getString(R.string.log_tag_name) + " "
							+ TAG_ACTIVITY,
							"Retour de demande de chargement des favoris.");

					if (lignesFavoris != null) {
						lignesFavoris.clear();
					}

					if (lignes != null && lignes.size() > 0) {
						Log.d(getString(R.string.log_tag_name) + " "
								+ TAG_ACTIVITY, "Chargement de "
								+ lignes.size() + " favoris.");
						lignesFavoris = new ArrayList<LigneModel>();
						lignesFavoris.addAll(lignes);
						Log.d(getString(R.string.log_tag_name) + " "
								+ TAG_ACTIVITY,
								"Fin de chargement des favoris.");
					}

					startGetIncidentsFromServiceAsync(mCurrentScope);
				}
			};
			mBoundService.addGetFavorisListener(getFavorisListener);

			mBoundService.startGetFavorisAsync();
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService.removeGetIncidentsListener(getIncidentsEnCoursListener);
			mBoundService.removeVoteIncidentListener(voteIncidentListener);
			mBoundService.removeGetFavorisListener(getFavorisListener);
			mBoundService = null;
;		}
	};
	
	@Override
	protected void onStop() {
		super.onStop();
		
		if(loadingDialog != null) {
			loadingDialog.dismiss();
		}		
		
		unbindService(conn);
	}
}