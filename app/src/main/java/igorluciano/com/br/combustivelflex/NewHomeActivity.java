package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewHomeActivity extends AppCompatActivity {
    public static final String EXTRA_CLEAR_INPUTS = "newClearInputs";
    public static final String EXTRA_OPEN_HISTORY = "openHistory";

    private enum Mode { CALCULAR, HISTORICO }
    private Mode currentMode = Mode.CALCULAR;

    private EditText gasolinePriceInput;
    private EditText ethanolPriceInput;
    private EditText gasolineConsumptionInput;
    private EditText ethanolConsumptionInput;
    private AdView bottomAd;

    private TextView tabCalcular;
    private TextView tabHistorico;
    private View calcPanel;
    private View historyPanel;
    private LinearLayout historyContainer;
    private TextView historyEmptyText;

    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy, HH:mm", new Locale("pt", "BR"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_home);

        gasolinePriceInput = findViewById(R.id.new_gasoline_price_input);
        ethanolPriceInput = findViewById(R.id.new_ethanol_price_input);
        gasolineConsumptionInput = findViewById(R.id.new_gasoline_consumption_input);
        ethanolConsumptionInput = findViewById(R.id.new_ethanol_consumption_input);
        setupInput(gasolinePriceInput);
        setupInput(ethanolPriceInput);
        setupInput(gasolineConsumptionInput);
        setupInput(ethanolConsumptionInput);

        tabCalcular = findViewById(R.id.tab_calcular);
        tabHistorico = findViewById(R.id.tab_historico);
        calcPanel = findViewById(R.id.home_calc_panel);
        historyPanel = findViewById(R.id.home_history_panel);
        historyContainer = findViewById(R.id.home_history_container);
        historyEmptyText = findViewById(R.id.home_history_empty_text);

        tabCalcular.setOnClickListener(v -> switchToMode(Mode.CALCULAR));
        tabHistorico.setOnClickListener(v -> switchToMode(Mode.HISTORICO));

        findViewById(R.id.new_calculate_button).setOnClickListener(view -> startResultIfReady());
        findViewById(R.id.new_clear_button).setOnClickListener(view -> clearInputs());
        findViewById(R.id.home_history_clear_button).setOnClickListener(v -> showClearConfirmDialog());

        findViewById(R.id.new_home_home_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_home_stations_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStationsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_home_maintenance_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewMaintenanceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_home_more_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewMoreActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        FrameLayout adContainer = findViewById(R.id.new_home_ad_container);
        bottomAd = AdMobBanner.loadMainBanner(this, adContainer);
        clearInputsIfRequested(getIntent());
        fillDefaultConsumptionsIfEmpty();

        if (getIntent().getBooleanExtra(EXTRA_OPEN_HISTORY, false)) {
            switchToMode(Mode.HISTORICO);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        clearInputsIfRequested(intent);
        if (intent.getBooleanExtra(EXTRA_OPEN_HISTORY, false)) {
            switchToMode(Mode.HISTORICO);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillDefaultConsumptionsIfEmpty();
        if (currentMode == Mode.HISTORICO) {
            renderHistory(CalculationHistoryStore.list(this));
        }
    }

    private void switchToMode(Mode mode) {
        if (currentMode == mode) return;
        currentMode = mode;
        updateTabStyles();
        if (mode == Mode.CALCULAR) {
            calcPanel.setVisibility(View.VISIBLE);
            historyPanel.setVisibility(View.GONE);
        } else {
            calcPanel.setVisibility(View.GONE);
            historyPanel.setVisibility(View.VISIBLE);
            renderHistory(CalculationHistoryStore.list(this));
        }
    }

    private void updateTabStyles() {
        if (currentMode == Mode.CALCULAR) {
            tabCalcular.setBackgroundResource(R.drawable.tab_station_active);
            tabCalcular.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            tabCalcular.setTypeface(null, Typeface.BOLD);
            tabHistorico.setBackgroundResource(R.drawable.tab_station_inactive);
            tabHistorico.setTextColor(ContextCompat.getColor(this, R.color.new_text_muted));
            tabHistorico.setTypeface(null, Typeface.NORMAL);
        } else {
            tabHistorico.setBackgroundResource(R.drawable.tab_station_active);
            tabHistorico.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            tabHistorico.setTypeface(null, Typeface.BOLD);
            tabCalcular.setBackgroundResource(R.drawable.tab_station_inactive);
            tabCalcular.setTextColor(ContextCompat.getColor(this, R.color.new_text_muted));
            tabCalcular.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void showClearConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.new_history_clear_confirm_title)
                .setMessage(R.string.new_history_clear_confirm_message)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.new_history_clear_confirm_yes, (dialog, which) -> {
                    CalculationHistoryStore.clear(this);
                    renderHistory(CalculationHistoryStore.list(this));
                })
                .show();
    }

    private void renderHistory(List<CalculationHistoryItem> items) {
        historyContainer.removeAllViews();

        if (items.isEmpty()) {
            historyEmptyText.setVisibility(View.VISIBLE);
            return;
        }

        historyEmptyText.setVisibility(View.GONE);
        for (CalculationHistoryItem item : items) {
            historyContainer.addView(createHistoryCard(item));
        }
    }

    private View createHistoryCard(CalculationHistoryItem item) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundResource(R.drawable.new_history_card_background);
        int padding = dp(14);
        card.setPadding(padding, padding, padding, padding);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(cardParams);

        TextView dateText = new TextView(this);
        dateText.setText(dateFormat.format(new Date(item.createdAt)));
        dateText.setTextColor(getColor(R.color.new_text_secondary));
        dateText.setTextSize(12);
        card.addView(dateText);

        TextView resultText = new TextView(this);
        resultText.setText(item.result);
        resultText.setTextColor(getColor(
                getString(R.string.result_ethanol).equals(item.result)
                        ? R.color.new_green
                        : R.color.new_orange
        ));
        resultText.setTextSize(18);
        resultText.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(resultText);

        TextView summaryText = new TextView(this);
        summaryText.setText(formatSummary(item));
        summaryText.setTextColor(getColor(R.color.new_text_primary));
        summaryText.setTextSize(13);
        summaryText.setPadding(0, dp(4), 0, 0);
        card.addView(summaryText);

        TextView savingsText = new TextView(this);
        savingsText.setText(getString(R.string.new_history_savings_label,
                currencyFormat.format(item.savings)));
        savingsText.setTextColor(getColor(R.color.new_text_secondary));
        savingsText.setTextSize(13);
        savingsText.setPadding(0, dp(4), 0, 0);
        card.addView(savingsText);

        return card;
    }

    private String formatSummary(CalculationHistoryItem item) {
        String summary = getString(
                R.string.new_history_prices_format,
                currencyFormat.format(item.gasoline),
                currencyFormat.format(item.ethanol)
        );

        if (!item.usedConsumption) {
            return summary;
        }

        return summary + "\n" + getString(
                R.string.new_history_consumption_format,
                item.gasolineConsumption,
                item.ethanolConsumption
        );
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
        intent.putExtra(NewResultActivity.EXTRA_GASOLINE, gasoline);
        intent.putExtra(NewResultActivity.EXTRA_ETHANOL, ethanol);
        intent.putExtra(NewResultActivity.EXTRA_GASOLINE_CONSUMPTION, gasolineConsumption);
        intent.putExtra(NewResultActivity.EXTRA_ETHANOL_CONSUMPTION, ethanolConsumption);
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

    private void setupInput(EditText input) {
        MaskedDecimalTextWatcher watcher = new MaskedDecimalTextWatcher(input);
        input.addTextChangedListener(watcher);
        input.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                watcher.padDecimals();
            }
        });
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
