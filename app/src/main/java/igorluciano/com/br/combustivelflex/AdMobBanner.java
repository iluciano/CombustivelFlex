package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

final class AdMobBanner {
    private static final String MAIN_BANNER_AD_UNIT_ID = "ca-app-pub-1199102836233471/9002359835";
    private static final String RESULT_BANNER_AD_UNIT_ID = "ca-app-pub-1199102836233471/5072740189";
    private static final String MORE_NATIVE_AD_UNIT_ID = "ca-app-pub-1199102836233471/3525523095";
    private static final String TIPS_BANNER_AD_UNIT_ID = "ca-app-pub-1199102836233471/3058619235";
    private static final String SETTINGS_BANNER_AD_UNIT_ID = "ca-app-pub-1199102836233471/2035295102";

    private AdMobBanner() {
    }

    static AdView loadMainBanner(Activity activity, FrameLayout container) {
        return loadInto(activity, container, MAIN_BANNER_AD_UNIT_ID);
    }

    static AdView loadResultBanner(Activity activity, FrameLayout container) {
        return loadInto(activity, container, RESULT_BANNER_AD_UNIT_ID);
    }

    static void loadMoreNativeAd(Activity activity, FrameLayout container, NativeAdLoadedCallback callback) {
        container.removeAllViews();
        container.setVisibility(View.GONE);

        AdLoader adLoader = new AdLoader.Builder(activity, MORE_NATIVE_AD_UNIT_ID)
                .forNativeAd(nativeAd -> {
                    if (activity.isDestroyed()) {
                        nativeAd.destroy();
                        return;
                    }

                    NativeAdView adView = (NativeAdView) LayoutInflater.from(activity)
                            .inflate(R.layout.native_more_ad, container, false);
                    populateNativeAdView(nativeAd, adView);

                    container.removeAllViews();
                    container.addView(adView);
                    container.setVisibility(View.VISIBLE);
                    callback.onNativeAdLoaded(nativeAd);
                })
                .withAdListener(new com.google.android.gms.ads.AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        container.removeAllViews();
                        container.setVisibility(View.GONE);
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    static AdView loadTipsBanner(Activity activity, FrameLayout container) {
        return loadInto(activity, container, TIPS_BANNER_AD_UNIT_ID);
    }

    static AdView loadSettingsBanner(Activity activity, FrameLayout container) {
        return loadInto(activity, container, SETTINGS_BANNER_AD_UNIT_ID);
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

    private static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.native_more_ad_media);
        TextView headlineView = adView.findViewById(R.id.native_more_ad_headline);
        TextView bodyView = adView.findViewById(R.id.native_more_ad_body);
        ImageView iconView = adView.findViewById(R.id.native_more_ad_icon);
        Button callToActionView = adView.findViewById(R.id.native_more_ad_call_to_action);

        adView.setMediaView(mediaView);
        adView.setHeadlineView(headlineView);
        adView.setBodyView(bodyView);
        adView.setIconView(iconView);
        adView.setCallToActionView(callToActionView);

        headlineView.setText(nativeAd.getHeadline());
        mediaView.setMediaContent(nativeAd.getMediaContent());

        if (nativeAd.getBody() == null) {
            bodyView.setVisibility(View.GONE);
        } else {
            bodyView.setText(nativeAd.getBody());
            bodyView.setVisibility(View.VISIBLE);
        }

        if (nativeAd.getIcon() == null) {
            iconView.setVisibility(View.GONE);
        } else {
            iconView.setImageDrawable(nativeAd.getIcon().getDrawable());
            iconView.setVisibility(View.VISIBLE);
        }

        if (nativeAd.getCallToAction() == null) {
            callToActionView.setVisibility(View.GONE);
        } else {
            callToActionView.setText(nativeAd.getCallToAction());
            callToActionView.setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }

    interface NativeAdLoadedCallback {
        void onNativeAdLoaded(NativeAd nativeAd);
    }
}
