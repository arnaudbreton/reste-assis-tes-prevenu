package com.android.resteassistesprevenu.model.adapters;

import java.util.ArrayList;
import java.util.List;

import com.android.resteassistesprevenu.R;
import com.android.resteassistesprevenu.model.IncidentModel;
import com.android.resteassistesprevenu.model.TypeLigne;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
            	 	 ImageView imgTypeLigne = (ImageView) v.findViewById(R.id.textviewTypeLigne);
                     TextView tt = (TextView) v.findViewById(R.id.textviewLigneId);
                     TextView bt = (TextView) v.findViewById(R.id.textviewReason);
                     
                     if(imgTypeLigne != null) {
                    	 imgTypeLigne.setImageDrawable(getImageForLineType(incident.getTypeLigne()));
                     }
                     if (tt != null) {
                           tt.setText(incident.getLigne());                       
                     }
                     if(bt != null){
                           bt.setText(incident.getReason());
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
