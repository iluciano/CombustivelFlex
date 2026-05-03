package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.text.NumberFormat;
import java.util.Locale;

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
        TextView savingsValueText = findViewById(R.id.savings_value_text);
        double gasoline = getIntent().getDoubleExtra(EXTRA_GASOLINE, 0);
        double ethanol = getIntent().getDoubleExtra(EXTRA_ETHANOL, 0);
        double gasolineConsumption = getIntent().getDoubleExtra(EXTRA_GASOLINE_CONSUMPTION, 0);
        double ethanolConsumption = getIntent().getDoubleExtra(EXTRA_ETHANOL_CONSUMPTION, 0);

        if (gasoline <= 0 || ethanol <= 0) {
            resultText.setText(R.string.invalid_result);
            return;
        }

        int result = hasConsumptionValues(gasolineConsumption, ethanolConsumption)
                ? calculateByConsumption(gasoline, ethanol, gasolineConsumption, ethanolConsumption)
                : calculateByDefaultRule(gasoline, ethanol);
        resultText.setText(result);
        savingsValueText.setText(formatSavings(
                calculateSavings(gasoline, ethanol, gasolineConsumption, ethanolConsumption)
        ));

        findViewById(R.id.recalculate_button).setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(MainActivity.EXTRA_CLEAR_INPUTS, true);
            startActivity(intent);
            finish();
        });

        FrameLayout adContainer = findViewById(R.id.bottom_car_ad_container);
        bottomCarAd = AdMobBanner.loadResultBanner(this, adContainer);
    }

    private boolean hasConsumptionValues(double gasolineConsumption, double ethanolConsumption) {
        return gasolineConsumption > 0 && ethanolConsumption > 0;
    }

    private int calculateByConsumption(
            double gasoline,
            double ethanol,
            double gasolineConsumption,
            double ethanolConsumption
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

    private double calculateSavings(
            double gasoline,
            double ethanol,
            double gasolineConsumption,
            double ethanolConsumption
    ) {
        if (hasConsumptionValues(gasolineConsumption, ethanolConsumption)) {
            double gasolineCostPerKm = gasoline / gasolineConsumption;
            double ethanolCostPerKm = ethanol / ethanolConsumption;
            return Math.abs(gasolineCostPerKm - ethanolCostPerKm);
        }

        return Math.abs((gasoline * 0.7) - ethanol);
    }

    private String formatSavings(double savings) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return getString(R.string.savings_per_liter_format, currencyFormat.format(savings));
    }

    @Override
    protected void onDestroy() {
        if (bottomCarAd != null) {
            bottomCarAd.destroy();
        }
        super.onDestroy();
    }
}
