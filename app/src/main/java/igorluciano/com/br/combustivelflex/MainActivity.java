package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends Activity {
    private EditText gasolineInput;
    private EditText ethanolInput;
    private AdView bottomButtonsAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gasolineInput = findViewById(R.id.gasoline_input);
        ethanolInput = findViewById(R.id.ethanol_input);

        findViewById(R.id.clear_button).setOnClickListener(view -> {
            gasolineInput.setText("");
            ethanolInput.setText("");
            gasolineInput.requestFocus();
        });

        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();
        FrameLayout adContainer = findViewById(R.id.bottom_buttons_ad_container);
        bottomButtonsAd = AdMobBanner.loadMainBanner(this, adContainer);
    }

    public void onClickResult(View view) {
        String gasolineText = gasolineInput.getText().toString();
        String ethanolText = ethanolInput.getText().toString();

        if (TextUtils.isEmpty(gasolineText) || TextUtils.isEmpty(ethanolText)) {
            Toast.makeText(this, R.string.enter_values, Toast.LENGTH_SHORT).show();
            return;
        }

        Double gasoline = parsePrice(gasolineText);
        Double ethanol = parsePrice(ethanolText);

        if (gasoline == null || ethanol == null || gasoline <= 0 || ethanol <= 0) {
            Toast.makeText(this, R.string.enter_valid_values, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra(ResultActivity.EXTRA_GASOLINE, gasoline);
        intent.putExtra(ResultActivity.EXTRA_ETHANOL, ethanol);
        startActivity(intent);
    }

    private Double parsePrice(String value) {
        try {
            return Double.parseDouble(value.trim().replace(',', '.'));
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        if (bottomButtonsAd != null) {
            bottomButtonsAd.destroy();
        }
        super.onDestroy();
    }
}
