package com.resteassistesprevenu.model.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.LigneModel;
import com.resteassistesprevenu.model.LigneModelService;

public class ImageTypeLineSpinnerListAdapter extends ArrayAdapter<String> {

	public ImageTypeLineSpinnerListAdapter(Context context,
			int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.spinner_ligne_image_view, null);
		}
		
		String typeLigne = getItem(position);
		if(typeLigne != null) {
			ImageView imgLigne = (ImageView) v.findViewById(R.id.imgLigne);
			
			if(imgLigne != null) {
				imgLigne.setImageDrawable(getImage(typeLigne));
			}
		}
		
		return null;
	}
	
	public Drawable getImage(String typeLigne) {
		if(typeLigne  != null) {
			int imageResource = getContext().getResources().getIdentifier(LigneModelService.getTypeLigneImage(typeLigne), "drawable", getContext().getPackageName());
			if(imageResource != 0) {
				return getContext().getResources().getDrawable(imageResource);
			}	
			else {
				return null;
			}			
		}
		else {
			return null;
		}
	}
}
