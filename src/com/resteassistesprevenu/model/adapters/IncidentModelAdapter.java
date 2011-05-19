package com.resteassistesprevenu.model.adapters;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.model.IncidentModel;
import com.resteassistesprevenu.model.LigneModelService;

public class IncidentModelAdapter {

	public static View getIncidentView(Context context, IncidentModel incident) {
		View v;

		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = vi.inflate(R.layout.incident_item_view, null);

		if (incident != null) {
			ImageView imgTypeLigne = (ImageView) v
					.findViewById(R.id.imgTypeLigne);
			ImageView imgNumLigne = (ImageView) v
					.findViewById(R.id.imgNumLigne);

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

			int imageResource;
			Drawable image;

			if (imgTypeLigne != null) {
				imageResource = context.getResources().getIdentifier(
						LigneModelService.getTypeLigneImage(incident.getLigne()
								.getTypeLigne()), "drawable",
						context.getPackageName());
				if (imageResource != 0) {
					image = context.getResources().getDrawable(imageResource);
					imgTypeLigne.setImageDrawable(image);
				} else {
					imgNumLigne.setImageDrawable(null);
				}
			}

			if (imgNumLigne != null) {
				imageResource = context.getResources().getIdentifier(
						LigneModelService.getNumLigneImage(incident.getLigne()
								.getTypeLigne(), incident.getLigne()
								.getNumLigne()), "drawable",
						context.getPackageName());
				if (imageResource != 0) {
					image = context.getResources().getDrawable(imageResource);
					imgNumLigne.setImageDrawable(image);
				} else {
					imgNumLigne.setImageDrawable(null);
				}
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
		}

		return v;
	}

	public static RemoteViews getIncidentRemoteView(Context context, RemoteViews v,
			IncidentModel incident, int position) {

		if (incident != null) {
			int imageResource;

			imageResource = context.getResources().getIdentifier(
					LigneModelService.getTypeLigneImage(incident.getLigne()
							.getTypeLigne()), "drawable",
					context.getPackageName());
			if (imageResource != 0) {
				v.setImageViewResource(R.id.imgTypeLigne, imageResource);
			}

			imageResource = context.getResources()
					.getIdentifier(
							LigneModelService.getNumLigneImage(incident
									.getLigne().getTypeLigne(), incident
									.getLigne().getNumLigne()), "drawable",
							context.getPackageName());
			if (imageResource != 0) {

				v.setImageViewResource(R.id.imgNumLigne, imageResource);
			}

			v.setTextViewText(R.id.txtIncidentItemViewRaison, incident.getReason());

			v.setTextViewText(R.id.txtHeureIncident,
					("@" + new SimpleDateFormat("HH:mm").format(incident
							.getLastModifiedTime())));
			
			v.setTextViewText(R.id.txtIndIncident, String.valueOf(position));

			v.setTextViewText(R.id.txtNbVotePlus,
					String.valueOf(incident.getVotePlus()));
			v.setTextViewText(R.id.txtNbVoteMinus,
					String.valueOf(incident.getVoteMinus()));
			v.setTextViewText(R.id.txtNbVoteEnd,
					String.valueOf(incident.getVoteEnded()));
		}

		return v;
	}
}
