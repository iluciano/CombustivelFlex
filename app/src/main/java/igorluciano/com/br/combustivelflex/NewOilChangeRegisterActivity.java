package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewOilChangeRegisterActivity extends AppCompatActivity {

    static final String EXTRA_DATE = "date";
    static final String EXTRA_KM = "km";
    static final String EXTRA_NEXT_KM = "nextKm";
    static final String EXTRA_NEXT_DATE = "nextDate";
    static final String EXTRA_OIL_TYPE = "oilType";
    static final String EXTRA_NOTES = "notes";

    private static final int DEFAULT_NEXT_KM = 5_000;
    private static final int MAX_OIL_TYPE_LENGTH = 50;
    private static final int MAX_NOTES_LENGTH = 120;

    private final SimpleDateFormat dateFmt =
            new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));

    private EditText regDate, regKm, regNextKm, regNextDate, regOilType, regNotes;
    private TextView toggleKm, toggleDate, notesCounter;
    private boolean nextDateManuallySet = false;
    private boolean byKmMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_oil_change_register);

        regDate      = findViewById(R.id.reg_date);
        regKm        = findViewById(R.id.reg_km);
        regNextKm    = findViewById(R.id.reg_next_km);
        regNextDate  = findViewById(R.id.reg_next_date);
        regOilType   = findViewById(R.id.reg_oil_type);
        regNotes     = findViewById(R.id.reg_notes);
        toggleKm     = findViewById(R.id.reg_toggle_km);
        toggleDate   = findViewById(R.id.reg_toggle_date);
        notesCounter = findViewById(R.id.reg_notes_counter);

        // Km masks
        regKm.addTextChangedListener(new KmTextWatcher(regKm));
        regNextKm.addTextChangedListener(new KmTextWatcher(regNextKm));

        // Default values
        regDate.setText(dateFmt.format(new Date()));
        regNextKm.setText("5.000");
        autoUpdateNextDate();

        // Date pickers
        View dateRow = findViewById(R.id.reg_date);
        regDate.setOnClickListener(v -> showDatePicker(regDate, false));
        regNextDate.setOnClickListener(v -> showDatePicker(regNextDate, true));
        View dateContainer = (View) regDate.getParent();
        dateContainer.setOnClickListener(v -> showDatePicker(regDate, false));
        View nextDateContainer = (View) regNextDate.getParent();
        nextDateContainer.setOnClickListener(v -> showDatePicker(regNextDate, true));

        // Toggle
        toggleKm.setOnClickListener(v -> setMode(true));
        toggleDate.setOnClickListener(v -> setMode(false));

        // Notes counter
        regNotes.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int i, int b, int c) {}
            @Override
            public void afterTextChanged(Editable s) {
                notesCounter.setText(s.length() + "/120");
            }
        });

        findViewById(R.id.reg_back).setOnClickListener(v -> finish());
        findViewById(R.id.reg_continue_btn).setOnClickListener(v -> onContinue());
    }

    private void setMode(boolean byKm) {
        byKmMode = byKm;
        if (byKm) {
            toggleKm.setBackgroundResource(R.drawable.tab_station_active);
            toggleKm.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            toggleKm.setTypeface(null, Typeface.BOLD);
            toggleDate.setBackgroundResource(R.drawable.tab_station_inactive);
            toggleDate.setTextColor(ContextCompat.getColor(this, R.color.new_text_muted));
            toggleDate.setTypeface(null, Typeface.NORMAL);
            ((TextView) findViewById(R.id.reg_next_km_label))
                    .setText(getString(R.string.oil_change_next_km_label));
            ((TextView) findViewById(R.id.reg_next_date_label))
                    .setText(getString(R.string.oil_change_next_date_label));
        } else {
            toggleDate.setBackgroundResource(R.drawable.tab_station_active);
            toggleDate.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            toggleDate.setTypeface(null, Typeface.BOLD);
            toggleKm.setBackgroundResource(R.drawable.tab_station_inactive);
            toggleKm.setTextColor(ContextCompat.getColor(this, R.color.new_text_muted));
            toggleKm.setTypeface(null, Typeface.NORMAL);
            ((TextView) findViewById(R.id.reg_next_km_label))
                    .setText(getString(R.string.oil_change_next_km_optional_label));
            ((TextView) findViewById(R.id.reg_next_date_label))
                    .setText(getString(R.string.oil_change_next_date_required_label));
        }
    }

    private void showDatePicker(final EditText target, final boolean isNextDate) {
        Calendar cal = Calendar.getInstance();
        String current = target.getText().toString();
        if (!current.isEmpty()) {
            try { cal.setTime(dateFmt.parse(current)); } catch (Exception ignored) {}
        }
        new DatePickerDialog(this, (view, year, month, day) -> {
            cal.set(year, month, day);
            target.setText(dateFmt.format(cal.getTime()));
            if (isNextDate) {
                nextDateManuallySet = true;
            } else {
                autoUpdateNextDate();
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void autoUpdateNextDate() {
        if (nextDateManuallySet) return;
        String dateStr = regDate.getText().toString();
        if (dateStr.isEmpty()) return;
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFmt.parse(dateStr));
            cal.add(Calendar.MONTH, 6);
            regNextDate.setText(dateFmt.format(cal.getTime()));
        } catch (Exception ignored) {}
    }

    private void onContinue() {
        String date = regDate.getText().toString().trim();
        String kmStr = regKm.getText().toString().trim();

        if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, R.string.oil_change_date_required, Toast.LENGTH_SHORT).show();
            return;
        }
        long km = KmTextWatcher.parseKm(kmStr);
        if (km <= 0) {
            Toast.makeText(this, R.string.oil_change_km_required, Toast.LENGTH_SHORT).show();
            return;
        }

        // O campo "Km para próxima troca" é o intervalo a somar ao km atual
        long intervalKm = KmTextWatcher.parseKm(regNextKm.getText().toString().trim());
        if (intervalKm <= 0) intervalKm = DEFAULT_NEXT_KM;
        long nextKm = km + intervalKm;

        String nextDate = regNextDate.getText().toString().trim();
        if (nextDate.isEmpty()) nextDate = autoComputeNextDate(date, 6);

        String oilType = sanitize(regOilType.getText().toString(), MAX_OIL_TYPE_LENGTH);
        String notes = sanitize(regNotes.getText().toString(), MAX_NOTES_LENGTH);

        Intent intent = new Intent(this, NewOilChangeItemsActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        intent.putExtra(EXTRA_KM, km);
        intent.putExtra(EXTRA_NEXT_KM, nextKm);
        intent.putExtra(EXTRA_NEXT_DATE, nextDate);
        intent.putExtra(EXTRA_OIL_TYPE, oilType);
        intent.putExtra(EXTRA_NOTES, notes);
        startActivity(intent);
    }

    private String autoComputeNextDate(String date, int months) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateFmt.parse(date));
            cal.add(Calendar.MONTH, months);
            return dateFmt.format(cal.getTime());
        } catch (Exception e) {
            return date;
        }
    }

    private String sanitize(String input, int maxLength) {
        if (input == null) return "";
        // Remove ASCII control characters (keep printable chars + accented letters)
        String clean = input.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "").trim();
        if (clean.length() > maxLength) clean = clean.substring(0, maxLength);
        return clean;
    }
}
