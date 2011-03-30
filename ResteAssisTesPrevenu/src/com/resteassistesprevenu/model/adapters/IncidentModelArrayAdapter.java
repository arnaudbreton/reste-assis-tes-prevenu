package com.resteassistesprevenu.model.adapters;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.activities.listeners.IIncidentActionListener;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.quick_action.IncidentQuickAction;

public class IncidentModelArrayAdapter extends ArrayAdapter<IncidentModel> {
	private ArrayList<IncidentModel> incidents;
	private IIncidentActionListener listener;
	private IncidentModel incident;

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
		incident = incidents.get(position);
		if (incident != null) {
			TextView txtLigne = (TextView) v.findViewById(R.id.txtLigne);
			TextView txtHeureIncident = (TextView) v
					.findViewById(R.id.txtHeureIncident);
			TextView txtReason = (TextView) v
					.findViewById(R.id.txtIncidentItemViewRaison);

			TextView txtNbVotePlus = (TextView) v
					.findViewById(R.id.txtNbVotePlus);
			TextView txtNbVoteMinus = (TextView) v
					.findViewById(R.id.txtNbVoteMinus);
			TextView txtNbVoteEnd = (TextView) v
					.findViewById(R.id.txtNbVoteEnd);

			if (txtLigne != null) {
				txtLigne.setText(incident.getLigne().toString());
			}

			if (txtReason != null) {
				txtReason.setText(incident.getReason());
			}

			if (txtHeureIncident != null) {
				txtHeureIncident.setText("@"
						+ new SimpleDateFormat("HH:mm").format(incident
								.getLastModifiedTime()));
			}

			if (txtNbVotePlus != null) {
				txtNbVotePlus.setText(String.valueOf(incident.getVotePlus()));
			}

			if (txtNbVoteMinus != null) {
				txtNbVoteMinus.setText(String.valueOf(incident.getVoteMinus()));
			}

			if (txtNbVoteEnd != null) {
				txtNbVoteEnd.setText(String.valueOf(incident.getVoteEnded()));
			}

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
