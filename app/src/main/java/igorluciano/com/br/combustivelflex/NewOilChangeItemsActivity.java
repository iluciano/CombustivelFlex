package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;

public class NewOilChangeItemsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_oil_change_items);

        findViewById(R.id.items_back).setOnClickListener(v -> finish());
        findViewById(R.id.items_save_btn).setOnClickListener(v -> saveAndContinue());
    }

    private void saveAndContinue() {
        Intent src = getIntent();
        CheckBox cbEngine  = findViewById(R.id.cb_engine_oil);
        CheckBox cbOil     = findViewById(R.id.cb_oil_filter);
        CheckBox cbAir     = findViewById(R.id.cb_air_filter);
        CheckBox cbFuel    = findViewById(R.id.cb_fuel_filter);
        CheckBox cbCabin   = findViewById(R.id.cb_cabin_filter);
        CheckBox cbBrake   = findViewById(R.id.cb_brake_fluid);
        CheckBox cbSparks  = findViewById(R.id.cb_spark_plugs);

        OilChangeRecord record = new OilChangeRecord();
        record.date             = src.getStringExtra(NewOilChangeRegisterActivity.EXTRA_DATE);
        record.km               = src.getLongExtra(NewOilChangeRegisterActivity.EXTRA_KM, 0);
        record.nextKm           = src.getLongExtra(NewOilChangeRegisterActivity.EXTRA_NEXT_KM, 0);
        record.nextDate         = src.getStringExtra(NewOilChangeRegisterActivity.EXTRA_NEXT_DATE);
        record.oilType          = src.getStringExtra(NewOilChangeRegisterActivity.EXTRA_OIL_TYPE);
        record.notes            = src.getStringExtra(NewOilChangeRegisterActivity.EXTRA_NOTES);
        record.changedEngineOil  = cbEngine.isChecked();
        record.changedOilFilter  = cbOil.isChecked();
        record.changedAirFilter  = cbAir.isChecked();
        record.changedFuelFilter = cbFuel.isChecked();
        record.changedCabinFilter= cbCabin.isChecked();
        record.changedBrakeFluid = cbBrake.isChecked();
        record.changedSparkPlugs = cbSparks.isChecked();

        OilChangeStore.save(this, record);

        startActivity(new Intent(this, NewOilChangeConfirmationActivity.class));
    }
}
