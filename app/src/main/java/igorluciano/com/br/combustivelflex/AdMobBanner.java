package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

final class AdMobBanner {
    private static final String MAIN_BANNER_AD_UNIT_ID = "ca-app-pub-1199102836233471/1062338832";
    private static final String RESULT_BANNER_AD_UNIT_ID = "ca-app-pub-1199102836233471/4710165144";

    private AdMobBanner() {
    }

    static AdView loadMainBanner(Activity activity, FrameLayout container) {
        return loadInto(activity, container, MAIN_BANNER_AD_UNIT_ID);
    }

    static AdView loadResultBanner(Activity activity, FrameLayout container) {
        return loadInto(activity, container, RESULT_BANNER_AD_UNIT_ID);
    }

    private static AdView loadInto(Activity activity, FrameLayout container, String adUnitId) {
        AdView adView = new AdView(activity);
        adView.setAdUnitId(adUnitId);
        adView.setAdSize(getAdSize(activity));

        container.removeAllViews();
        container.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());
        return adView;
    }

    private static AdSize getAdSize(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int adWidth = (int) (displayMetrics.widthPixels / displayMetrics.density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }
}
