package igorluciano.com.br.combustivelflex;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class NewSettingsActivity extends Activity {
    private static final int REQUEST_NOTIFICATIONS = 10;
    private static final String PLAY_STORE_URL =
            "https://play.google.com/store/apps/details?id=igorluciano.com.br.combustivelflex&pli=1";
    private static final String MARKET_URL =
            "market://details?id=igorluciano.com.br.combustivelflex";

    private final DecimalFormat consumptionFormat = new DecimalFormat(
            "0.0#",
            DecimalFormatSymbols.getInstance(new Locale("pt", "BR"))
    );

    private TextView literUnitButton;
    private TextView kmUnitButton;
    private TextView gasolineConsumptionText;
    private TextView ethanolConsumptionText;
    private Switch notificationsSwitch;
    private Switch priceReminderSwitch;
    private AdView bottomAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTransparentStatusBar();
        setContentView(R.layout.activity_new_settings);

        literUnitButton = findViewById(R.id.new_settings_unit_liter);
        kmUnitButton = findViewById(R.id.new_settings_unit_km);
        gasolineConsumptionText = findViewById(R.id.new_settings_gasoline_consumption_text);
        ethanolConsumptionText = findViewById(R.id.new_settings_ethanol_consumption_text);
        notificationsSwitch = findViewById(R.id.new_settings_notifications_switch);
        priceReminderSwitch = findViewById(R.id.new_settings_price_reminder_switch);

        findViewById(R.id.new_settings_gasoline_card).setOnClickListener(
                view -> showConsumptionDialog(true)
        );
        findViewById(R.id.new_settings_ethanol_card).setOnClickListener(
                view -> showConsumptionDialog(false)
        );
        literUnitButton.setOnClickListener(view -> setUnit(NewSettingsStore.UNIT_LITER));
        kmUnitButton.setOnClickListener(view -> setUnit(NewSettingsStore.UNIT_KM));
        notificationsSwitch.setOnCheckedChangeListener((button, checked) ->
                handleNotificationsToggle(checked)
        );
        priceReminderSwitch.setOnCheckedChangeListener((button, checked) ->
                NewSettingsStore.setPriceReminderEnabled(this, checked)
        );
        findViewById(R.id.new_settings_rate_row).setOnClickListener(view -> openStoreReview());
        findViewById(R.id.new_settings_share_row).setOnClickListener(view -> shareApp());
        findViewById(R.id.new_settings_about_row).setOnClickListener(
                view -> Toast.makeText(this, R.string.app_name, Toast.LENGTH_SHORT).show()
        );
        findViewById(R.id.new_settings_home_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewStartActivity.class))
        );
        findViewById(R.id.new_settings_history_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewHistoryActivity.class))
        );
        findViewById(R.id.new_settings_stations_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewStationsActivity.class))
        );
        findViewById(R.id.new_settings_more_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewMoreActivity.class))
        );

        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();
        FrameLayout adContainer = findViewById(R.id.new_settings_ad_container);
        bottomAd = AdMobBanner.loadSettingsBanner(this, adContainer);

        renderSettings();
    }

    @Override
    protected void onDestroy() {
        if (bottomAd != null) {
            bottomAd.destroy();
        }
        super.onDestroy();
    }

    private void renderSettings() {
        renderUnit(NewSettingsStore.getUnit(this));
        gasolineConsumptionText.setText(formatConsumption(
                NewSettingsStore.getGasolineConsumption(this)
        ));
        ethanolConsumptionText.setText(formatConsumption(
                NewSettingsStore.getEthanolConsumption(this)
        ));
        notificationsSwitch.setChecked(NewSettingsStore.isNotificationsEnabled(this));
        priceReminderSwitch.setChecked(NewSettingsStore.isPriceReminderEnabled(this));
        TextView versionText = findViewById(R.id.new_settings_version_text);
        versionText.setText(getString(R.string.new_settings_version_format, getVersionName()));
    }

    private void setUnit(String unit) {
        NewSettingsStore.setUnit(this, unit);
        renderUnit(unit);
    }

    private void renderUnit(String unit) {
        boolean isLiter = NewSettingsStore.UNIT_LITER.equals(unit);
        literUnitButton.setBackgroundResource(isLiter
                ? R.drawable.new_settings_segment_selected
                : R.drawable.new_settings_segment_unselected);
        literUnitButton.setTextColor(getColor(isLiter ? android.R.color.white : R.color.new_text_primary));
        kmUnitButton.setBackgroundResource(isLiter
                ? R.drawable.new_settings_segment_unselected
                : R.drawable.new_settings_segment_selected);
        kmUnitButton.setTextColor(getColor(isLiter ? R.color.new_text_primary : android.R.color.white));
    }

    private void showConsumptionDialog(boolean gasoline) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        MaskedDecimalTextWatcher watcher = new MaskedDecimalTextWatcher(input);
        double currentValue = gasoline
                ? NewSettingsStore.getGasolineConsumption(this)
                : NewSettingsStore.getEthanolConsumption(this);
        if (currentValue > 0) {
            input.setText(String.format(Locale.US, "%.2f", currentValue));
            input.setSelection(input.getText().length());
        }
        input.addTextChangedListener(watcher);
        input.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                watcher.padDecimals();
            }
        });

        new AlertDialog.Builder(this)
                .setTitle(gasoline
                        ? R.string.new_settings_gasoline_label
                        : R.string.new_settings_ethanol_label)
                .setMessage(R.string.new_settings_consumption_dialog_message)
                .setView(input)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    watcher.padDecimals();
                    saveConsumption(gasoline, input.getText().toString());
                })
                .show();
    }

    private void saveConsumption(boolean gasoline, String value) {
        try {
            double consumption = Double.parseDouble(value.trim().replace(',', '.'));
            if (consumption <= 0) {
                Toast.makeText(this, R.string.enter_valid_values, Toast.LENGTH_SHORT).show();
                return;
            }

            if (gasoline) {
                NewSettingsStore.setGasolineConsumption(this, consumption);
            } else {
                NewSettingsStore.setEthanolConsumption(this, consumption);
            }
            renderSettings();
        } catch (NumberFormatException exception) {
            Toast.makeText(this, R.string.enter_valid_values, Toast.LENGTH_SHORT).show();
        }
    }

    private void handleNotificationsToggle(boolean checked) {
        if (!checked) {
            NewSettingsStore.setNotificationsEnabled(this, false);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATIONS
            );
            return;
        }

        NewSettingsStore.setNotificationsEnabled(this, true);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_NOTIFICATIONS) {
            return;
        }

        boolean granted = grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        NewSettingsStore.setNotificationsEnabled(this, granted);
        notificationsSwitch.setChecked(granted);
        if (!granted) {
            Toast.makeText(this, R.string.new_settings_notifications_denied, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void openStoreReview() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL)));
        } catch (ActivityNotFoundException exception) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URL)));
        }
    }

    private void shareApp() {
        String shareText = getString(R.string.new_settings_share_message, PLAY_STORE_URL);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, shareText);

        if (!hasShareTarget(intent)) {
            showCopyShareTextDialog(shareText);
            return;
        }

        try {
            startActivity(Intent.createChooser(intent, getString(R.string.new_settings_share_title)));
        } catch (ActivityNotFoundException exception) {
            showCopyShareTextDialog(shareText);
        }
    }

    private boolean hasShareTarget(Intent intent) {
        List<ResolveInfo> activities;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activities = getPackageManager().queryIntentActivities(
                    intent,
                    PackageManager.ResolveInfoFlags.of(0)
            );
        } else {
            activities = getPackageManager().queryIntentActivities(intent, 0);
        }

        return !activities.isEmpty();
    }

    private void showCopyShareTextDialog(String shareText) {
        TextView textView = new TextView(this);
        int padding = dp(18);
        textView.setPadding(padding, padding, padding, 0);
        textView.setText(shareText);
        textView.setTextColor(getColor(R.color.new_text_primary));
        textView.setTextSize(15);
        textView.setTextIsSelectable(true);

        new AlertDialog.Builder(this)
                .setTitle(R.string.new_settings_share_manual_title)
                .setMessage(R.string.new_settings_share_manual_message)
                .setView(textView)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.new_settings_copy_text, (dialog, which) -> {
                    ClipboardManager clipboard =
                            (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(ClipData.newPlainText(
                            getString(R.string.app_name),
                            shareText
                    ));
                    Toast.makeText(this, R.string.new_settings_share_copied, Toast.LENGTH_SHORT)
                            .show();
                })
                .show();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private String formatConsumption(double value) {
        if (value <= 0) {
            return getString(R.string.new_settings_not_configured);
        }

        return getString(R.string.new_settings_consumption_value, consumptionFormat.format(value));
    }

    private String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException exception) {
            return "";
        }
    }

    private void setupTransparentStatusBar() {
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }
}
