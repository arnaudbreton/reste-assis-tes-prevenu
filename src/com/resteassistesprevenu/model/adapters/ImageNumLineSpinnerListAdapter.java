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

public class ImageNumLineSpinnerListAdapter extends ArrayAdapter<LigneModel> {
	private List<LigneModel> lignes;

	public ImageNumLineSpinnerListAdapter(Context context,
			int textViewResourceId, List<LigneModel> objects) {
		super(context, textViewResourceId, objects);

		this.lignes = objects;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.spinner_ligne_image_view, null);
		}

		LigneModel ligne = lignes.get(position);
		if (ligne != null) {
			ImageView imgLigne = (ImageView) v.findViewById(R.id.imgLigne);

			if (imgLigne != null) {
				imgLigne.setImageDrawable(getImage(ligne));
			}
		}

		return v;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.spinner_ligne_image_view, null);
		}

		LigneModel ligne = lignes.get(position);
		if (ligne != null) {
			ImageView imgLigne = (ImageView) v.findViewById(R.id.imgLigne);

			if (imgLigne != null) {
				imgLigne.setImageDrawable(getImage(ligne));
			}
		}

		return v;
	}

	public Drawable getImage(LigneModel ligne) {
		if (ligne != null) {
			int imageResource = getContext().getResources().getIdentifier(
					LigneModelService.getNumLigneImage(ligne.getTypeLigne(),
							ligne.getNumLigne()), "drawable",
					getContext().getPackageName());
			if (imageResource != 0) {
				return getContext().getResources().getDrawable(imageResource);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
}
