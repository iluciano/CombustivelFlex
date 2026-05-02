package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.gms.tasks.Task;

public class MainActivity extends Activity {
    private static final long AUTO_CALCULATION_DELAY_MS = 900;
    private static final int UPDATE_REQUEST_CODE = 1001;
    public static final String EXTRA_CLEAR_INPUTS = "clearInputs";

    private EditText gasolineInput;
    private EditText ethanolInput;
    private EditText gasolineConsumptionInput;
    private EditText ethanolConsumptionInput;
    private AppUpdateManager appUpdateManager;
    private AdView bottomButtonsAd;
    private final Handler autoCalculationHandler = new Handler(Looper.getMainLooper());
    private final Runnable autoCalculationRunnable = () -> startResultIfReady(false);
    private boolean clearingInputs;
    private boolean resultStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gasolineInput = findViewById(R.id.gasoline_input);
        ethanolInput = findViewById(R.id.ethanol_input);
        gasolineConsumptionInput = findViewById(R.id.gasoline_consumption_input);
        ethanolConsumptionInput = findViewById(R.id.ethanol_consumption_input);
        setupPriceInput(gasolineInput);
        setupPriceInput(ethanolInput);
        setupPriceInput(gasolineConsumptionInput);
        setupPriceInput(ethanolConsumptionInput);
        setupAutoCalculation();
        appUpdateManager = AppUpdateManagerFactory.create(this);
        checkForAppUpdate();

        findViewById(R.id.clear_button).setOnClickListener(view -> {
            autoCalculationHandler.removeCallbacks(autoCalculationRunnable);
            resultStarted = false;
            gasolineInput.setText("");
            ethanolInput.setText("");
            gasolineConsumptionInput.setText("");
            ethanolConsumptionInput.setText("");
            gasolineInput.requestFocus();
        });

        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();
        FrameLayout adContainer = findViewById(R.id.bottom_buttons_ad_container);
        bottomButtonsAd = AdMobBanner.loadMainBanner(this, adContainer);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        clearInputsIfRequested(intent);
    }

    public void onClickResult(View view) {
        startResultIfReady(true);
    }

    private void startResultIfReady(boolean showErrors) {
        Intent intent = buildResultIntent(showErrors);
        if (intent == null || resultStarted) {
            return;
        }

        resultStarted = true;
        startActivity(intent);
    }

    private Intent buildResultIntent(boolean showErrors) {
        String gasolineText = gasolineInput.getText().toString();
        String ethanolText = ethanolInput.getText().toString();
        String gasolineConsumptionText = gasolineConsumptionInput.getText().toString();
        String ethanolConsumptionText = ethanolConsumptionInput.getText().toString();

        if (TextUtils.isEmpty(gasolineText) || TextUtils.isEmpty(ethanolText)) {
            if (showErrors) {
                Toast.makeText(this, R.string.enter_values, Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        Double gasoline = parsePrice(gasolineText);
        Double ethanol = parsePrice(ethanolText);

        if (gasoline == null || ethanol == null || gasoline <= 0 || ethanol <= 0) {
            if (showErrors) {
                Toast.makeText(this, R.string.enter_valid_values, Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        Double gasolineConsumption = parseConsumption(gasolineConsumptionText);
        Double ethanolConsumption = parseConsumption(ethanolConsumptionText);
        boolean hasGasolineConsumption = !TextUtils.isEmpty(gasolineConsumptionText);
        boolean hasEthanolConsumption = !TextUtils.isEmpty(ethanolConsumptionText);

        if (hasGasolineConsumption != hasEthanolConsumption
                || gasolineConsumption == null
                || ethanolConsumption == null) {
            if (showErrors) {
                Toast.makeText(this, R.string.enter_both_consumptions, Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_GASOLINE, gasoline);
        intent.putExtra(ResultActivity.EXTRA_ETHANOL, ethanol);
        intent.putExtra(ResultActivity.EXTRA_GASOLINE_CONSUMPTION, gasolineConsumption);
        intent.putExtra(ResultActivity.EXTRA_ETHANOL_CONSUMPTION, ethanolConsumption);
        return intent;
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

    private void setupPriceInput(EditText input) {
        PriceInputTextWatcher watcher = new PriceInputTextWatcher(input);
        input.addTextChangedListener(watcher);
        input.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                watcher.padDecimals();
            }
        });
    }

    private void setupAutoCalculation() {
        TextWatcher watcher = new AutoCalculationTextWatcher();
        gasolineInput.addTextChangedListener(watcher);
        ethanolInput.addTextChangedListener(watcher);
        gasolineConsumptionInput.addTextChangedListener(watcher);
        ethanolConsumptionInput.addTextChangedListener(watcher);
    }

    private void scheduleAutoCalculation() {
        if (clearingInputs) {
            return;
        }

        resultStarted = false;
        autoCalculationHandler.removeCallbacks(autoCalculationRunnable);
        if (buildResultIntent(false) != null) {
            autoCalculationHandler.postDelayed(autoCalculationRunnable, AUTO_CALCULATION_DELAY_MS);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resultStarted = false;
        clearInputsIfRequested(getIntent());
        resumeAppUpdateIfNeeded();
    }

    @Override
    protected void onDestroy() {
        autoCalculationHandler.removeCallbacks(autoCalculationRunnable);
        if (bottomButtonsAd != null) {
            bottomButtonsAd.destroy();
        }
        super.onDestroy();
    }

    private void clearInputsIfRequested(Intent intent) {
        if (intent == null || !intent.getBooleanExtra(EXTRA_CLEAR_INPUTS, false)) {
            return;
        }

        autoCalculationHandler.removeCallbacks(autoCalculationRunnable);
        clearingInputs = true;
        gasolineInput.setText("");
        ethanolInput.setText("");
        gasolineConsumptionInput.setText("");
        ethanolConsumptionInput.setText("");
        clearingInputs = false;
        gasolineInput.requestFocus();
        intent.removeExtra(EXTRA_CLEAR_INPUTS);
    }

    private void checkForAppUpdate() {
        if (appUpdateManager == null) {
            return;
        }

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startAppUpdate(appUpdateInfo);
            }
        });
    }

    private void resumeAppUpdateIfNeeded() {
        if (appUpdateManager == null) {
            return;
        }

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startAppUpdate(appUpdateInfo);
            }
        });
    }

    private void startAppUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    UPDATE_REQUEST_CODE
            );
        } catch (Exception ignored) {
            // Google Play handles update availability; the app can continue normally if it cannot start.
        }
    }

    private final class AutoCalculationTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            scheduleAutoCalculation();
        }
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
