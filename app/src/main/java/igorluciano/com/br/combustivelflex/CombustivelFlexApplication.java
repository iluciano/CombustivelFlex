package igorluciano.com.br.combustivelflex;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

public class CombustivelFlexApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(() -> MobileAds.initialize(this, initializationStatus -> {})).start();
    }
}
