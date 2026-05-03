package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class NewTipsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupTransparentStatusBar();
        setContentView(R.layout.activity_new_tips);

        findViewById(R.id.new_tips_home_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewStartActivity.class))
        );
        findViewById(R.id.new_tips_history_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewHistoryActivity.class))
        );
        findViewById(R.id.new_tips_stations_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewStationsActivity.class))
        );
        findViewById(R.id.new_tips_more_tab).setOnClickListener(
                view -> startActivity(new Intent(this, NewMoreActivity.class))
        );
    }

    private void setupTransparentStatusBar() {
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }
}
