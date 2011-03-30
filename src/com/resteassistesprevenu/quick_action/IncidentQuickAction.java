package com.resteassistesprevenu.quick_action;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.activities.listeners.IIncidentActionListener;
import com.resteassistesprevenu.model.IncidentAction;
import com.resteassistesprevenu.model.IncidentModel;

import android.view.View;
import android.view.View.OnClickListener;

public class IncidentQuickAction extends QuickAction {
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

		votePlusAction.setTitle("Plus");
		votePlusAction.setIcon(mView.getContext().getResources().getDrawable(
				R.drawable.ic_vote_plus));
		
		votePlusAction.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				callListener(IncidentAction.VOTE_PLUS);						
			}
		});
		
		final ActionItem voteMinusAction = new ActionItem();

		voteMinusAction.setTitle("Moins");
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
		if(listener != null) {
			listener.actionPerformed(incident, action);			
		}	
		
		dismiss();
	}
}
