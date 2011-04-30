package com.resteassistesprevenu.model.adapters;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.activities.listeners.IIncidentActionListener;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.quick_action.IncidentQuickAction;

public class IncidentModelArrayAdapter extends ArrayAdapter<IncidentModel> {
	private ArrayList<IncidentModel> incidents;
	private IIncidentActionListener listener;

	public IncidentModelArrayAdapter(Context context, int textViewResourceId,
			List<IncidentModel> objects, IIncidentActionListener listener) {
		super(context, textViewResourceId, objects);

		this.incidents = (ArrayList<IncidentModel>) objects;
		this.listener = listener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.incident_item_view, null);
		}
		
		final IncidentModel incident = incidents.get(position);
		if (incident != null) {			
			v = IncidentModelAdapter.getIncidentView(getContext(), incident);

			v.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {	
					new IncidentQuickAction(v, listener, incident).show();
				}
			});
		}
		return v;
	}

	public void addIncidents(List<IncidentModel> newIncidents) {
		this.incidents.addAll(newIncidents);
	}
}
