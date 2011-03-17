package com.android.resteassistesprevenu.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.android.resteassistesprevenu.R;
import com.android.resteassistesprevenu.services.IIncidentsTransportsBackgroundService;

public class NewIncidentActivity extends Activity {	
	private IIncidentsTransportsBackgroundService mBoundService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_incident_view);
		
//		ServiceConnection connection = new ServiceConnection() {
//		    public void onServiceConnected(ComponentName className, IBinder service) {
//		        Log.i("BackgroundService", "Connected!"); 
//		    	mBoundService = ((IncidentsTransportsBackgroundServiceBinder)service).getService();		    	
//		    	
//		        mBoundService.addListener(new IIncidentsTransportsBackgroundServiceListener() {
//					
//					@Override
//					public void dataChanged(Object data) {
//						// TODO Auto-generated method stub						
//					}
//				});
//
//		    public void onServiceDisconnected(ComponentName className) 
//		    {		
//		    }
//		};
		
		//getApplicationContext().bindService(new Intent(".IncidentsBackgroundService.ACTION"), connection, Context.BIND_AUTO_CREATE);
	}
}
