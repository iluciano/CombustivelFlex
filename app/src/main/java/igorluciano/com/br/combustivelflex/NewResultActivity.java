package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import java.text.NumberFormat;
import java.util.Locale;

public class NewResultActivity extends Activity {
    private AdView bottomAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTransparentStatusBar();
        setContentView(R.layout.activity_new_result);

        TextView resultText = findViewById(R.id.new_result_text);
        TextView savingsText = findViewById(R.id.new_savings_value_text);
        TextView gasolineCostText = findViewById(R.id.new_gasoline_cost_text);
        TextView ethanolCostText = findViewById(R.id.new_ethanol_cost_text);

        double gasoline = getIntent().getDoubleExtra(ResultActivity.EXTRA_GASOLINE, 0);
        double ethanol = getIntent().getDoubleExtra(ResultActivity.EXTRA_ETHANOL, 0);
        double gasolineConsumption = getIntent()
                .getDoubleExtra(ResultActivity.EXTRA_GASOLINE_CONSUMPTION, 0);
        double ethanolConsumption = getIntent()
                .getDoubleExtra(ResultActivity.EXTRA_ETHANOL_CONSUMPTION, 0);

        if (gasoline <= 0 || ethanol <= 0) {
            resultText.setText(R.string.invalid_result);
            return;
        }

        boolean hasConsumption = hasConsumptionValues(gasolineConsumption, ethanolConsumption);
        int result = hasConsumption
                ? calculateByConsumption(gasoline, ethanol, gasolineConsumption, ethanolConsumption)
                : calculateByDefaultRule(gasoline, ethanol);
        resultText.setText(result);
        resultText.setTextColor(getColor(
                result == R.string.result_ethanol ? R.color.new_green : R.color.new_orange
        ));
        savingsText.setText(formatSavings(
                calculateSavings(gasoline, ethanol, gasolineConsumption, ethanolConsumption)
        ));

        if (hasConsumption) {
            gasolineCostText.setText(formatCostPerKm(gasoline / gasolineConsumption));
            ethanolCostText.setText(formatCostPerKm(ethanol / ethanolConsumption));
        } else {
            gasolineCostText.setText(formatCurrency(gasoline));
            ethanolCostText.setText(formatCurrency(ethanol));
        }

        findViewById(R.id.new_recalculate_button).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(NewHomeActivity.EXTRA_CLEAR_INPUTS, true);
            startActivity(intent);
            finish();
        });

        FrameLayout adContainer = findViewById(R.id.new_result_ad_container);
        bottomAd = AdMobBanner.loadResultBanner(this, adContainer);
    }

    private boolean hasConsumptionValues(double gasolineConsumption, double ethanolConsumption) {
        return gasolineConsumption > 0 && ethanolConsumption > 0;
    }

    private void setupTransparentStatusBar() {
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
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
        return formatCurrency(savings) + " por litro";
    }

    private String formatCostPerKm(double value) {
        return formatCurrency(value) + "/km";
    }

    private String formatCurrency(double value) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return currencyFormat.format(value);
    }

    @Override
    protected void onDestroy() {
        if (bottomAd != null) {
            bottomAd.destroy();
        }
        super.onDestroy();
    }
}
