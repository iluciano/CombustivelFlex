package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

public class NewMoreActivity extends AppCompatActivity {

    private static final String MORE_NATIVE_AD_UNIT_ID = "ca-app-pub-1199102836233471/3525523095";

    private NativeAd nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_more);

        findViewById(R.id.new_more_tips_card).setOnClickListener(
                view -> startActivity(new Intent(this, NewTipsActivity.class))
        );
        findViewById(R.id.new_more_settings_card).setOnClickListener(
                view -> startActivity(new Intent(this, NewSettingsActivity.class))
        );
        findViewById(R.id.new_more_home_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_more_history_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewHistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_more_stations_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStationsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        loadNativeAd();
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }

    private void loadNativeAd() {
        new AdLoader.Builder(this, MORE_NATIVE_AD_UNIT_ID)
                .forNativeAd(ad -> {
                    nativeAd = ad;
                    showNativeAd(ad);
                })
                .build()
                .loadAd(new AdRequest.Builder().build());
    }

    private void showNativeAd(NativeAd ad) {
        FrameLayout container = findViewById(R.id.new_more_ad_container);
        View divider = findViewById(R.id.new_more_ad_divider);

        NativeAdView adView = (NativeAdView) LayoutInflater.from(this)
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
        divider.setVisibility(View.VISIBLE);
    }
}
