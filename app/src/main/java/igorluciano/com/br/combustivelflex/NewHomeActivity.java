package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();
        FrameLayout adContainer = findViewById(R.id.new_home_ad_container);
        bottomAd = AdMobBanner.loadMainBanner(this, adContainer);
        clearInputsIfRequested(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        clearInputsIfRequested(intent);
    }

    private void clearInputs() {
        gasolinePriceInput.setText("");
        ethanolPriceInput.setText("");
        gasolineConsumptionInput.setText("");
        ethanolConsumptionInput.setText("");
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

        Double gasolineConsumption = parseConsumption(gasolineConsumptionText);
        Double ethanolConsumption = parseConsumption(ethanolConsumptionText);
        boolean hasGasolineConsumption = !TextUtils.isEmpty(gasolineConsumptionText);
        boolean hasEthanolConsumption = !TextUtils.isEmpty(ethanolConsumptionText);
        if (hasGasolineConsumption != hasEthanolConsumption
                || gasolineConsumption == null
                || ethanolConsumption == null) {
            Toast.makeText(this, R.string.enter_both_consumptions, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, NewResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_GASOLINE, gasoline);
        intent.putExtra(ResultActivity.EXTRA_ETHANOL, ethanol);
        intent.putExtra(ResultActivity.EXTRA_GASOLINE_CONSUMPTION, gasolineConsumption);
        intent.putExtra(ResultActivity.EXTRA_ETHANOL_CONSUMPTION, ethanolConsumption);
        startActivity(intent);
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
        PriceInputTextWatcher watcher = new PriceInputTextWatcher(input);
        input.addTextChangedListener(watcher);
        input.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                watcher.padDecimals();
            }
        });
    }

    private static final class PriceInputTextWatcher implements TextWatcher {
        private static final int MAX_DIGITS = 4;
        private static final int MAX_INTEGER_DIGITS = 2;
        private static final int MIN_DECIMAL_DIGITS = 2;

        private final EditText input;
        private boolean updating;
        private boolean deletingDecimalPoint;

        private PriceInputTextWatcher(EditText input) {
            this.input = input;
        }

        @Override
        public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            deletingDecimalPoint = count == 1
                    && after == 0
                    && start < text.length()
                    && text.charAt(start) == '.';
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (updating) {
                return;
            }

            if (deletingDecimalPoint) {
                deletingDecimalPoint = false;
                return;
            }

            String original = editable.toString();
            String formatted = formatPriceInput(original);
            if (original.equals(formatted)) {
                return;
            }

            updateText(formatted);
        }

        private void padDecimals() {
            if (updating) {
                return;
            }

            String original = input.getText().toString();
            if (TextUtils.isEmpty(original)) {
                return;
            }

            String formatted = padDecimalPart(formatPriceInput(original));
            if (!original.equals(formatted)) {
                updateText(formatted);
            }
        }

        private void updateText(String value) {
            updating = true;
            input.setText(value);
            input.setSelection(value.length());
            updating = false;
        }

        private String formatPriceInput(String value) {
            String digits = collectDigits(value);
            if (digits.isEmpty()) {
                return "";
            }

            int integerDigits = digits.length() < MAX_DIGITS
                    ? 1
                    : MAX_INTEGER_DIGITS;
            String integerPart = digits.substring(0, integerDigits);
            String decimalPart = digits.substring(integerDigits);
            return integerPart + "." + decimalPart;
        }

        private String padDecimalPart(String value) {
            int pointIndex = value.indexOf('.');
            if (pointIndex < 0) {
                return value + ".00";
            }

            String integerPart = value.substring(0, pointIndex);
            String decimalPart = value.substring(pointIndex + 1);
            while (decimalPart.length() < MIN_DECIMAL_DIGITS) {
                decimalPart += "0";
            }

            return integerPart + "." + decimalPart;
        }

        private String collectDigits(String value) {
            StringBuilder digits = new StringBuilder();

            for (int index = 0; index < value.length(); index++) {
                char character = value.charAt(index);
                if (Character.isDigit(character) && digits.length() < MAX_DIGITS) {
                    digits.append(character);
                }
            }

            return digits.toString();
        }
    }
}
