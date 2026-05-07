package igorluciano.com.br.combustivelflex;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;

public class InAppUpdateHelper {
    public static final int UPDATE_REQUEST_CODE = 1001;

    private static final String PACKAGE_NAME = "igorluciano.com.br.combustivelflex";
    private static final String PLAY_STORE_URI = "market://details?id=" + PACKAGE_NAME;
    private static final String PLAY_STORE_WEB_URL =
            "https://play.google.com/store/apps/details?id=" + PACKAGE_NAME;

    private final Activity activity;
    private final AppUpdateManager appUpdateManager;
    private final InstallStateUpdatedListener installStateUpdatedListener;

    public InAppUpdateHelper(Activity activity) {
        this.activity = activity;
        appUpdateManager = AppUpdateManagerFactory.create(activity);
        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showCompleteUpdateDialog();
            } else if (state.installStatus() == InstallStatus.FAILED) {
                showPlayStoreFallbackDialog();
            }
        };
        appUpdateManager.registerListener(installStateUpdatedListener);
    }

    public void checkForUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE) {
                return;
            }

            if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                startUpdate(appUpdateInfo, AppUpdateType.FLEXIBLE);
            } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE);
            }
        });
    }

    public void resumeUpdateIfNeeded() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showCompleteUpdateDialog();
            } else if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE);
            }
        });
    }

    public void handleActivityResult(int requestCode, int resultCode) {
        if (requestCode != UPDATE_REQUEST_CODE) {
            return;
        }

        if (resultCode == com.google.android.play.core.install.model.ActivityResult
                .RESULT_IN_APP_UPDATE_FAILED) {
            showPlayStoreFallbackDialog();
        }
    }

    public void destroy() {
        appUpdateManager.unregisterListener(installStateUpdatedListener);
    }

    private void startUpdate(AppUpdateInfo appUpdateInfo, int appUpdateType) {
        try {
            boolean started = appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    appUpdateType,
                    activity,
                    UPDATE_REQUEST_CODE
            );
            if (!started) {
                showPlayStoreFallbackDialog();
            }
        } catch (Exception exception) {
            showPlayStoreFallbackDialog();
        }
    }

    private void showCompleteUpdateDialog() {
        if (activity.isFinishing()) {
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle(R.string.app_update_ready_title)
                .setMessage(R.string.app_update_ready_message)
                .setPositiveButton(R.string.app_update_restart_now,
                        (dialog, which) -> appUpdateManager.completeUpdate())
                .setNegativeButton(R.string.app_update_later, null)
                .show();
    }

    private void showPlayStoreFallbackDialog() {
        if (activity.isFinishing()) {
            return;
        }

        new AlertDialog.Builder(activity)
                .setTitle(R.string.app_update_fallback_title)
                .setMessage(R.string.app_update_fallback_message)
                .setPositiveButton(R.string.app_update_open_play_store,
                        (dialog, which) -> openPlayStore())
                .setNegativeButton(R.string.app_update_later, null)
                .show();
    }

    private void openPlayStore() {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_URI)));
        } catch (ActivityNotFoundException exception) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(PLAY_STORE_WEB_URL)));
        }
    }
}
