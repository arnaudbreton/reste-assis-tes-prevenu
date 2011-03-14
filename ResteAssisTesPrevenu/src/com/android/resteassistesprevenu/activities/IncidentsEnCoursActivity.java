package com.android.resteassistesprevenu.activities;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.android.resteassistesprevenu.R;
import com.android.resteassistesprevenu.model.IncidentModel;
import com.android.resteassistesprevenu.model.adapters.IncidentModelArrayAdapter;

public class IncidentsEnCoursActivity extends ListActivity {	
	
	private ProgressDialog pd; 
	private ArrayList<IncidentModel> incidents;
	private LoadIncidentsAsyncTask loadIncidentsTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		new LoadIncidentsAsyncTask(this).execute();
	}
	
	private class LoadIncidentsAsyncTask extends AsyncTask<Void, Void, Void> 
	{
		private String serviceURLBase = "http://openreact.alwaysdata.net";
		private String serviceURLByHour = "/api/incidents.json/hour";
		private String serviceURLRunning = "/api/incidents.json/current";

		private Context c; 
		private ProgressDialog pd;
		
		public LoadIncidentsAsyncTask(Context c) {
			this.c = c;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = ProgressDialog.show(c, "", "Chargement des incidents en cours");
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pd.dismiss();
			
		    setListAdapter(new IncidentModelArrayAdapter(  
                    IncidentsEnCoursActivity.this, R.layout.incident_item_view, incidents));  
		}		
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				incidents = IncidentModel.deserializeFromArray(getIncidentsEnCours());
			} catch (JSONException e) {
				return null;
			} catch (ParseException e) {
				return null;
			}			
			
			return null;
		}
		
		private String getIncidentsEnCours() throws JSONException {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet request = new HttpGet(this.serviceURLBase + this.serviceURLRunning);
			String result = null; 		
			
			ResponseHandler<String> handler = new BasicResponseHandler();
			try {
				result = httpclient.execute(request, handler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			httpclient.getConnectionManager().shutdown();
			Log.i("ResteAssisTesPrevenu : ", result);
			
			return result;
		}	
	}
}