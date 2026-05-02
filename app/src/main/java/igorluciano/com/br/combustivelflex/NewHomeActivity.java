package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class NewHomeActivity extends Activity {
    private EditText gasolinePriceInput;
    private EditText ethanolPriceInput;
    private EditText gasolineConsumptionInput;
    private EditText ethanolConsumptionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home);

        gasolinePriceInput = findViewById(R.id.new_gasoline_price_input);
        ethanolPriceInput = findViewById(R.id.new_ethanol_price_input);
        gasolineConsumptionInput = findViewById(R.id.new_gasoline_consumption_input);
        ethanolConsumptionInput = findViewById(R.id.new_ethanol_consumption_input);

        findViewById(R.id.new_clear_button).setOnClickListener(view -> clearInputs());
    }

    private void clearInputs() {
        gasolinePriceInput.setText("");
        ethanolPriceInput.setText("");
        gasolineConsumptionInput.setText("");
        ethanolConsumptionInput.setText("");
        gasolinePriceInput.requestFocus();
    }
}
