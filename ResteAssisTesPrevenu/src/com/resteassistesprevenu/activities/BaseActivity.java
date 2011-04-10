package com.resteassistesprevenu.activities;

import android.app.Activity;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.resteassistesprevenu.R;

public abstract class BaseActivity extends Activity {
	/**
	 * La bannière de pub
	 */
	protected AdView adView;
		
	public void startAd() {
		// Look up the AdView as a resource and load a request.
		adView = (AdView) this.findViewById(R.id.adViewBanner);
		AdRequest request = new AdRequest();
		request.setTesting(true);
		adView.loadAd(request);
	}
}
