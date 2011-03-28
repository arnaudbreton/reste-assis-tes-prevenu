package com.resteassistesprevenu.model.adapters;

import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.resteassistesprevenu.R;

public class FavorisExpandableListAdapter extends BaseExpandableListAdapter {
	private List<String> typeLignesGroups;
	private List<List<String>> lignesChildrenGroups;

	private Context ctx;
	private LayoutInflater inflater;

	public FavorisExpandableListAdapter(Context ctx, List<String> typeLignesGroups,
			List<List<String>> lignesChildrenGroups) {
		this.typeLignesGroups = typeLignesGroups;
		this.lignesChildrenGroups = lignesChildrenGroups;

		this.ctx = ctx;
		this.inflater = LayoutInflater.from(ctx);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return lignesChildrenGroups.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		 View v = null;
	        if( convertView != null )
	            v = convertView;
	        else
	            v = inflater.inflate(R.layout.favorite_item_view, parent, false); 
	        String c = (String)getChild( groupPosition, childPosition );
			TextView txtLigne = (TextView)v.findViewById( R.id.txtLigne );
			if( txtLigne != null ) {
				txtLigne.setText( c );
				txtLigne.setPadding(60, 0, 0, 0);
			}
			

			
	        return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int i = 0;
		try {
			i = lignesChildrenGroups.get(groupPosition).size();

		} catch (Exception e) {
		}

		return i;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return typeLignesGroups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return typeLignesGroups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView textView = getGenericView();
		textView.setText(getGroup(groupPosition).toString());
		return textView;
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

	public List<String> getTypeLignesGroups() {
		return typeLignesGroups;
	}

	public void setTypeLignesGroups(List<String> typeLignesGroups) {
		this.typeLignesGroups = typeLignesGroups;
	}

	public List<List<String>> getLignesChildrenGroups() {
		return lignesChildrenGroups;
	}

	public void setLignesChildrenGroups(List<List<String>> lignesChildrenGroups) {
		this.lignesChildrenGroups = lignesChildrenGroups;
	}
}
