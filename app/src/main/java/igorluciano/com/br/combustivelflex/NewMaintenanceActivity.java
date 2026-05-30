package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import com.google.android.gms.ads.nativead.NativeAd;

public class NewMaintenanceActivity extends AppCompatActivity {

    private static final String NATIVE_AD_UNIT_ID = "ca-app-pub-1199102836233471/7477718691";

    private NativeAd nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_maintenance);

        findViewById(R.id.maintenance_oil_change_card).setOnClickListener(v ->
                startActivity(new Intent(this, NewOilChangeActivity.class))
        );

        findViewById(R.id.new_maintenance_home_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_maintenance_stations_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStationsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_maintenance_more_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewMoreActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        NativeAdHelper.load(this, NATIVE_AD_UNIT_ID,
                R.id.maintenance_ad_container, R.id.maintenance_ad_divider,
                ad -> nativeAd = ad);
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }
}
