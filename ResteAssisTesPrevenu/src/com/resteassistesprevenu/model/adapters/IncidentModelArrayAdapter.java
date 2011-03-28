package com.resteassistesprevenu.model.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.activities.listeners.IIncidentActionListener;
import com.resteassistesprevenu.model.IncidentAction;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.model.TypeLigne;

public class IncidentModelArrayAdapter extends ArrayAdapter<IncidentModel> {
	private ArrayList<IncidentModel> incidents;
	private IIncidentActionListener listener;
	private IncidentModel incident;
	
	public IncidentModelArrayAdapter(Context context,
			int textViewResourceId, List<IncidentModel> objects, IIncidentActionListener listener) {
		super(context,textViewResourceId, objects);

		this.incidents = (ArrayList<IncidentModel>) objects;
		this.listener = listener;
	}

	 @Override
     public View getView(int position, View convertView, ViewGroup parent) {
             View v = convertView;
             if (v == null) {
                 LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                 v = vi.inflate(R.layout.incident_item_view, null);
             }
             incident = incidents.get(position);
             if (incident != null) {
            	 	 TextView txtLigne = (TextView) v.findViewById(R.id.txtLigne);
                     TextView txtHeureIncident = (TextView) v.findViewById(R.id.txtHeureIncident);
                     TextView txtReason = (TextView) v.findViewById(R.id.txtIncidentItemViewRaison);
                     
                     if(txtLigne != null) {
                    	 txtLigne.setText(incident.getTypeLigne().concat(" " + incident.getLigne()));
                     }
                     
                     if(txtReason != null){
                    	 txtReason.setText(incident.getReason());
                     }
                     
                     if(txtHeureIncident != null) {
                    	 txtHeureIncident.setText("@" + new SimpleDateFormat("HH:mm").format(incident.getLastModifiedTime()));
                     }
                     
                     v.setOnClickListener(new OnClickListener() {						
						@Override
						public void onClick(View v) {
							DemoPopupWindow dw = new DemoPopupWindow(v, incident, listener);
							dw.showLikeQuickAction(0, 30);							
						}
					});
             }
             return v;
     }
	 
	 private Drawable getImageForLineType(TypeLigne typeLigne) {
		switch(typeLigne) {
		case METRO:
			return getContext().getResources().getDrawable(R.drawable.logo_metro);
		case RER:
			return getContext().getResources().getDrawable(R.drawable.logo_rer);
		case TRANSILIEN:
			return getContext().getResources().getDrawable(R.drawable.logo_transilien);
			default:
				return null;
		}
	}

	public void addIncidents(List<IncidentModel> newIncidents) {
		 this.incidents.addAll(newIncidents);
	 }
	
	private static class DemoPopupWindow extends com.resteassistesprevenu.activities.BetterPopupWindow implements OnClickListener {
		private IIncidentActionListener listener;
		private IncidentModel incident;
		
		public DemoPopupWindow(View anchor, IncidentModel incident, IIncidentActionListener listener) {
			super(anchor);
			
			this.listener = listener;
			this.incident = incident;
		}

		@Override
		protected void onCreate() {
			// inflate layout
			LayoutInflater inflater =
					(LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			ViewGroup root = (ViewGroup) inflater.inflate(R.layout.popup_grid_layout, null);

			// setup button events
			for(int i = 0, icount = root.getChildCount() ; i < icount ; i++) {
				View v = root.getChildAt(i);

				if(v instanceof TableRow) {
					TableRow row = (TableRow) v;

					for(int j = 0, jcount = row.getChildCount() ; j < jcount ; j++) {
						View item = row.getChildAt(j);
						if(item instanceof ImageButton) {
							ImageButton b = (ImageButton) item;
							b.setOnClickListener(this);
						}
					}
				}
			}

			// set the inflated view as what we want to display
			this.setContentView(root);
		}

		@Override
		public void onClick(View v) {
			IncidentAction action;	
			
			switch (v.getId()) {
			case R.id.btnPlus:
				action = IncidentAction.VOTE_PLUS;
				break;
			case R.id.btnMinus:
				action = IncidentAction.VOTE_MINUS;
				break;
			case R.id.btnEnd:
				action = IncidentAction.VOTE_END;
				break;
			case R.id.btnShare:
				action = IncidentAction.SHARE;
				break;
			default:
				action = null;
				break;
			}
			
			if(listener != null) {
				listener.actionPerformed(incident, action);
			}
		
			this.dismiss();
		}
	}
}
