package com.resteassistesprevenu.model.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.LigneModel;
import com.resteassistesprevenu.model.LigneModelService;

public class FavorisExpandableListAdapter extends BaseExpandableListAdapter {
	private List<String> typeLignesGroups;
	private List<List<LigneModel>> lignesChildrenGroups;
	
	private Context ctx;
	private LayoutInflater inflater;	

	public FavorisExpandableListAdapter(Context ctx) {
		this.ctx = ctx;
		this.inflater = LayoutInflater.from(ctx);
		
		this.typeLignesGroups = new ArrayList<String>();
		this.lignesChildrenGroups = new ArrayList<List<LigneModel>>();
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
	            v = inflater.inflate(R.layout.favoris_item_view, parent, false); 
	        LigneModel ligneModel = (LigneModel)getChild( groupPosition, childPosition );
	        
			ImageView imgNumLigne = (ImageView)v.findViewById( R.id.imgNumLigne );
			
			int imageResource;
			Drawable image;			
		
			if( imgNumLigne != null ) {
				imageResource = ctx.getResources().getIdentifier(LigneModelService.getNumLigneImage(ligneModel.getTypeLigne(),ligneModel.getNumLigne()), "drawable", ctx.getPackageName());
				
				if(imageResource != 0) {
					image = ctx.getResources().getDrawable(imageResource);
					imgNumLigne.setImageDrawable(image);
					imgNumLigne.setPadding(60, 0, 0, 0);
				}
				else {
					imgNumLigne.setImageDrawable(null);	
				}				
			}	
						
			ImageButton btnFavorite = (ImageButton) v.findViewById(R.id.btnFavorite);
			
			btnFavorite.setFocusable(false);
			imgNumLigne.setFocusable(false);
			
			if(ligneModel.isFavoris()) {
				btnFavorite.setImageDrawable(ctx.getResources().getDrawable(android.R.drawable.star_big_on));
			}
			else {
				btnFavorite.setImageDrawable(ctx.getResources().getDrawable(android.R.drawable.star_big_off));
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
			View v;
			if( convertView != null )
	            v = convertView;
	        else
	            v = inflater.inflate(R.layout.favoris_group_view, parent, false); 
		  	String typeLigne = (String)getGroup(groupPosition);
		  	
		  	int nbFavorisGroup = 0;
		  	for(LigneModel ligne : this.lignesChildrenGroups.get(groupPosition)) {
		  		if(ligne.isFavoris()) {
		  			nbFavorisGroup++;
		  		}
		  	}
	        
			ImageView imgTypeLigne = (ImageView)v.findViewById( R.id.imgTypeLigne );
			TextView lblNbFavoris = (TextView)v.findViewById(R.id.lblNbFavoris);
			ImageView imgFavoris = (ImageView)v.findViewById(R.id.imgFavoris);
			
			int imageResource;
			Drawable image;			
		
			if( imgTypeLigne != null ) {
				imageResource = ctx.getResources().getIdentifier(LigneModelService.getTypeLigneImage(typeLigne), "drawable", ctx.getPackageName());
				image = ctx.getResources().getDrawable(imageResource);
				
				imgTypeLigne.setImageDrawable(image);
				imgTypeLigne.setPadding(60, 0, 0, 0);
			}					
			
			lblNbFavoris.setText(String.valueOf(nbFavorisGroup));
			
			if(nbFavorisGroup > 0) {
				imgFavoris.setImageDrawable(ctx.getResources().getDrawable(android.R.drawable.star_big_on));
			}
			else {
				imgFavoris.setImageDrawable(ctx.getResources().getDrawable(android.R.drawable.star_big_off));
			}
			
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

	public List<String> getTypeLignesGroups() {
		return typeLignesGroups;
	}

	public List<List<LigneModel>> getLignesChildrenGroups() {
		return lignesChildrenGroups;
	}
}
