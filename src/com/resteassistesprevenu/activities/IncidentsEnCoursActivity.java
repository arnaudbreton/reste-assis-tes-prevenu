package com.resteassistesprevenu.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import android.widget.ImageButton;
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
 * Activit� listant les incidents
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
	 * Ensemble des incidents affich�s
	 */
	private List<IncidentModel> incidentsServiceDisplay;

	/**
	 * Liste des incidents charg�s
	 */
	private List<IncidentModel> incidentsService;

	/**
	 * Adapter des incidents mod�le => listeView
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
	private ImageButton mBtnAddIncident;
	
	/**
	 * ImageButton de rafra�chissement
	 */
	private ImageButton mBtnRefresh;


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
	private static final int REQUEST_NEW_INCIDENT = 101;

	/**
	 * Mode de chargement (avec/sans favoris)
	 */
	private ModeChargement mModeChargement;

	/**
	 * Le gestionnaire de connexion
	 */
	private ServiceIncidentConnection conn;

	/**
	 * Listener de r�cup�ration des incidents
	 */
	private IIncidentsTransportsBackgroundServiceGetIncidentsEnCoursListener getIncidentsEnCoursListener;

	/**
	 * Listener de vote pour les incidents
	 */
	private IIncidentsTransportsBackgroundServiceVoteIncidentListener voteIncidentListener;

	/**
	 * Listener de r�cup�ration des favoris
	 */
	private IIncidentsTransportsBackgroundServiceGetFavorisListener getFavorisListener;

	/**
	 * Timestamp du dernier chargement
	 */
	private long lastLoadingTimestamp;
	

	/**
	 * Dur�e de validit� des donn�es charg�es (10 minutes)
	 */
	private static final int MAX_DATA_VALIDITY_PERIOD = 10;

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
				IncidentsTransportsBackgroundService.class), this.conn,
				Context.BIND_AUTO_CREATE);
	}

	private void initialize() {

		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"D�but initialisation de l'activit�.");
		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"R�cup�ration des contr�les.");

		this.lastLoadingTimestamp = 0;

		this.mBtnJour = (RadioButton) this.findViewById(R.id.radioJour);
		this.mBtnHeure = (RadioButton) this.findViewById(R.id.radioHeure);
		this.mBtnMinute = (RadioButton) this.findViewById(R.id.radioMinute);
		this.mBtnAddIncident = (ImageButton) this
				.findViewById(R.id.btnAjouterIncident);
		this.mBtnRefresh = (ImageButton) this.findViewById(R.id.btnRefreshIncident);
		this.mBtnIgnorerFavoris = (Button) this
				.findViewById(R.id.btnIgnorerFavoris);

		this.mTxtAucunIncident = (TextView) findViewById(R.id.txtAucunIncident);

		this.incidentsServiceDisplay = new ArrayList<IncidentModel>();
		this.incidentsService = new ArrayList<IncidentModel>();

		this.mCurrentScope = IncidentModel.SCOPE_HOUR;

		this.mModeChargement = ModeChargement.NORMAL;

		startAd();

		this.mAdapter = new IncidentModelArrayAdapter(this,
				R.id.listViewIncidentEnCours, this.incidentsServiceDisplay,
				this);
		((android.widget.ListView) this
				.findViewById(R.id.listViewIncidentEnCours))
				.setAdapter(mAdapter);
		
		this.mBtnRefresh.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {		
				lastLoadingTimestamp = 0;
				startGetIncidentsFromServiceAsync();
			}
		});

		this.mBtnAddIncident.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IncidentsEnCoursActivity.this.startActivityForResult(
						new Intent(IncidentsEnCoursActivity.this,
								NewIncidentActivity.class),
						REQUEST_NEW_INCIDENT);
			}
		});

		this.mBtnJour.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mModeChargement = ModeChargement.NORMAL;
				
				if (mCurrentScope != IncidentModel.SCOPE_JOUR) {
					mCurrentScope = IncidentModel.SCOPE_JOUR;					
				}
				
				lastLoadingTimestamp = 0;
				
				startGetIncidentsFromServiceAsync();
			}
		});

		this.mBtnHeure.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mModeChargement = ModeChargement.NORMAL;

				if (mCurrentScope != IncidentModel.SCOPE_HOUR) {
					mCurrentScope = IncidentModel.SCOPE_HOUR;					
				}
				
				lastLoadingTimestamp = 0;

				startGetIncidentsFromServiceAsync();
			}
		});

		this.mBtnMinute.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mModeChargement = ModeChargement.NORMAL;

				if (mCurrentScope != IncidentModel.SCOPE_MINUTE) {
					mCurrentScope = IncidentModel.SCOPE_MINUTE;
				}
				
				lastLoadingTimestamp = 0;

				startGetIncidentsFromServiceAsync();
			}
		});

		this.mBtnIgnorerFavoris.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mModeChargement = ModeChargement.IGNORER_FAVORIS;
				mBtnIgnorerFavoris.setVisibility(View.GONE);

				startGetIncidentsFromServiceAsync();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"D�but cr�ation du menu");
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
		builder.setMessage(getString(R.string.msg_voter))
				.setCancelable(true)
				.setPositiveButton(getString(R.string.msg_btn_voter),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri
										.parse("market://details?id=com.resteassistesprevenu"));
								startActivity(intent);
							}
						})
				.setNegativeButton("Annuler",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
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
				"D�but cr�ation AlertDialog choix du serveur");
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

							lastLoadingTimestamp = 0;
							IncidentsEnCoursActivity.this
									.startGetIncidentsFromServiceAsync();
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
	public void showIncidents() {
		if (this.incidentsServiceDisplay == null) {
			this.incidentsServiceDisplay = new ArrayList<IncidentModel>();
		}

		this.incidentsServiceDisplay.clear();

		if (this.incidentsService != null && this.incidentsService.size() > 0) {
			// Pr�sence de favoris
			if (this.mModeChargement.equals(ModeChargement.NORMAL)
					&& lignesFavoris != null && lignesFavoris.size() > 0) {
				Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
						"Favoris pr�sent");

				for (IncidentModel incident : this.incidentsService) {
					if (this.lignesFavoris.contains(incident.getLigne())) {
						this.incidentsServiceDisplay.add(incident);
					}
				}

				if (this.incidentsServiceDisplay.size() == 0) {
					this.mTxtAucunIncident
							.setText(getString(R.string.msg_no_incident_favoris));
					this.mBtnIgnorerFavoris.setVisibility(View.VISIBLE);
				}
				// Si moins d'incidents que ceux du service, on donne la
				// possibilit� de tout voir
				else if (this.incidentsServiceDisplay.size() < this.incidentsService
						.size()) {
					this.mTxtAucunIncident
							.setText(getString(R.string.msg_other_incidents_favoris));
					this.mBtnIgnorerFavoris.setVisibility(View.VISIBLE);
				}
			} else {
				Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
						"Aucun favoris");

				this.incidentsServiceDisplay.addAll(this.incidentsService);

				if (this.incidentsServiceDisplay.size() == 0) {
					this.mTxtAucunIncident
							.setText(getString(R.string.msg_no_incident));
				}
			}
		} else {
			this.mTxtAucunIncident.setText(getString(R.string.msg_no_incident));
		}

		if (this.incidentsServiceDisplay.size() == 0
				|| this.incidentsServiceDisplay.size() < this.incidentsService
						.size()) {
			this.mTxtAucunIncident.setVisibility(View.VISIBLE);
		} else {
			this.mTxtAucunIncident.setVisibility(View.GONE);
		}

		this.mAdapter.notifyDataSetChanged();
	}

	private void startGetIncidentsFromServiceAsync() {
		Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
				"D�but chargement des incidents.");

		if (this.mBoundService != null) {
			if (this.loadingDialog != null) {
				loadingDialog.dismiss();
			}

			this.mBtnIgnorerFavoris.setVisibility(View.GONE);
			this.mTxtAucunIncident.setVisibility(View.GONE);

			if (isDataValid()) {
				showIncidents();
			} else {
				this.loadingDialog = ProgressDialog
						.show(IncidentsEnCoursActivity.this,
								"",
								getString(R.string.msg_incident_en_cours_list_loading_incidents));
				this.mBoundService.startGetIncidentsAsync(mCurrentScope,
						getIncidentsEnCoursListener);
			}
		} else {
			Toast.makeText(this,
					R.string.msg_incident_en_cours_list_loading_incidents,
					Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_FAVORIS) {
			Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
					"Fin activit� Favoris : " + resultCode);
			if (resultCode == Activity.RESULT_OK) {
				mBoundService.startGetFavorisAsync(getFavorisListener);
			}
		} else if (requestCode == REQUEST_NEW_INCIDENT) {
			Log.d(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
					"Fin activit� NewIncident : " + resultCode);
			if (resultCode == Activity.RESULT_OK) {
				lastLoadingTimestamp = 0;
				//startGetIncidentsFromServiceAsync();
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
							.toString(), mBoundService.getUrlService()
							+ "incident/detail/" + incident.getId()));

			startActivity(Intent.createChooser(share,
					getString(R.string.msg_share_title)));
		} else {
			if (this.mBoundService != null) {
				this.mBoundService.startVoteIncident(incident, action,
						voteIncidentListener);
			} else {
				Toast.makeText(this,
						R.string.msg_incident_en_cours_list_loading_incidents,
						Toast.LENGTH_LONG).show();
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
				public void dataChanged(List<IncidentModel> incidentsService) {
					try {
						Log.i(getString(R.string.log_tag_name),
								"D�but du chargement des incidents.");
						if (incidentsService == null) {
							Toast.makeText(
									IncidentsEnCoursActivity.this,
									R.string.msg_incident_en_cours_list_load_incidents_KO,
									Toast.LENGTH_LONG).show();
						} else {
							lastLoadingTimestamp = Calendar.getInstance().getTime().getTime();

							IncidentsEnCoursActivity.this.incidentsService
									.clear();
							IncidentsEnCoursActivity.this.incidentsService
									.addAll(incidentsService);
							
							mBtnRefresh.clearAnimation();

							showIncidents();
						}

						if (loadingDialog != null && loadingDialog.isShowing())
							loadingDialog.dismiss();

						Log.i(getString(R.string.log_tag_name),
								"Chargement des incidents r�ussi.");
					} catch (Exception e) {
						Log.e(getString(R.string.log_tag_name),
								"Probl�me de chargement des incidents", e);
						AlertDialog.Builder builder = new AlertDialog.Builder(
								IncidentsEnCoursActivity.this);
						builder.setMessage(
								getString(R.string.msg_incident_en_cours_list_load_incidents_KO))
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
			};

			voteIncidentListener = new IIncidentsTransportsBackgroundServiceVoteIncidentListener() {

				@Override
				public void dataChanged(boolean voteSent) {
					Log.i(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
							"Retour de demande de vote.");
					if (voteSent) {
						Log.i(getString(R.string.log_tag_name),
								"Demande de vote r�ussi.");
						Toast.makeText(IncidentsEnCoursActivity.this,
								R.string.msg_vote_OK, Toast.LENGTH_SHORT)
								.show();
						
						startGetIncidentsFromServiceAsync();
					} else {
						Log.i(getString(R.string.log_tag_name) + " "
								+ TAG_ACTIVITY, "Echec de la demande de vote.");
						Toast.makeText(IncidentsEnCoursActivity.this,
								R.string.msg_vote_KO, Toast.LENGTH_SHORT)
								.show();
					}
				}
			};

			getFavorisListener = new IIncidentsTransportsBackgroundServiceGetFavorisListener() {
				@Override
				public void dataChanged(List<LigneModel> lignes) {
					Log.i(getString(R.string.log_tag_name) + " " + TAG_ACTIVITY,
							"Retour de demande de chargement des favoris.");

					if (lignesFavoris != null) {
						lignesFavoris.clear();
					}

					if (lignes != null && lignes.size() > 0) {
						Log.d(getString(R.string.log_tag_name) + " "
								+ TAG_ACTIVITY,
								"Chargement de " + lignes.size() + " favoris.");
						lignesFavoris = new ArrayList<LigneModel>();
						lignesFavoris.addAll(lignes);
						Log.d(getString(R.string.log_tag_name) + " "
								+ TAG_ACTIVITY,
								"Fin de chargement des favoris.");
					}

					startGetIncidentsFromServiceAsync();
				}
			};

			mBoundService.startGetFavorisAsync(getFavorisListener);
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};

	private boolean isDataValid() {		
		if(this.lastLoadingTimestamp > 0) {
			GregorianCalendar c1 = new GregorianCalendar();
			c1.setTimeInMillis(this.lastLoadingTimestamp);
			c1.add(Calendar.MINUTE, MAX_DATA_VALIDITY_PERIOD);
			
			GregorianCalendar c2 = new GregorianCalendar();
			c2.setTimeInMillis(System.currentTimeMillis());
			
			return c2.before(c1);
		}
		else {
			return false;
		}
		
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (loadingDialog != null) {
			loadingDialog.dismiss();
		}

		unbindService(conn);
	}
}