package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class NewStartActivity extends Activity {
    private InAppUpdateHelper inAppUpdateHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdgeHelper.enable(this);
        setContentView(R.layout.activity_new_start);
        EdgeToEdgeHelper.applySystemBarInsets(this);
        inAppUpdateHelper = new InAppUpdateHelper(this);
        inAppUpdateHelper.checkForUpdate();

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
        if (inAppUpdateHelper != null) {
            inAppUpdateHelper.resumeUpdateIfNeeded();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (inAppUpdateHelper != null) {
            inAppUpdateHelper.handleActivityResult(requestCode, resultCode);
        }
    }

    @Override
    protected void onDestroy() {
        if (inAppUpdateHelper != null) {
            inAppUpdateHelper.destroy();
        }
        super.onDestroy();
    }
}
