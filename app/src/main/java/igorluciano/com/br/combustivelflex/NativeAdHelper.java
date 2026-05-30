package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

/**
 * Carrega e exibe um anúncio Native Advanced reutilizando o layout
 * {@code layout_native_ad_detail.xml}. O container e o divider ficam ocultos
 * até o anúncio carregar.
 */
public class NativeAdHelper {

    public interface Callback {
        void onLoaded(NativeAd ad);
    }

    /**
     * Carrega um anúncio nativo e o exibe no container informado.
     *
     * @param callback recebe o NativeAd carregado para que a Activity possa
     *                 destruí-lo em onDestroy.
     */
    public static void load(Activity activity, String adUnitId,
                            int containerId, int dividerId, Callback callback) {
        new AdLoader.Builder(activity, adUnitId)
                .forNativeAd(ad -> {
                    if (activity.isFinishing() || activity.isDestroyed()) {
                        ad.destroy();
                        return;
                    }
                    show(activity, ad, containerId, dividerId);
                    callback.onLoaded(ad);
                })
                .build()
                .loadAd(new AdRequest.Builder().build());
    }

    private static void show(Activity activity, NativeAd ad, int containerId, int dividerId) {
        FrameLayout container = activity.findViewById(containerId);
        View divider = activity.findViewById(dividerId);

        NativeAdView adView = (NativeAdView) LayoutInflater.from(activity)
                .inflate(R.layout.layout_native_ad_detail, container, false);

        TextView headline = adView.findViewById(R.id.ad_headline);
        headline.setText(ad.getHeadline());
        adView.setHeadlineView(headline);

        TextView body = adView.findViewById(R.id.ad_body);
        if (ad.getBody() != null) {
            body.setText(ad.getBody());
            body.setVisibility(View.VISIBLE);
        } else {
            body.setVisibility(View.GONE);
        }
        adView.setBodyView(body);

        ImageView icon = adView.findViewById(R.id.ad_icon);
        if (ad.getIcon() != null) {
            icon.setImageDrawable(ad.getIcon().getDrawable());
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.GONE);
        }
        adView.setIconView(icon);

        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);
        if (ad.getMediaContent() != null) {
            mediaView.setMediaContent(ad.getMediaContent());
            mediaView.setVisibility(View.VISIBLE);
        }

        Button cta = adView.findViewById(R.id.ad_call_to_action);
        if (ad.getCallToAction() != null) {
            cta.setText(ad.getCallToAction());
            cta.setVisibility(View.VISIBLE);
        }
        adView.setCallToActionView(cta);

        adView.setNativeAd(ad);

        container.removeAllViews();
        container.addView(adView);
        container.setVisibility(View.VISIBLE);
        if (divider != null) divider.setVisibility(View.VISIBLE);
    }
}
