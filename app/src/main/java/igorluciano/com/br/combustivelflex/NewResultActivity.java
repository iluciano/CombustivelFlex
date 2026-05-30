package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import com.google.android.gms.ads.AdView;

import java.text.NumberFormat;
import java.util.Locale;

public class NewResultActivity extends AppCompatActivity {
    public static final String EXTRA_GASOLINE = "valGas";
    public static final String EXTRA_ETHANOL = "valEta";
    public static final String EXTRA_GASOLINE_CONSUMPTION = "gasolineConsumption";
    public static final String EXTRA_ETHANOL_CONSUMPTION = "ethanolConsumption";

    private AdView bottomAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_result);

        TextView resultText = findViewById(R.id.new_result_text);
        TextView savingsText = findViewById(R.id.new_savings_value_text);
        TextView gasolineCostText = findViewById(R.id.new_gasoline_cost_text);
        TextView ethanolCostText = findViewById(R.id.new_ethanol_cost_text);

        double gasoline = getIntent().getDoubleExtra(EXTRA_GASOLINE, 0);
        double ethanol = getIntent().getDoubleExtra(EXTRA_ETHANOL, 0);
        double gasolineConsumption = getIntent().getDoubleExtra(EXTRA_GASOLINE_CONSUMPTION, 0);
        double ethanolConsumption = getIntent().getDoubleExtra(EXTRA_ETHANOL_CONSUMPTION, 0);

        if (gasoline <= 0 || ethanol <= 0) {
            resultText.setText(R.string.invalid_result);
            return;
        }

        boolean hasConsumption = hasConsumptionValues(gasolineConsumption, ethanolConsumption);
        int result = hasConsumption
                ? calculateByConsumption(gasoline, ethanol, gasolineConsumption, ethanolConsumption)
                : calculateByDefaultRule(gasoline, ethanol);
        double savings = calculateSavings(
                gasoline,
                ethanol,
                gasolineConsumption,
                ethanolConsumption
        );
        resultText.setText(result);
        resultText.setTextColor(getColor(
                result == R.string.result_ethanol ? R.color.new_green : R.color.new_orange
        ));
        savingsText.setText(formatSavings(savings, hasConsumption));
        saveCalculation(
                gasoline,
                ethanol,
                gasolineConsumption,
                ethanolConsumption,
                getString(result),
                savings,
                hasConsumption
        );

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
        findViewById(R.id.new_result_home_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_result_stations_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStationsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_result_maintenance_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewMaintenanceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_result_more_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewMoreActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        FrameLayout adContainer = findViewById(R.id.new_result_ad_container);
        bottomAd = AdMobBanner.loadResultBanner(this, adContainer);
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

    private void saveCalculation(
            double gasoline,
            double ethanol,
            double gasolineConsumption,
            double ethanolConsumption,
            String result,
            double savings,
            boolean usedConsumption
    ) {
        CalculationHistoryStore.save(this, new CalculationHistoryItem(
                System.currentTimeMillis(),
                gasoline,
                ethanol,
                gasolineConsumption,
                ethanolConsumption,
                result,
                savings,
                usedConsumption
        ));
    }

    private String formatSavings(double savings, boolean perKm) {
        int format = perKm ? R.string.savings_per_km_format : R.string.savings_per_liter_format;
        return getString(format, formatCurrency(savings));
    }

    private String formatCostPerKm(double value) {
        return getString(R.string.cost_per_km_format, formatCurrency(value));
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
