package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewOilChangeHistoryActivity extends AppCompatActivity {

    private enum Filter { TODOS, OLEO, FILTROS }
    private Filter currentFilter = Filter.TODOS;

    private final DecimalFormat kmFmt =
            new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(new Locale("pt", "BR")));

    private TextView filterAll, filterOil, filterFilters;
    private LinearLayout container;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_oil_change_history);

        filterAll     = findViewById(R.id.filter_all);
        filterOil     = findViewById(R.id.filter_oil);
        filterFilters = findViewById(R.id.filter_filters);
        container     = findViewById(R.id.history_container);
        emptyView     = findViewById(R.id.history_empty);

        filterAll.setOnClickListener(v -> setFilter(Filter.TODOS));
        filterOil.setOnClickListener(v -> setFilter(Filter.OLEO));
        filterFilters.setOnClickListener(v -> setFilter(Filter.FILTROS));

        findViewById(R.id.history_back).setOnClickListener(v -> finish());
        findViewById(R.id.history_new_btn).setOnClickListener(v ->
                startActivity(new Intent(this, NewOilChangeRegisterActivity.class)));

        findViewById(R.id.nav_home_tab).setOnClickListener(v -> navTo(NewStartActivity.class));
        findViewById(R.id.nav_stations_tab).setOnClickListener(v -> navTo(NewStationsActivity.class));
        findViewById(R.id.nav_maintenance_tab).setOnClickListener(v -> navTo(NewMaintenanceActivity.class));
        findViewById(R.id.nav_more_tab).setOnClickListener(v -> navTo(NewMoreActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadHistory();
    }

    private void navTo(Class<?> target) {
        Intent intent = new Intent(this, target);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void setFilter(Filter filter) {
        if (currentFilter == filter) return;
        currentFilter = filter;
        updateFilterStyles();
        loadHistory();
    }

    private void updateFilterStyles() {
        styleChip(filterAll, currentFilter == Filter.TODOS);
        styleChip(filterOil, currentFilter == Filter.OLEO);
        styleChip(filterFilters, currentFilter == Filter.FILTROS);
    }

    private void styleChip(TextView chip, boolean active) {
        if (active) {
            chip.setBackgroundResource(R.drawable.tab_station_active);
            chip.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            chip.setTypeface(null, Typeface.BOLD);
        } else {
            chip.setBackgroundResource(R.drawable.tab_station_inactive);
            chip.setTextColor(ContextCompat.getColor(this, R.color.new_text_muted));
            chip.setTypeface(null, Typeface.NORMAL);
        }
    }

    private void loadHistory() {
        container.removeAllViews();
        List<OilChangeRecord> all = OilChangeStore.getHistory(this);
        List<OilChangeRecord> filtered = new ArrayList<>();

        for (OilChangeRecord r : all) {
            if (currentFilter == Filter.TODOS) {
                filtered.add(r);
            } else if (currentFilter == Filter.OLEO && r.changedEngineOil) {
                filtered.add(r);
            } else if (currentFilter == Filter.FILTROS && hasAnyFilter(r)) {
                filtered.add(r);
            }
        }

        if (filtered.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            return;
        }
        emptyView.setVisibility(View.GONE);
        for (OilChangeRecord r : filtered) {
            container.addView(buildCard(r));
        }
    }

    private boolean hasAnyFilter(OilChangeRecord r) {
        return r.changedOilFilter || r.changedAirFilter
                || r.changedFuelFilter || r.changedCabinFilter;
    }

    private View buildCard(OilChangeRecord r) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setBackgroundResource(R.drawable.new_history_card_background);
        int pad = dp(14);
        card.setPadding(pad, pad, pad, pad);
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLp.setMargins(0, 0, 0, dp(12));
        card.setLayoutParams(cardLp);
        card.setClickable(true);
        card.setFocusable(true);
        card.setOnClickListener(v -> {
            Intent intent = new Intent(this, NewOilChangeDetailActivity.class);
            intent.putExtra(NewOilChangeDetailActivity.EXTRA_TIMESTAMP, r.timestamp);
            startActivity(intent);
        });

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        // Linha principal: data + km
        TextView title = new TextView(this);
        title.setText(r.date + "  •  " + kmFmt.format(r.km) + " km");
        title.setTextColor(getColor(R.color.new_text_primary));
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        content.addView(title);

        // Próxima
        TextView next = new TextView(this);
        next.setText(getString(R.string.oil_change_history_next_format,
                kmFmt.format(r.nextKm), r.nextDate != null ? r.nextDate : "—"));
        next.setTextColor(getColor(R.color.new_text_secondary));
        next.setTextSize(13);
        next.setPadding(0, dp(4), 0, 0);
        content.addView(next);

        // Itens
        String itemsLine = buildItemsLine(r);
        if (!itemsLine.isEmpty()) {
            TextView items = new TextView(this);
            items.setText(itemsLine);
            items.setTextColor(getColor(R.color.new_text_secondary));
            items.setTextSize(13);
            items.setPadding(0, dp(4), 0, 0);
            content.addView(items);
        }

        card.addView(content);

        ImageView chevron = new ImageView(this);
        chevron.setImageResource(R.drawable.ic_nav_chevron);
        chevron.setColorFilter(getColor(R.color.new_text_muted));
        int chevronSize = dp(16);
        chevron.setLayoutParams(new LinearLayout.LayoutParams(chevronSize, chevronSize));
        card.addView(chevron);

        return card;
    }

    private String buildItemsLine(OilChangeRecord r) {
        List<String> items = new ArrayList<>();
        if (r.changedEngineOil)  items.add(getString(R.string.oil_change_engine_oil));
        if (r.changedOilFilter)  items.add(getString(R.string.oil_change_oil_filter));
        if (r.changedAirFilter)  items.add(getString(R.string.oil_change_air_filter));
        if (r.changedFuelFilter) items.add(getString(R.string.oil_change_fuel_filter));
        if (r.changedCabinFilter)items.add(getString(R.string.oil_change_cabin_filter));
        if (r.changedBrakeFluid) items.add(getString(R.string.oil_change_brake_fluid));
        if (r.changedSparkPlugs) items.add(getString(R.string.oil_change_spark_plugs));
        return android.text.TextUtils.join("  •  ", items);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
