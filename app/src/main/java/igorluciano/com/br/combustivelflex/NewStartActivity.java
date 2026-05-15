package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

public class NewStartActivity extends AppCompatActivity {
    private static final int UPDATE_REQUEST_CODE = 1001;

    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_start);
        appUpdateManager = AppUpdateManagerFactory.create(this);
        checkForAppUpdate();

        findViewById(R.id.new_start_calculate_card).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewHomeActivity.class);
            intent.putExtra(NewHomeActivity.EXTRA_CLEAR_INPUTS, true);
            startActivity(intent);
        });
        findViewById(R.id.new_start_history_card).setOnClickListener(
                view -> startActivity(new Intent(this, NewHistoryActivity.class))
        );
        findViewById(R.id.new_start_history_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewHistoryActivity.class))
        );
        findViewById(R.id.new_start_stations_card).setOnClickListener(
                view -> startActivity(new Intent(this, NewStationsActivity.class))
        );
        findViewById(R.id.new_start_stations_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewStationsActivity.class))
        );
        findViewById(R.id.new_start_tips_card).setOnClickListener(
                view -> startActivity(new Intent(this, NewTipsActivity.class))
        );
        findViewById(R.id.new_start_settings_card).setOnClickListener(
                view -> startActivity(new Intent(this, NewSettingsActivity.class))
        );
        findViewById(R.id.new_start_more_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewMoreActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeAppUpdateIfNeeded();
    }

    private void checkForAppUpdate() {
        if (appUpdateManager == null) {
            return;
        }

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startAppUpdate(appUpdateInfo);
            }
        });
    }

    private void resumeAppUpdateIfNeeded() {
        if (appUpdateManager == null) {
            return;
        }

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startAppUpdate(appUpdateInfo);
            }
        });
    }

    private void startAppUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    UPDATE_REQUEST_CODE
            );
        } catch (Exception ignored) {
            // Google Play handles update availability; the app can continue normally if it cannot start.
        }
    }
}
