package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewHistoryActivity extends Activity {
    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy, HH:mm", new Locale("pt", "BR"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTransparentStatusBar();
        setContentView(R.layout.activity_new_history);

        findViewById(R.id.new_history_home_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        renderHistory(CalculationHistoryStore.list(this));
    }

    private void renderHistory(List<CalculationHistoryItem> items) {
        LinearLayout container = findViewById(R.id.new_history_container);
        TextView emptyText = findViewById(R.id.new_history_empty_text);
        container.removeAllViews();

        if (items.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            return;
        }

        emptyText.setVisibility(View.GONE);
        for (CalculationHistoryItem item : items) {
            container.addView(createHistoryCard(item));
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
        savingsText.setText(getString(R.string.new_history_savings_label)
                + " " + currencyFormat.format(item.savings));
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

    private void setupTransparentStatusBar() {
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
