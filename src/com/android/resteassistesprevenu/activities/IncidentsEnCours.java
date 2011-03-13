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
import android.os.Bundle;
import android.util.Log;

import com.android.resteassistesprevenu.model.IncidentModel;
import com.android.resteassistesprevenu.model.adapters.IncidentModelAdapter;

public class IncidentsEnCours extends ListActivity {

	String serviceURLBase = "http://openreact.alwaysdata.net";
	String serviceURLByHour = "/api/incidents.json/hour";
	String serviceURLRunning = "/api/incidents.json/current";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView()
		
		ArrayList<IncidentModel> incidents = new ArrayList<IncidentModel>();
		try {
			incidents = IncidentModel.deserializeFromArray(getIncidentsEnCours());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setListAdapter(new IncidentModelAdapter(this, android.R.layout.simple_list_item_1, incidents));
	}

	private String getIncidentsEnCours() throws JSONException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet request = new HttpGet(this.serviceURLBase + this.serviceURLRunning);
		String result = null; 
		
		//request.addHeader("deviceId", deviceId);
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
