package igorluciano.com.br.combustivelflex;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.WindowCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NewStationsActivity extends Activity {

    private static final int REQUEST_LOCATION = 42;

    private FusedLocationProviderClient fusedLocationClient;
    private PostoAdapter adapter;
    private View loadingView;
    private View recyclerView;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        setContentView(R.layout.activity_new_stations);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        RecyclerView rv = findViewById(R.id.stations_recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new PostoAdapter();
        adapter.setOnItemClickListener(posto -> {
            Intent detail = new Intent(this, NewStationDetailActivity.class);
            detail.putExtra(NewStationDetailActivity.EXTRA_POSTO, posto);
            startActivity(detail);
        });
        rv.setAdapter(adapter);

        loadingView = findViewById(R.id.stations_loading);
        recyclerView = rv;
        statusText = findViewById(R.id.stations_status);

        findViewById(R.id.stations_map_button).setOnClickListener(v -> openMap(null));

        findViewById(R.id.new_stations_home_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewStartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_stations_history_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewHistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
        findViewById(R.id.new_stations_more_tab).setOnClickListener(view -> {
            Intent intent = new Intent(this, NewMoreActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        checkAndLoadStations();
    }

    private void checkAndLoadStations() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            loadStations();
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION
            );
        }
    }

    @SuppressWarnings("MissingPermission")
    private void loadStations() {
        showLoading();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        loadFromFirestore(location.getLatitude(), location.getLongitude(), location);
                    } else {
                        CancellationTokenSource cts = new CancellationTokenSource();
                        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.getToken())
                                .addOnSuccessListener(this, fresh -> {
                                    if (fresh != null) {
                                        loadFromFirestore(fresh.getLatitude(), fresh.getLongitude(), fresh);
                                    } else {
                                        showStatus(getString(R.string.stations_error_location));
                                    }
                                })
                                .addOnFailureListener(this, e -> showStatus(getString(R.string.stations_error_location)));
                    }
                })
                .addOnFailureListener(this, e -> showStatus(getString(R.string.stations_error_location)));
    }

    private void loadFromFirestore(double userLat, double userLon, Location userLocation) {
        FirebaseFirestore.getInstance()
                .collection("postos")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Posto> postos = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Posto posto = new Posto();
                        posto.setId(doc.getId());
                        posto.setNome(doc.getString("nome"));
                        posto.setBandeira(doc.getString("bandeira"));

                        Double lat = doc.getDouble("latitude");
                        Double lon = doc.getDouble("longitude");
                        Double preco = doc.getDouble("preco_gasolina");
                        Double precoEtanol = doc.getDouble("preco_etanol");

                        if (lat == null || lon == null) continue;
                        posto.setLatitude(lat);
                        posto.setLongitude(lon);
                        if (preco != null) posto.setPrecoGasolinaComum(preco);
                        if (precoEtanol != null) posto.setPrecoEtanol(precoEtanol);

                        posto.setRua(doc.getString("rua"));
                        posto.setNumero(doc.getString("numero"));
                        posto.setBairro(doc.getString("bairro"));
                        posto.setCidade(doc.getString("cidade"));
                        posto.setEstado(doc.getString("estado"));

                        float[] result = new float[1];
                        Location.distanceBetween(userLat, userLon, lat, lon, result);
                        if (result[0] > 10_000) continue;
                        posto.setDistanciaMetros(result[0]);

                        postos.add(posto);
                    }

                    Collections.sort(postos, (a, b) ->
                            Float.compare(a.getDistanciaMetros(), b.getDistanciaMetros()));

                    runOnUiThread(() -> {
                        if (postos.isEmpty()) {
                            showStatus(getString(R.string.stations_empty));
                        } else {
                            adapter.setPostos(postos);
                            // Update map button with real location
                            findViewById(R.id.stations_map_button).setOnClickListener(v -> openMap(userLocation));
                            showList();
                        }
                    });
                })
                .addOnFailureListener(e ->
                        runOnUiThread(() -> showStatus(getString(R.string.stations_error_firestore)))
                );
    }

    private void openMap(Location location) {
        String query = "posto+de+combustivel";
        String uri;
        if (location != null) {
            uri = "geo:" + location.getLatitude() + "," + location.getLongitude() + "?q=" + query;
        } else {
            uri = "geo:0,0?q=" + query;
        }
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        } catch (Exception ignored) {}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_LOCATION) return;

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadStations();
        } else {
            showStatus(getString(R.string.stations_permission_denied));
        }
    }

    private void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);
    }

    private void showList() {
        loadingView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        statusText.setVisibility(View.GONE);
    }

    private void showStatus(String message) {
        loadingView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        statusText.setText(message);
        statusText.setVisibility(View.VISIBLE);
    }
}
