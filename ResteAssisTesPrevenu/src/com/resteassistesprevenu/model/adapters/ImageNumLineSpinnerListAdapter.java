package com.resteassistesprevenu.model.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.LigneModel;
import com.resteassistesprevenu.model.LigneModelService;

public class ImageNumLineSpinnerListAdapter extends ArrayAdapter<LigneModel> {
	private final Object mLock = new Object();
	private List<LigneModel> lignes;
	private List<LigneModel> filteredLignes;
	private TypeLigneFilter filter;

	public ImageNumLineSpinnerListAdapter(Context context,
			int textViewResourceId, List<LigneModel> objects) {
		super(context, textViewResourceId, objects);

		this.lignes = objects;
		this.filteredLignes = objects;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.spinner_ligne_image_view, null);
		}

		LigneModel ligne = this.filteredLignes.get(position);
		if (ligne != null) {
			ImageView imgLigne = (ImageView) v.findViewById(R.id.imgLigne);

			if (imgLigne != null) {
				imgLigne.setImageDrawable(getImage(ligne));
			}
		}

		return v;
	}
	
    @Override
    public int getCount() {
        return filteredLignes.size();
    }

    @Override
    public LigneModel getItem(int position) {
        return filteredLignes.get(position);
    }

    @Override
    public int getPosition(LigneModel item) {
        return filteredLignes.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.spinner_ligne_image_view, null);
		}

		LigneModel ligne = this.filteredLignes.get(position);
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

	@Override
	public Filter getFilter() {
		if (filter == null)
			filter = new TypeLigneFilter();
		return filter;
	}
	
	private class TypeLigneFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults result = new FilterResults();

			if (constraint != null && constraint.toString().length() > 0) {
				ArrayList<LigneModel> filt = new ArrayList<LigneModel>();
				ArrayList<LigneModel> lItems = new ArrayList<LigneModel>();
				synchronized (mLock) {
					lItems.addAll(lignes);
				}
				for (int i = 0, l = lItems.size(); i < l; i++) {
					LigneModel ligne = lItems.get(i);
					if (ligne.getTypeLigne().equals(constraint))
						filt.add(ligne);
				}
				result.count = filt.size();
				result.values = filt;
			} else {
				synchronized (mLock) {
					result.values = lignes;
					result.count = lignes.size();
				}
			}
			return result;
		}

		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			filteredLignes = (ArrayList<LigneModel>)results.values;
			if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
		}
	};
}
