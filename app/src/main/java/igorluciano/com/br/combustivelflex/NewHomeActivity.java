package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class NewHomeActivity extends Activity {
    public static final String EXTRA_CLEAR_INPUTS = "newClearInputs";

    private EditText gasolinePriceInput;
    private EditText ethanolPriceInput;
    private EditText gasolineConsumptionInput;
    private EditText ethanolConsumptionInput;
    private AdView bottomAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTransparentStatusBar();
        setContentView(R.layout.activity_new_home);

        gasolinePriceInput = findViewById(R.id.new_gasoline_price_input);
        ethanolPriceInput = findViewById(R.id.new_ethanol_price_input);
        gasolineConsumptionInput = findViewById(R.id.new_gasoline_consumption_input);
        ethanolConsumptionInput = findViewById(R.id.new_ethanol_consumption_input);
        setupPriceInput(gasolinePriceInput);
        setupPriceInput(ethanolPriceInput);
        setupPriceInput(gasolineConsumptionInput);
        setupPriceInput(ethanolConsumptionInput);

        findViewById(R.id.new_calculate_button).setOnClickListener(view -> startResultIfReady());
        findViewById(R.id.new_clear_button).setOnClickListener(view -> clearInputs());
        findViewById(R.id.new_home_home_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewStartActivity.class))
        );
        findViewById(R.id.new_home_history_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewHistoryActivity.class))
        );
        findViewById(R.id.new_home_stations_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewStationsActivity.class))
        );
        findViewById(R.id.new_home_more_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewMoreActivity.class))
        );

        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();
        FrameLayout adContainer = findViewById(R.id.new_home_ad_container);
        bottomAd = AdMobBanner.loadMainBanner(this, adContainer);
        clearInputsIfRequested(getIntent());
        fillDefaultConsumptionsIfEmpty();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        clearInputsIfRequested(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillDefaultConsumptionsIfEmpty();
    }

    private void clearInputs() {
        gasolinePriceInput.setText("");
        ethanolPriceInput.setText("");
        fillDefaultConsumptions();
        gasolinePriceInput.requestFocus();
    }

    private void clearInputsIfRequested(Intent intent) {
        if (intent == null || !intent.getBooleanExtra(EXTRA_CLEAR_INPUTS, false)) {
            return;
        }

        clearInputs();
        intent.removeExtra(EXTRA_CLEAR_INPUTS);
    }

    private void startResultIfReady() {
        String gasolineText = gasolinePriceInput.getText().toString();
        String ethanolText = ethanolPriceInput.getText().toString();
        String gasolineConsumptionText = gasolineConsumptionInput.getText().toString();
        String ethanolConsumptionText = ethanolConsumptionInput.getText().toString();

        if (TextUtils.isEmpty(gasolineText) || TextUtils.isEmpty(ethanolText)) {
            Toast.makeText(this, R.string.enter_values, Toast.LENGTH_SHORT).show();
            return;
        }

        Double gasoline = parsePrice(gasolineText);
        Double ethanol = parsePrice(ethanolText);
        if (gasoline == null || ethanol == null || gasoline <= 0 || ethanol <= 0) {
            Toast.makeText(this, R.string.enter_valid_values, Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hasGasolineConsumption = !TextUtils.isEmpty(gasolineConsumptionText);
        boolean hasEthanolConsumption = !TextUtils.isEmpty(ethanolConsumptionText);
        Double gasolineConsumption = hasGasolineConsumption
                ? parseConsumption(gasolineConsumptionText)
                : getDefaultGasolineConsumption();
        Double ethanolConsumption = hasEthanolConsumption
                ? parseConsumption(ethanolConsumptionText)
                : getDefaultEthanolConsumption();
        hasGasolineConsumption = gasolineConsumption != null && gasolineConsumption > 0;
        hasEthanolConsumption = ethanolConsumption != null && ethanolConsumption > 0;
        if (hasGasolineConsumption != hasEthanolConsumption
                || gasolineConsumption == null
                || ethanolConsumption == null) {
            Toast.makeText(this, R.string.enter_both_consumptions, Toast.LENGTH_SHORT).show();
            return;
        }

        if (shouldAskToUpdateDefaultConsumption(gasolineConsumption, ethanolConsumption)) {
            showUpdateDefaultConsumptionDialog(
                    gasoline,
                    ethanol,
                    gasolineConsumption,
                    ethanolConsumption
            );
            return;
        }

        startResult(gasoline, ethanol, gasolineConsumption, ethanolConsumption);
    }

    private void startResult(
            double gasoline,
            double ethanol,
            double gasolineConsumption,
            double ethanolConsumption
    ) {
        Intent intent = new Intent(this, NewResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_GASOLINE, gasoline);
        intent.putExtra(ResultActivity.EXTRA_ETHANOL, ethanol);
        intent.putExtra(ResultActivity.EXTRA_GASOLINE_CONSUMPTION, gasolineConsumption);
        intent.putExtra(ResultActivity.EXTRA_ETHANOL_CONSUMPTION, ethanolConsumption);
        startActivity(intent);
    }

    private boolean shouldAskToUpdateDefaultConsumption(
            double gasolineConsumption,
            double ethanolConsumption
    ) {
        double savedGasolineConsumption = NewSettingsStore.getGasolineConsumption(this);
        double savedEthanolConsumption = NewSettingsStore.getEthanolConsumption(this);
        if (savedGasolineConsumption <= 0 || savedEthanolConsumption <= 0) {
            return false;
        }

        return hasMeaningfulDifference(savedGasolineConsumption, gasolineConsumption)
                || hasMeaningfulDifference(savedEthanolConsumption, ethanolConsumption);
    }

    private void showUpdateDefaultConsumptionDialog(
            double gasoline,
            double ethanol,
            double gasolineConsumption,
            double ethanolConsumption
    ) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.new_home_update_defaults_title)
                .setMessage(R.string.new_home_update_defaults_message)
                .setNegativeButton(R.string.new_home_update_defaults_no, (dialog, which) ->
                        startResult(gasoline, ethanol, gasolineConsumption, ethanolConsumption))
                .setPositiveButton(R.string.new_home_update_defaults_yes, (dialog, which) -> {
                    NewSettingsStore.setGasolineConsumption(this, gasolineConsumption);
                    NewSettingsStore.setEthanolConsumption(this, ethanolConsumption);
                    startResult(gasoline, ethanol, gasolineConsumption, ethanolConsumption);
                })
                .show();
    }

    private Double parsePrice(String value) {
        try {
            return Double.parseDouble(value.trim().replace(',', '.'));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Double parseConsumption(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0.0;
        }

        try {
            double consumption = Double.parseDouble(value.trim().replace(',', '.'));
            return consumption > 0 ? consumption : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private Double getDefaultGasolineConsumption() {
        double value = NewSettingsStore.getGasolineConsumption(this);
        return value > 0 ? value : 0.0;
    }

    private Double getDefaultEthanolConsumption() {
        double value = NewSettingsStore.getEthanolConsumption(this);
        return value > 0 ? value : 0.0;
    }

    private void fillDefaultConsumptionsIfEmpty() {
        if (!TextUtils.isEmpty(gasolineConsumptionInput.getText().toString())
                || !TextUtils.isEmpty(ethanolConsumptionInput.getText().toString())) {
            return;
        }

        fillDefaultConsumptions();
    }

    private void fillDefaultConsumptions() {
        double gasolineConsumption = NewSettingsStore.getGasolineConsumption(this);
        double ethanolConsumption = NewSettingsStore.getEthanolConsumption(this);
        gasolineConsumptionInput.setText(gasolineConsumption > 0
                ? formatInputConsumption(gasolineConsumption)
                : "");
        ethanolConsumptionInput.setText(ethanolConsumption > 0
                ? formatInputConsumption(ethanolConsumption)
                : "");
    }

    private String formatInputConsumption(double value) {
        return String.format(java.util.Locale.US, "%.2f", value);
    }

    private boolean hasMeaningfulDifference(double first, double second) {
        return Math.abs(first - second) >= 0.01;
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

    private void setupPriceInput(EditText input) {
        MaskedDecimalTextWatcher watcher = new MaskedDecimalTextWatcher(input);
        input.addTextChangedListener(watcher);
        input.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                watcher.padDecimals();
            }
        });
    }
}
