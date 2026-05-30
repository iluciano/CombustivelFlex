package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NewOilChangeDeleteActivity extends AppCompatActivity {

    static final String EXTRA_TIMESTAMP = "timestamp";

    private final DecimalFormat kmFmt =
            new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(new Locale("pt", "BR")));

    private long timestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_oil_change_delete);

        timestamp = getIntent().getLongExtra(EXTRA_TIMESTAMP, -1);
        OilChangeRecord record = findRecord();
        if (record == null) { finish(); return; }

        ((TextView) findViewById(R.id.delete_date))
                .setText(getString(R.string.oil_change_delete_data_format, record.date));
        ((TextView) findViewById(R.id.delete_km))
                .setText(getString(R.string.oil_change_delete_km_format, kmFmt.format(record.km)));

        findViewById(R.id.delete_back).setOnClickListener(v -> finish());
        findViewById(R.id.delete_cancel_btn).setOnClickListener(v -> finish());
        findViewById(R.id.delete_confirm_btn).setOnClickListener(v -> onDelete());
    }

    private OilChangeRecord findRecord() {
        for (OilChangeRecord r : OilChangeStore.getHistory(this)) {
            if (r.timestamp == timestamp) return r;
        }
        return null;
    }

    private void onDelete() {
        OilChangeStore.delete(this, timestamp);
        Toast.makeText(this, R.string.oil_change_deleted, Toast.LENGTH_SHORT).show();
        // Volta para o histórico, limpando a tela de detalhe do backstack
        Intent intent = new Intent(this, NewOilChangeHistoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
