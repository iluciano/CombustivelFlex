package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NewOilChangeEditActivity extends AppCompatActivity {

    static final String EXTRA_TIMESTAMP = "timestamp";

    private static final int DEFAULT_NEXT_KM = 5_000;
    private static final int MAX_OIL_TYPE_LENGTH = 50;
    private static final int MAX_NOTES_LENGTH = 120;

    private final SimpleDateFormat dateFmt =
            new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
    private final DecimalFormat kmFmt =
            new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(new Locale("pt", "BR")));

    private EditText editDate, editKm, editNextKm, editNextDate, editOilType, editNotes;
    private CheckBox cbEngine, cbOil, cbAir, cbFuel, cbCabin, cbBrake, cbSparks;
    private long timestamp;
    private OilChangeRecord original;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_oil_change_edit);

        timestamp = getIntent().getLongExtra(EXTRA_TIMESTAMP, -1);
        original = findRecord();
        if (original == null) { finish(); return; }

        editDate     = findViewById(R.id.edit_date);
        editKm       = findViewById(R.id.edit_km);
        editNextKm   = findViewById(R.id.edit_next_km);
        editNextDate = findViewById(R.id.edit_next_date);
        editOilType  = findViewById(R.id.edit_oil_type);
        editNotes    = findViewById(R.id.edit_notes);

        cbEngine = findViewById(R.id.edit_cb_engine_oil);
        cbOil    = findViewById(R.id.edit_cb_oil_filter);
        cbAir    = findViewById(R.id.edit_cb_air_filter);
        cbFuel   = findViewById(R.id.edit_cb_fuel_filter);
        cbCabin  = findViewById(R.id.edit_cb_cabin_filter);
        cbBrake  = findViewById(R.id.edit_cb_brake_fluid);
        cbSparks = findViewById(R.id.edit_cb_spark_plugs);

        // Máscaras de km
        editKm.addTextChangedListener(new KmTextWatcher(editKm));
        editNextKm.addTextChangedListener(new KmTextWatcher(editNextKm));

        // Date pickers
        editDate.setOnClickListener(v -> showDatePicker(editDate));
        editNextDate.setOnClickListener(v -> showDatePicker(editNextDate));
        ((View) editDate.getParent()).setOnClickListener(v -> showDatePicker(editDate));
        ((View) editNextDate.getParent()).setOnClickListener(v -> showDatePicker(editNextDate));

        prefill(original);

        findViewById(R.id.edit_back).setOnClickListener(v -> finish());
        findViewById(R.id.edit_save_btn).setOnClickListener(v -> onSave());
    }

    private OilChangeRecord findRecord() {
        for (OilChangeRecord r : OilChangeStore.getHistory(this)) {
            if (r.timestamp == timestamp) return r;
        }
        return null;
    }

    private void prefill(OilChangeRecord r) {
        editDate.setText(r.date);
        editKm.setText(kmFmt.format(r.km));
        // Campo mostra o intervalo (nextKm - km), não o valor absoluto
        long intervalKm = r.nextKm > r.km ? r.nextKm - r.km : DEFAULT_NEXT_KM;
        editNextKm.setText(kmFmt.format(intervalKm));
        editNextDate.setText(r.nextDate);

        // Tipo de óleo — só mostra se preenchido
        if (r.oilType != null && !r.oilType.trim().isEmpty()) {
            findViewById(R.id.edit_oil_type_label).setVisibility(View.VISIBLE);
            editOilType.setVisibility(View.VISIBLE);
            editOilType.setText(r.oilType);
        }
        // Observações — só mostra se preenchido
        if (r.notes != null && !r.notes.trim().isEmpty()) {
            findViewById(R.id.edit_notes_label).setVisibility(View.VISIBLE);
            editNotes.setVisibility(View.VISIBLE);
            editNotes.setText(r.notes);
        }

        cbEngine.setChecked(r.changedEngineOil);
        cbOil.setChecked(r.changedOilFilter);
        cbAir.setChecked(r.changedAirFilter);
        cbFuel.setChecked(r.changedFuelFilter);
        cbCabin.setChecked(r.changedCabinFilter);
        cbBrake.setChecked(r.changedBrakeFluid);
        cbSparks.setChecked(r.changedSparkPlugs);
    }

    private void showDatePicker(final EditText target) {
        Calendar cal = Calendar.getInstance();
        String current = target.getText().toString();
        if (!current.isEmpty()) {
            try { cal.setTime(dateFmt.parse(current)); } catch (Exception ignored) {}
        }
        new DatePickerDialog(this, (view, year, month, day) -> {
            cal.set(year, month, day);
            target.setText(dateFmt.format(cal.getTime()));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void onSave() {
        String date = editDate.getText().toString().trim();
        if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, R.string.oil_change_date_required, Toast.LENGTH_SHORT).show();
            return;
        }
        long km = KmTextWatcher.parseKm(editKm.getText().toString().trim());
        if (km <= 0) {
            Toast.makeText(this, R.string.oil_change_km_required, Toast.LENGTH_SHORT).show();
            return;
        }

        // O campo "Km para próxima troca" é o intervalo a somar ao km atual
        long intervalKm = KmTextWatcher.parseKm(editNextKm.getText().toString().trim());
        if (intervalKm <= 0) intervalKm = DEFAULT_NEXT_KM;
        long nextKm = km + intervalKm;

        String nextDate = editNextDate.getText().toString().trim();
        if (nextDate.isEmpty()) nextDate = autoComputeNextDate(date, 6);

        OilChangeRecord updated = new OilChangeRecord();
        updated.timestamp = timestamp;          // mantém o mesmo id
        updated.date = date;
        updated.km = km;
        updated.nextKm = nextKm;
        updated.nextDate = nextDate;
        updated.oilType = sanitize(editOilType.getText().toString(), MAX_OIL_TYPE_LENGTH);
        updated.notes = sanitize(editNotes.getText().toString(), MAX_NOTES_LENGTH);
        updated.changedEngineOil  = cbEngine.isChecked();
        updated.changedOilFilter  = cbOil.isChecked();
        updated.changedAirFilter  = cbAir.isChecked();
        updated.changedFuelFilter = cbFuel.isChecked();
        updated.changedCabinFilter= cbCabin.isChecked();
        updated.changedBrakeFluid = cbBrake.isChecked();
        updated.changedSparkPlugs = cbSparks.isChecked();

        OilChangeStore.update(this, updated);
        Toast.makeText(this, R.string.oil_change_changes_saved, Toast.LENGTH_SHORT).show();
        finish();
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
        String clean = input.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "").trim();
        if (clean.length() > maxLength) clean = clean.substring(0, maxLength);
        return clean;
    }
}
