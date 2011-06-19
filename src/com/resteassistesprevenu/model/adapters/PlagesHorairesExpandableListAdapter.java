package com.resteassistesprevenu.model.adapters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.LigneModel;
import com.resteassistesprevenu.model.PlageHoraireModel;

public class PlagesHorairesExpandableListAdapter extends
		BaseExpandableListAdapter {
	private List<String> titlesGroups;
	private List<List<PlageHoraireModel>> plagesHorairesChildrenGroups;

	private Context ctx;
	private LayoutInflater inflater;

	/**
	 * Dernier groupe ouvert
	 */
	private int lastExpandedGroupPosition;

	/**
	 * ExpandableListView
	 */
	private ExpandableListView expandableListView;

	public PlagesHorairesExpandableListAdapter(Context ctx, ExpandableListView v) {
		this.ctx = ctx;
		this.inflater = LayoutInflater.from(ctx);

		this.titlesGroups = new ArrayList<String>();
		this.plagesHorairesChildrenGroups = new ArrayList<List<PlageHoraireModel>>();

		this.expandableListView = v;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return plagesHorairesChildrenGroups.get(groupPosition).get(
				childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View v = null;
		if (convertView != null)
			v = convertView;
		else
			v = inflater.inflate(R.layout.plages_horaires_item_view, parent,
					false);
		PlageHoraireModel plageHoraireModel = (PlageHoraireModel) getChild(
				groupPosition, childPosition);

		TextView txtPlageHoraire = (TextView) v
				.findViewById(R.id.txtPlageHoraire);

		txtPlageHoraire.setText(plageHoraireModel.toString());

		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int i = 0;
		try {
			i = plagesHorairesChildrenGroups.get(groupPosition).size();

		} catch (Exception e) {
		}

		return i;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return titlesGroups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return titlesGroups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View v;
		if (convertView != null)
			v = convertView;
		else
			v = inflater.inflate(R.layout.plages_horaires_group_view, parent,
					false);

		TextView txtTitre = (TextView) v
				.findViewById(R.id.txtTitrePlagesHoraires);
		ImageButton btnAddPlagesHoraires = (ImageButton) v
				.findViewById(R.id.btnAddPlagesHoraires);
		TextView lblNbFavoris = (TextView) v.findViewById(R.id.lblNbFavoris);

		lblNbFavoris.setText(String.valueOf(this.plagesHorairesChildrenGroups
				.get(groupPosition).size()));

		txtTitre.setText("Plages horaires de notification");
		btnAddPlagesHoraires.setOnClickListener(new View.OnClickListener() {
			private int heureDebut;
			private int minuteDebut;
			private int heureFin;
			private int minuteFin;
				
			@Override
			public void onClick(final View v) {
				final OnTimeSetListener callbackTimeSet2 = new OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {						
						heureFin = hourOfDay;
						minuteFin = minute;
						
						// Enregistrement de la plage horaire dans le service
						PlageHoraireModel plage = new PlageHoraireModel(heureDebut, minuteDebut, heureFin, minuteFin);
					}
				}; 
				
				OnTimeSetListener callbackTimeSet1 = new OnTimeSetListener() {
					
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
						heureDebut = hourOfDay;
						minuteDebut = minute;
						
						new TimePickerDialog(v.getContext(), callbackTimeSet2, 0, 0, true);
					}
				}; 
				
				
				new TimePickerDialog(v.getContext(), callbackTimeSet1, 0, 0, true);				
			}
			
			
		});

		return v;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public TextView getGenericView() {
		// Layout parameters for the ExpandableListView
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, 64);

		TextView tv = new TextView(this.ctx);
		tv.setLayoutParams(lp);

		// Center the text vertically
		tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		// Set the text starting position
		tv.setPadding(50, 0, 0, 0);
		return tv;
	}

	public List<List<PlageHoraireModel>> getPlagesHorairesChildrenGroups() {
		return plagesHorairesChildrenGroups;
	}

	public void onGroupExpanded(int groupPosition) {
		// collapse the old expanded group, if not the same
		// as new group to expand
		if (groupPosition != lastExpandedGroupPosition) {
			this.expandableListView.collapseGroup(lastExpandedGroupPosition);
		}

		super.onGroupExpanded(groupPosition);
		lastExpandedGroupPosition = groupPosition;
	}
}
