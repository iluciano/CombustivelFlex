package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;

import com.google.android.gms.ads.AdView;

public class NewMoreActivity extends AppCompatActivity {
    private AdView bottomAd;

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
}
