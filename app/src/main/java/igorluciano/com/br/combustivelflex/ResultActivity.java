package igorluciano.com.br.combustivelflex;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by igorl on 22/02/2016.
 */
public class ResultActivity extends MainActivity {
    private TextView txtV;

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        setContentView(R.layout.result_activity);

        txtV = (TextView)findViewById(R.id.txtResultado_text_view);
        Bundle bundle = getIntent().getExtras();

        if (bundle.containsKey("valGas") && bundle.containsKey("valEta")){
            double val1 = bundle.getDouble("valEta");
            double val2 = bundle.getDouble("valGas");
            double res = val1/val2;

            if (res < 0.70){
                txtV.setText("ETANOL");
            }else{
                txtV.setText("GASOLINA");
            }
        }else{
            txtV.setText("Volte e digite valores válidos.");
        }

        AdView ads = (AdView) findViewById(R.id.adView);
        AdRequest request = new AdRequest.Builder().build();
        ads.loadAd(request);
    }
}
