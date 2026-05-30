package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewOilChangeDetailActivity extends AppCompatActivity {

    static final String EXTRA_TIMESTAMP = "timestamp";

    private final DecimalFormat kmFmt =
            new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(new Locale("pt", "BR")));

    private long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_oil_change_detail);

        timestamp = getIntent().getLongExtra(EXTRA_TIMESTAMP, -1);

        findViewById(R.id.detail_back).setOnClickListener(v -> finish());
        findViewById(R.id.detail_edit_btn).setOnClickListener(v -> {
            Intent intent = new Intent(this, NewOilChangeEditActivity.class);
            intent.putExtra(NewOilChangeEditActivity.EXTRA_TIMESTAMP, timestamp);
            startActivity(intent);
        });
        findViewById(R.id.detail_delete_btn).setOnClickListener(v -> {
            Intent intent = new Intent(this, NewOilChangeDeleteActivity.class);
            intent.putExtra(NewOilChangeDeleteActivity.EXTRA_TIMESTAMP, timestamp);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        OilChangeRecord record = findRecord();
        if (record == null) {
            // Foi excluída
            finish();
            return;
        }
        bindRecord(record);
    }

    private OilChangeRecord findRecord() {
        for (OilChangeRecord r : OilChangeStore.getHistory(this)) {
            if (r.timestamp == timestamp) return r;
        }
        return null;
    }

    private void bindRecord(OilChangeRecord r) {
        ((TextView) findViewById(R.id.detail_date)).setText(r.date);
        ((TextView) findViewById(R.id.detail_km)).setText(kmFmt.format(r.km) + " km");
        ((TextView) findViewById(R.id.detail_next)).setText(getString(
                R.string.oil_change_detail_next_format,
                kmFmt.format(r.nextKm),
                r.nextDate != null && !r.nextDate.isEmpty() ? r.nextDate : "—"));

        // Observações (opcional)
        bindOptional(r.notes, R.id.detail_notes_label, R.id.detail_notes);
        // Tipo de óleo (opcional)
        bindOptional(r.oilType, R.id.detail_oil_type_label, R.id.detail_oil_type);

        renderItems(r);
    }

    private void bindOptional(String value, int labelId, int valueId) {
        View label = findViewById(labelId);
        TextView text = findViewById(valueId);
        if (value != null && !value.trim().isEmpty()) {
            label.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
            text.setText(value);
        } else {
            label.setVisibility(View.GONE);
            text.setVisibility(View.GONE);
        }
    }

    private void renderItems(OilChangeRecord r) {
        LinearLayout container = findViewById(R.id.detail_items_container);
        TextView noItems = findViewById(R.id.detail_no_items);
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
        row.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dp(8));
        row.setLayoutParams(lp);

        ImageView icon = new ImageView(this);
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
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvLp.setMarginStart(dp(10));
        tv.setLayoutParams(tvLp);

        row.addView(icon);
        row.addView(tv);
        return row;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
