package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

public class ResultActivity extends Activity {
    public static final String EXTRA_GASOLINE = "valGas";
    public static final String EXTRA_ETHANOL = "valEta";
    public static final String EXTRA_GASOLINE_CONSUMPTION = "gasolineConsumption";
    public static final String EXTRA_ETHANOL_CONSUMPTION = "ethanolConsumption";

    private AdView bottomCarAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        TextView resultText = findViewById(R.id.result_text);
        double gasoline = getIntent().getDoubleExtra(EXTRA_GASOLINE, 0);
        double ethanol = getIntent().getDoubleExtra(EXTRA_ETHANOL, 0);
        int gasolineConsumption = getIntent().getIntExtra(EXTRA_GASOLINE_CONSUMPTION, 0);
        int ethanolConsumption = getIntent().getIntExtra(EXTRA_ETHANOL_CONSUMPTION, 0);

        if (gasoline <= 0 || ethanol <= 0) {
            resultText.setText(R.string.invalid_result);
            return;
        }

        int result = hasConsumptionValues(gasolineConsumption, ethanolConsumption)
                ? calculateByConsumption(gasoline, ethanol, gasolineConsumption, ethanolConsumption)
                : calculateByDefaultRule(gasoline, ethanol);
        resultText.setText(result);

        FrameLayout adContainer = findViewById(R.id.bottom_car_ad_container);
        bottomCarAd = AdMobBanner.loadResultBanner(this, adContainer);
    }

    private boolean hasConsumptionValues(int gasolineConsumption, int ethanolConsumption) {
        return gasolineConsumption > 0 && ethanolConsumption > 0;
    }

    private int calculateByConsumption(
            double gasoline,
            double ethanol,
            int gasolineConsumption,
            int ethanolConsumption
    ) {
        return ethanol * gasolineConsumption < gasoline * ethanolConsumption
                ? R.string.result_ethanol
                : R.string.result_gasoline;
    }

    private int calculateByDefaultRule(double gasoline, double ethanol) {
        return ethanol / gasoline < 0.7
                ? R.string.result_ethanol
                : R.string.result_gasoline;
    }

    @Override
    protected void onDestroy() {
        if (bottomCarAd != null) {
            bottomCarAd.destroy();
        }
        super.onDestroy();
    }
}
