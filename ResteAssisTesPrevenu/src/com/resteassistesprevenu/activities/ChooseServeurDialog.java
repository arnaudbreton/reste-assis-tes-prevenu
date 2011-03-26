package com.resteassistesprevenu.activities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import com.resteassistesprevenu.R;
import com.resteassistesprevenu.activities.listeners.ChooseServeurListener;

public class ChooseServeurDialog extends Dialog {

	private RadioButton mRadioProduction;
	private RadioButton mRadioPreProduction;

	private ChooseServeurListener mListener;
	private boolean isProduction;

	public ChooseServeurDialog(Context context, ChooseServeurListener listener,
			boolean isProduction) {
		super(context);

		this.mListener = listener;
		this.isProduction = isProduction;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.choose_serveur_dialog);
		setTitle(getContext().getString(R.string.title_choose_serveur));

		this.mRadioProduction = (RadioButton) findViewById(R.id.radioProduction);
		this.mRadioPreProduction = (RadioButton) findViewById(R.id.radioPreProduction);
		
		if(this.isProduction) {
			this.mRadioProduction.setChecked(true);
		}
		else {
			this.mRadioPreProduction.setChecked(true);
		}

		this.mRadioProduction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callListener(true);
			}
		});

		this.mRadioPreProduction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				callListener(false);
			}
		});
	}
	
	private void callListener(boolean isProduction) {
		if (mListener != null) {
			mListener.serveurChanged(isProduction);
		}
		
		dismiss();
	}
}
