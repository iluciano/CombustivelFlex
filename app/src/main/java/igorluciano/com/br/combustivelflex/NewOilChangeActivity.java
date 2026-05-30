package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import com.google.android.gms.ads.nativead.NativeAd;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewOilChangeActivity extends AppCompatActivity {

    public static final String EXTRA_SHOW_HISTORY = "showHistory";

    private static final String NATIVE_AD_UNIT_ID = "ca-app-pub-1199102836233471/7477718691";

    private static final int STATUS_EMPTY    = 0;
    private static final int STATUS_OK       = 1;
    private static final int STATUS_DUE      = 2;
    private static final int STATUS_OVERDUE  = 3;

    private final SimpleDateFormat dateFmt =
            new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
    private final DecimalFormat kmFmt =
            new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(new Locale("pt", "BR")));

    private NativeAd nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_oil_change);

        findViewById(R.id.oil_change_back).setOnClickListener(v -> finish());
        findViewById(R.id.oil_change_register_btn).setOnClickListener(v ->
                startActivity(new Intent(this, NewOilChangeRegisterActivity.class))
        );
        findViewById(R.id.oil_change_history_btn).setOnClickListener(v -> showHistory());
        // Last row is informational only
        findViewById(R.id.oil_change_last_row).setOnClickListener(v ->
                startActivity(new Intent(this, NewOilChangeRegisterActivity.class))
        );
        findViewById(R.id.oil_change_next_row).setOnClickListener(null);

        NativeAdHelper.load(this, NATIVE_AD_UNIT_ID,
                R.id.oil_change_ad_container, R.id.oil_change_ad_divider,
                ad -> nativeAd = ad);
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderScreen(OilChangeStore.getLatest(this));
        if (getIntent().getBooleanExtra(EXTRA_SHOW_HISTORY, false)) {
            getIntent().removeExtra(EXTRA_SHOW_HISTORY);
            showHistory();
        }
    }

    private void renderScreen(OilChangeRecord record) {
        if (record == null || record.date == null || record.date.isEmpty()) {
            renderEmptyState();
        } else {
            renderRecord(record);
        }
    }

    private void renderEmptyState() {
        setStatus(STATUS_EMPTY,
                getString(R.string.oil_change_no_record_title),
                getString(R.string.oil_change_no_record_msg));
        findViewById(R.id.oil_change_progress_section).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.oil_change_last_detail)).setText(
                getString(R.string.oil_change_no_record_detail));
        ((TextView) findViewById(R.id.oil_change_next_detail)).setText("—");
        findViewById(R.id.oil_change_items_section).setVisibility(View.GONE);
    }

    private void renderRecord(OilChangeRecord record) {
        String nextDate = (record.nextDate != null && !record.nextDate.isEmpty())
                ? record.nextDate
                : addMonths(record.date, 6);
        long nextKm = record.nextKm > 0 ? record.nextKm : record.km + 5000;

        int daysRemaining = getDaysRemaining(nextDate);
        int progressPercent = getProgressPercent(record.date, nextDate);

        int status = daysRemaining > 30 ? STATUS_OK
                   : daysRemaining >= 0 ? STATUS_DUE
                   : STATUS_OVERDUE;

        String statusTitle = status == STATUS_OK ? getString(R.string.oil_change_status_ok_title)
                : status == STATUS_DUE ? getString(R.string.oil_change_status_due_title)
                : getString(R.string.oil_change_status_overdue_title);
        String statusMsg = status == STATUS_OK ? getString(R.string.oil_change_status_ok_msg)
                : status == STATUS_DUE ? getString(R.string.oil_change_status_due_msg)
                : getString(R.string.oil_change_status_overdue_msg);
        setStatus(status, statusTitle, statusMsg);

        // Progress section
        findViewById(R.id.oil_change_progress_section).setVisibility(View.VISIBLE);

        TextView daysValue = findViewById(R.id.oil_change_days_value);
        TextView daysLabel = findViewById(R.id.oil_change_days_label);
        int absDays = Math.abs(daysRemaining);
        daysValue.setText(String.valueOf(absDays));
        daysLabel.setText(daysRemaining >= 0
                ? getString(R.string.oil_change_days_unit)
                : getString(R.string.oil_change_days_overdue_unit));

        ProgressBar bar = findViewById(R.id.oil_change_progress_bar);
        bar.setProgress(progressPercent);
        int barTint = status == STATUS_OK ? getColor(R.color.new_green)
                : status == STATUS_DUE ? getColor(R.color.new_orange)
                : getColor(R.color.new_red);
        bar.setProgressTintList(android.content.res.ColorStateList.valueOf(barTint));

        ((TextView) findViewById(R.id.oil_change_last_km_label))
                .setText(kmFmt.format(record.km) + " km");
        ((TextView) findViewById(R.id.oil_change_next_km_label))
                .setText(kmFmt.format(nextKm) + " km");
        ((TextView) findViewById(R.id.oil_change_last_detail))
                .setText(record.date + " • " + kmFmt.format(record.km) + " km");
        ((TextView) findViewById(R.id.oil_change_next_detail))
                .setText(nextDate + " • " + kmFmt.format(nextKm) + " km");

        renderItems(record);
    }

    private void setStatus(int status, String title, String msg) {
        LinearLayout section = findViewById(R.id.oil_change_status_section);
        TextView titleView = findViewById(R.id.oil_change_status_title);
        TextView msgView = findViewById(R.id.oil_change_status_msg);
        android.widget.ImageView iconView = findViewById(R.id.oil_change_status_icon);

        int bgColor, textColor, iconTint;
        switch (status) {
            case STATUS_OK:
                bgColor = getColor(R.color.new_green_light);
                textColor = getColor(R.color.new_green);
                iconTint = getColor(R.color.new_green);
                break;
            case STATUS_DUE:
                bgColor = getColor(R.color.new_orange_light);
                textColor = getColor(R.color.new_orange);
                iconTint = getColor(R.color.new_orange);
                break;
            case STATUS_OVERDUE:
                bgColor = getColor(R.color.new_red_light);
                textColor = getColor(R.color.new_red);
                iconTint = getColor(R.color.new_red);
                break;
            default:
                bgColor = getColor(R.color.new_panel);
                textColor = getColor(R.color.new_text_secondary);
                iconTint = getColor(R.color.new_text_muted);
                break;
        }

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(bgColor);
        bg.setCornerRadius(dp(12));
        section.setBackground(bg);
        titleView.setText(title);
        titleView.setTextColor(textColor);
        msgView.setText(msg);
        msgView.setTextColor(textColor);
        iconView.setColorFilter(iconTint);
    }

    private void renderItems(OilChangeRecord record) {
        LinearLayout section = findViewById(R.id.oil_change_items_section);
        LinearLayout row1 = findViewById(R.id.oil_change_items_row1);
        LinearLayout row2 = findViewById(R.id.oil_change_items_row2);
        row1.removeAllViews();
        row2.removeAllViews();

        List<String> items = new ArrayList<>();
        if (record.changedEngineOil)  items.add(getString(R.string.oil_change_engine_oil));
        if (record.changedOilFilter)  items.add(getString(R.string.oil_change_oil_filter));
        if (record.changedAirFilter)  items.add(getString(R.string.oil_change_air_filter));
        if (record.changedFuelFilter) items.add(getString(R.string.oil_change_fuel_filter));
        if (record.changedCabinFilter)items.add(getString(R.string.oil_change_cabin_filter));
        if (record.changedBrakeFluid) items.add(getString(R.string.oil_change_brake_fluid));
        if (record.changedSparkPlugs) items.add(getString(R.string.oil_change_spark_plugs));

        if (items.isEmpty()) {
            section.setVisibility(View.GONE);
            return;
        }
        section.setVisibility(View.VISIBLE);
        for (int i = 0; i < items.size(); i++) {
            TextView chip = buildItemChip(items.get(i));
            if (i < 3) {
                row1.addView(chip);
            } else {
                row2.addView(chip);
            }
        }
    }

    private TextView buildItemChip(String text) {
        TextView tv = new TextView(this);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(getColor(R.color.new_green_light));
        bg.setCornerRadius(dp(20));
        tv.setBackground(bg);
        tv.setText("✓ " + text);
        tv.setTextColor(getColor(R.color.new_green));
        tv.setTextSize(12);
        int hPad = dp(10), vPad = dp(5);
        tv.setPadding(hPad, vPad, hPad, vPad);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMarginEnd(dp(8));
        tv.setLayoutParams(lp);
        return tv;
    }

    private void showHistory() {
        startActivity(new Intent(this, NewOilChangeHistoryActivity.class));
    }

    private String addMonths(String date, int months) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFmt.parse(date));
            cal.add(Calendar.MONTH, months);
            return dateFmt.format(cal.getTime());
        } catch (Exception e) {
            return date;
        }
    }

    private int getDaysRemaining(String nextDate) {
        try {
            long nextTime = dateFmt.parse(nextDate).getTime();
            return (int) ((nextTime - new Date().getTime()) / (1000L * 60 * 60 * 24));
        } catch (Exception e) {
            return 0;
        }
    }

    private int getProgressPercent(String lastDate, String nextDate) {
        try {
            long lastTime = dateFmt.parse(lastDate).getTime();
            long nextTime = dateFmt.parse(nextDate).getTime();
            long today = new Date().getTime();
            if (today >= nextTime) return 100;
            if (today <= lastTime) return 0;
            return (int) ((today - lastTime) * 100L / (nextTime - lastTime));
        } catch (Exception e) {
            return 0;
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
