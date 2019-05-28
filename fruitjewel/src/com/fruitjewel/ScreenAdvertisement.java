package com.fruitjewel;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.app.Activity;
import android.os.Handler;

public class ScreenAdvertisement {

	final Activity activity;
	final int advertisementId;

	final Handler adsHandler = new Handler();

	public ScreenAdvertisement(final Activity activity, final int advertisementId) {
		this.activity = activity;
		this.advertisementId = advertisementId;
	}

	//show the ads.
	private void showAds () {
		// Show the ad.
		AdView adView = (AdView)activity.findViewById(advertisementId);
		adView.setVisibility(android.view.View.VISIBLE);
		adView.setEnabled(true);

        AdRequest request = new AdRequest();
        request.setTesting(true);
        adView.loadAd(request);
	}

	//hide ads.
	private void unshowAds () {
		AdView adView = (AdView)activity.findViewById(advertisementId);
		adView.setVisibility(android.view.View.INVISIBLE);
		adView.setEnabled(false);
	}

	final Runnable unshowAdsRunnable = new Runnable() {
		public void run() {
			unshowAds();
		}
	};

	final Runnable showAdsRunnable = new Runnable() {
		public void run() {
			showAds();
		}
	};

	public void showAdvertisement() {
		adsHandler.post(showAdsRunnable);
	}

	public void hideAdvertisement() {
		adsHandler.post(unshowAdsRunnable);
	}
}
