package com.resteassistesprevenu.model.adapters;

import java.util.ArrayList;
import java.util.List;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.activities.listeners.IIncidentActionListener;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.model.LigneModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ImageLineSpinnerListAdapter extends ArrayAdapter<LigneModel> {
	private List<LigneModel> lignes;
	
	public ImageLineSpinnerListAdapter(Context context, int textViewResourceId,
			List<LigneModel> objects) {
		super(context, textViewResourceId, objects);
		
		this.lignes = objects;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.new_incident_view, null);
		}
		
		LigneModel ligne = lignes.get(position);
		if(ligne != null) {
			Spinner spinTypeLigne = (Spinner) v.findViewById(R.id.spinnerTypeLigne);
			Spinner spinNumLigne = (Spinner) v.findViewById(R.id.spinnerNumeroLigne);
		}
		
		return null;
	}
}
