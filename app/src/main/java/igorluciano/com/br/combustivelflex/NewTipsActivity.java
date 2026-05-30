package igorluciano.com.br.combustivelflex;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;

public class NewTipsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_tips);

        findViewById(R.id.new_tips_home_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_tips_stations_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStationsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_tips_maintenance_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewMaintenanceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_tips_more_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewMoreActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

    }
}
