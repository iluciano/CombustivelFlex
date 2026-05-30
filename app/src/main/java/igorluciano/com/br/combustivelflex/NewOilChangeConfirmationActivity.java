package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewOilChangeConfirmationActivity extends AppCompatActivity {

    private final DecimalFormat kmFmt =
            new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(new Locale("pt", "BR")));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_oil_change_confirmation);

        OilChangeRecord record = OilChangeStore.getLatest(this);
        if (record != null) bindRecord(record);

        findViewById(R.id.confirm_back).setOnClickListener(v -> goToSummary());
        findViewById(R.id.confirm_back_btn).setOnClickListener(v -> goToSummary());
        findViewById(R.id.confirm_history_btn).setOnClickListener(v ->
                startActivity(new Intent(this, NewOilChangeHistoryActivity.class)));
    }

    private void bindRecord(OilChangeRecord r) {
        ((TextView) findViewById(R.id.confirm_date)).setText(r.date);

        String kmText = kmFmt.format(r.km) + " km";
        ((TextView) findViewById(R.id.confirm_km)).setText(kmText);

        String nextText = "";
        if (r.nextKm > 0) nextText = kmFmt.format(r.nextKm) + " km";
        if (r.nextDate != null && !r.nextDate.isEmpty()) {
            nextText += (nextText.isEmpty() ? "" : "  ou  ") + r.nextDate;
        }
        ((TextView) findViewById(R.id.confirm_next)).setText(nextText.isEmpty() ? "—" : nextText);

        renderItems(r);
    }

    private void renderItems(OilChangeRecord r) {
        LinearLayout container = findViewById(R.id.confirm_items_container);
        TextView noItems = findViewById(R.id.confirm_no_items);
        container.removeAllViews();

        List<String> items = new ArrayList<>();
        if (r.changedEngineOil)  items.add(getString(R.string.oil_change_engine_oil));
        if (r.changedOilFilter)  items.add(getString(R.string.oil_change_oil_filter));
        if (r.changedAirFilter)  items.add(getString(R.string.oil_change_air_filter));
        if (r.changedFuelFilter) items.add(getString(R.string.oil_change_fuel_filter));
        if (r.changedCabinFilter)items.add(getString(R.string.oil_change_cabin_filter));
        if (r.changedBrakeFluid) items.add(getString(R.string.oil_change_brake_fluid));
        if (r.changedSparkPlugs) items.add(getString(R.string.oil_change_spark_plugs));

        if (items.isEmpty()) {
            noItems.setVisibility(View.VISIBLE);
            return;
        }
        noItems.setVisibility(View.GONE);
        for (String item : items) {
            container.addView(buildItemRow(item));
        }
    }

    private View buildItemRow(String text) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 0, dp(8));
        row.setLayoutParams(lp);

        // Green checkmark circle
        android.widget.ImageView icon = new android.widget.ImageView(this);
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(getColor(R.color.new_green));
        icon.setBackground(circle);
        icon.setImageResource(R.drawable.ic_check);
        int iconSize = dp(22);
        icon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));

        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(getColor(R.color.new_text_primary));
        tv.setTextSize(14);
        LinearLayout.LayoutParams tvLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tvLp.setMarginStart(dp(10));
        tv.setLayoutParams(tvLp);

        row.addView(icon);
        row.addView(tv);
        return row;
    }

    private void goToSummary() {
        Intent intent = new Intent(this, NewOilChangeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void showHistory() {
        List<OilChangeRecord> history = OilChangeStore.getHistory(this);
        if (history.isEmpty()) return;

        DecimalFormat fmt = new DecimalFormat(
                "#,###", DecimalFormatSymbols.getInstance(new Locale("pt", "BR")));
        StringBuilder sb = new StringBuilder();
        for (OilChangeRecord r : history) {
            sb.append(r.date).append(" • ").append(fmt.format(r.km)).append(" km");
            if (r.nextDate != null && !r.nextDate.isEmpty()) {
                sb.append("\n   Próxima: ").append(r.nextDate);
            }
            sb.append("\n\n");
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.oil_change_history_btn)
                .setMessage(sb.toString().trim())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
