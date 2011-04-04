package com.resteassistesprevenu.quick_action;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.activities.listeners.IIncidentActionListener;
import com.resteassistesprevenu.model.IncidentAction;
import com.resteassistesprevenu.model.IncidentModel;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * QuickAction lié à un incident
 * @author Arnaud
 *
 */
public class IncidentQuickAction extends QuickAction {
	private final static String TAG_QUICK_ACTION = "IncidentQuickAction";
	
	private View mView;
	private IIncidentActionListener listener;
	private IncidentModel incident;
	
	public IncidentQuickAction(View anchor, IIncidentActionListener listener, IncidentModel incident) {
		super(anchor);
		
		this.mView = anchor;
		this.listener = listener;
		this.incident = incident;
		
		initializeActionItem();
	}

	private void initializeActionItem() {
		final ActionItem votePlusAction = new ActionItem();

		votePlusAction.setTitle("Confirmer");
		votePlusAction.setIcon(mView.getContext().getResources().getDrawable(
				R.drawable.ic_vote_plus));
		
		votePlusAction.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				callListener(IncidentAction.VOTE_PLUS);						
			}
		});
		
		final ActionItem voteMinusAction = new ActionItem();

		voteMinusAction.setTitle("Infirmer");
		voteMinusAction.setIcon(mView.getContext().getResources().getDrawable(
				R.drawable.ic_vote_minus));
		
		voteMinusAction.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				callListener(IncidentAction.VOTE_MINUS);							
			}
		});
		
		final ActionItem voteEndAction = new ActionItem();

		voteEndAction.setTitle("Terminé");
		voteEndAction.setIcon(mView.getContext().getResources().getDrawable(
				R.drawable.ic_vote_end));
		
		voteEndAction.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				callListener(IncidentAction.VOTE_END);						
			}
		});
		
		final ActionItem shareAction = new ActionItem();		

		shareAction.setTitle("Partager");
		shareAction.setIcon(mView.getContext().getResources().getDrawable(
				R.drawable.ic_share));	
		
		shareAction.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				callListener(IncidentAction.SHARE);			
			}
		});
		
		addActionItem(votePlusAction);
		addActionItem(voteMinusAction);					
		addActionItem(voteEndAction);
		addActionItem(shareAction);
		
		setAnimStyle(QuickAction.ANIM_AUTO);		
	}
	
	private void callListener(IncidentAction action) {
		Log.d(mView.getContext().getString(R.string.log_tag_name) + " " + TAG_QUICK_ACTION, "Appel au listener pour l'action : " + action);
		if(listener != null) {
			listener.actionPerformed(incident, action);			
		}	
		
		dismiss();
	}
}
