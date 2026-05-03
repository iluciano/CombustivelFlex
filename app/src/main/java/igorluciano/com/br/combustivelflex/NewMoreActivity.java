package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class NewMoreActivity extends Activity {
    private AdView bottomAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTransparentStatusBar();
        setContentView(R.layout.activity_new_more);

        findViewById(R.id.new_more_tips_card).setOnClickListener(
                view -> startActivity(new Intent(this, NewTipsActivity.class))
        );
        findViewById(R.id.new_more_settings_card).setOnClickListener(
                view -> startActivity(new Intent(this, NewSettingsActivity.class))
        );
        findViewById(R.id.new_more_home_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewStartActivity.class))
        );
        findViewById(R.id.new_more_history_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewHistoryActivity.class))
        );
        findViewById(R.id.new_more_stations_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewStationsActivity.class))
        );

        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();
        FrameLayout adContainer = findViewById(R.id.new_more_ad_container);
        bottomAd = AdMobBanner.loadMoreBanner(this, adContainer);
    }

    @Override
    protected void onDestroy() {
        if (bottomAd != null) {
            bottomAd.destroy();
        }
        super.onDestroy();
    }

    private void setupTransparentStatusBar() {
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }
}
