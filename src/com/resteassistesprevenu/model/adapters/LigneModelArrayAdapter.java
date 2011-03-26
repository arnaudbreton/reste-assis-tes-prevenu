package com.resteassistesprevenu.model.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.model.TypeLigne;

public class LigneModelArrayAdapter extends ArrayAdapter<IncidentModel> {
	private ArrayList<IncidentModel> lignes;
	
	public LigneModelArrayAdapter(Context context,
			int textViewResourceId) {
		super(context,textViewResourceId);

		this.lignes = new ArrayList<IncidentModel>();
	}
	
	public LigneModelArrayAdapter(Context context,
			int textViewResourceId, List<IncidentModel> objects) {
		super(context,textViewResourceId, objects);

		this.lignes = (ArrayList<IncidentModel>) objects;
	}

	 @Override
     public View getView(int position, View convertView, ViewGroup parent) {
             View v = convertView;
             if (v == null) {
                 LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                 v = vi.inflate(R.layout.new_incident_view, null);
             }
             IncidentModel ligne = lignes.get(position);
             if (ligne != null) {
            	 	 Spinner spinTypeLigne = (Spinner) v.findViewById(R.id.spinnerTypeLigne);
            	 	 Spinner spinNumeroLigne = (Spinner) v.findViewById(R.id.spinnerTypeLigne);
                     
            	 	 if(spinTypeLigne != null) {            	 		
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
		 this.lignes.addAll(newIncidents);
	 }
}
