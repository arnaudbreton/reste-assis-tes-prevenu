package com.resteassistesprevenu.model.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.model.TypeLigne;

public class IncidentModelArrayAdapter extends ArrayAdapter<IncidentModel> {
	private ArrayList<IncidentModel> incidents;
	
	public IncidentModelArrayAdapter(Context context,
			int textViewResourceId) {
		super(context,textViewResourceId);

		this.incidents = new ArrayList<IncidentModel>();
	}
	
	public IncidentModelArrayAdapter(Context context,
			int textViewResourceId, List<IncidentModel> objects) {
		super(context,textViewResourceId, objects);

		this.incidents = (ArrayList<IncidentModel>) objects;
	}

	 @Override
     public View getView(int position, View convertView, ViewGroup parent) {
             View v = convertView;
             if (v == null) {
                 LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                 v = vi.inflate(R.layout.incident_item_view, null);
             }
             IncidentModel incident = incidents.get(position);
             if (incident != null) {
            	 	 TextView txtLigne = (TextView) v.findViewById(R.id.txtLigne);
                     TextView txtHeureIncident = (TextView) v.findViewById(R.id.txtHeureIncident);
                     TextView txtReason = (TextView) v.findViewById(R.id.txtIncidentItemViewRaison);
                     
                     if(txtLigne != null) {
                    	 txtLigne.setText(incident.getTypeLigne().concat(" " + incident.getLigne()));
                     }
                     
                     if(txtReason != null){
                    	 txtReason.setText(incident.getReason());
                     }
                     
                     if(txtHeureIncident != null) {
                    	 txtHeureIncident.setText("@" + new SimpleDateFormat("HH:mm").format(incident.getLastModifiedTime()));
                     }
             }
             return v;
     }
	 
	 private Drawable getImageForLineType(TypeLigne typeLigne) {
		switch(typeLigne) {
		case METRO:
			return getContext().getResources().getDrawable(R.drawable.logo_metro);
		case RER:
			return getContext().getResources().getDrawable(R.drawable.logo_rer);
		case TRANSILIEN:
			return getContext().getResources().getDrawable(R.drawable.logo_transilien);
			default:
				return null;
		}
	}

	public void addIncidents(List<IncidentModel> newIncidents) {
		 this.incidents.addAll(newIncidents);
	 }
}
