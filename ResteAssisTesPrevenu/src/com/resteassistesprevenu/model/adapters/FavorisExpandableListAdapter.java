package com.resteassistesprevenu.model.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class FavorisExpandableListAdapter extends BaseExpandableListAdapter {
	private String[] typeLignesGroups;
	private String[][] lignesChildrenGroups;

	private Context ctx;

	public FavorisExpandableListAdapter(Context ctx, String[] typeLignesGroups,
			String[][] lignesChildrenGroups) {
		this.typeLignesGroups = typeLignesGroups;
		this.lignesChildrenGroups = lignesChildrenGroups;

		this.ctx = ctx;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return lignesChildrenGroups[groupPosition][childPosition];
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		TextView textView = getGenericView();
		textView.setText(getChild(groupPosition, childPosition).toString());
		return textView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int i = 0;
		try {
			i = lignesChildrenGroups[groupPosition].length;

		} catch (Exception e) {
		}

		return i;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return typeLignesGroups[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return typeLignesGroups.length;
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
		tv.setPadding(36, 0, 0, 0);
		return tv;
	}
}
