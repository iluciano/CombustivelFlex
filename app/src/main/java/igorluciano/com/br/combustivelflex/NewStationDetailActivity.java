package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.core.view.WindowCompat;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NewStationDetailActivity extends Activity {

    static final String EXTRA_POSTO = "posto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        setContentView(R.layout.activity_new_station_detail);

        Posto posto = getIntent().getParcelableExtra(EXTRA_POSTO);
        if (posto == null) { finish(); return; }

        findViewById(R.id.detail_back_button).setOnClickListener(v -> finish());

        bindBrand(posto);

        ((TextView) findViewById(R.id.detail_posto_nome))
                .setText(posto.getNome() != null ? posto.getNome() : "");
        ((TextView) findViewById(R.id.detail_distancia))
                .setText(getString(R.string.detail_distance_from_you, formatDistancia(posto.getDistanciaMetros())));

        DecimalFormat fmt = new DecimalFormat(
                "#,##0.00", DecimalFormatSymbols.getInstance(new Locale("pt", "BR")));
        ((TextView) findViewById(R.id.detail_gasoline_price))
                .setText(formatPreco(fmt, posto.getPrecoGasolinaComum()));
        ((TextView) findViewById(R.id.detail_ethanol_price))
                .setText(formatPreco(fmt, posto.getPrecoEtanol()));

        ((TextView) findViewById(R.id.detail_address_text))
                .setText(buildAddress(posto));

        findViewById(R.id.detail_map_button).setOnClickListener(v -> openMap(posto));
    }

    private void bindBrand(Posto posto) {
        FrameLayout container = findViewById(R.id.detail_brand_container);
        ImageView logo = findViewById(R.id.detail_brand_logo);
        TextView initial = findViewById(R.id.detail_brand_initial);

        @DrawableRes int logoRes = logoRes(posto.getBandeira());
        if (logoRes != 0) {
            container.setBackground(null);
            initial.setVisibility(View.GONE);
            logo.setImageResource(logoRes);
            logo.setVisibility(View.VISIBLE);
        } else {
            logo.setVisibility(View.GONE);
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(0xFF98A2B3);
            container.setBackground(circle);
            initial.setText("?");
            initial.setVisibility(View.VISIBLE);
        }
    }

    @DrawableRes
    private int logoRes(String bandeira) {
        if (bandeira == null) return 0;
        switch (bandeira.toLowerCase(Locale.ROOT)) {
            case "ipiranga": return R.drawable.ic_brand_ipiranga;
            case "shell":    return R.drawable.ic_brand_shell;
            case "ale":      return R.drawable.ic_brand_ale;
            case "vibra":    return R.drawable.ic_brand_vibra;
            default:         return 0;
        }
    }

    private String formatDistancia(float metros) {
        if (metros < 1000) {
            return getString(R.string.stations_distance_meters, (int) metros);
        }
        DecimalFormat fmt = new DecimalFormat(
                "0.0", DecimalFormatSymbols.getInstance(new Locale("pt", "BR")));
        return getString(R.string.stations_distance_km, fmt.format(metros / 1000.0));
    }

    private String formatPreco(DecimalFormat fmt, double preco) {
        if (preco <= 0) return "—";
        return getString(R.string.stations_price_format, fmt.format(preco));
    }

    private String buildAddress(Posto posto) {
        StringBuilder sb = new StringBuilder();
        if (posto.getRua() != null && !posto.getRua().isEmpty()) {
            sb.append(posto.getRua());
            if (posto.getNumero() != null && !posto.getNumero().isEmpty()) {
                sb.append(", ").append(posto.getNumero());
            }
        }
        if (posto.getBairro() != null && !posto.getBairro().isEmpty()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(posto.getBairro());
        }
        if (posto.getCidade() != null && !posto.getCidade().isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(posto.getCidade());
            if (posto.getEstado() != null && !posto.getEstado().isEmpty()) {
                sb.append(" - ").append(posto.getEstado());
            }
        }
        return sb.length() > 0 ? sb.toString() : getString(R.string.detail_address_unavailable);
    }

    private void openMap(Posto posto) {
        String label = Uri.encode(posto.getNome() != null ? posto.getNome() : "Posto");
        String uri = "geo:" + posto.getLatitude() + "," + posto.getLongitude()
                + "?q=" + posto.getLatitude() + "," + posto.getLongitude()
                + "(" + label + ")";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        } catch (Exception ignored) {}
    }
}
