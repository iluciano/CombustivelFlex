package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

public class ResultActivity extends Activity {
    public static final String EXTRA_GASOLINE = "valGas";
    public static final String EXTRA_ETHANOL = "valEta";

    private AdView bottomCarAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        TextView resultText = findViewById(R.id.result_text);
        double gasoline = getIntent().getDoubleExtra(EXTRA_GASOLINE, 0);
        double ethanol = getIntent().getDoubleExtra(EXTRA_ETHANOL, 0);

        if (gasoline <= 0 || ethanol <= 0) {
            resultText.setText(R.string.invalid_result);
            return;
        }

        int result = ethanol / gasoline < 0.7
                ? R.string.result_ethanol
                : R.string.result_gasoline;
        resultText.setText(result);

        FrameLayout adContainer = findViewById(R.id.bottom_car_ad_container);
        bottomCarAd = AdMobBanner.loadResultBanner(this, adContainer);
    }

    @Override
    protected void onDestroy() {
        if (bottomCarAd != null) {
            bottomCarAd.destroy();
        }
        super.onDestroy();
    }
}
