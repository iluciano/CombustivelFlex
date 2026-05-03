package igorluciano.com.br.combustivelflex;

import android.content.Context;
import android.content.SharedPreferences;

public final class NewSettingsStore {
    public static final String UNIT_LITER = "liter";
    public static final String UNIT_KM = "km";

    private static final String PREFERENCES_NAME = "newPrototypeSettings";
    private static final String KEY_UNIT = "unit";
    private static final String KEY_GASOLINE_CONSUMPTION = "gasolineConsumption";
    private static final String KEY_ETHANOL_CONSUMPTION = "ethanolConsumption";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_PRICE_REMINDER = "priceReminder";

    private NewSettingsStore() {
    }

    public static String getUnit(Context context) {
        return preferences(context).getString(KEY_UNIT, UNIT_LITER);
    }

    public static void setUnit(Context context, String unit) {
        preferences(context).edit().putString(KEY_UNIT, unit).apply();
    }

    public static double getGasolineConsumption(Context context) {
        return preferences(context).getFloat(KEY_GASOLINE_CONSUMPTION, 0);
    }

    public static void setGasolineConsumption(Context context, double value) {
        preferences(context).edit().putFloat(KEY_GASOLINE_CONSUMPTION, (float) value).apply();
    }

    public static double getEthanolConsumption(Context context) {
        return preferences(context).getFloat(KEY_ETHANOL_CONSUMPTION, 0);
    }

    public static void setEthanolConsumption(Context context, double value) {
        preferences(context).edit().putFloat(KEY_ETHANOL_CONSUMPTION, (float) value).apply();
    }

    public static boolean isNotificationsEnabled(Context context) {
        return preferences(context).getBoolean(KEY_NOTIFICATIONS, false);
    }

    public static void setNotificationsEnabled(Context context, boolean enabled) {
        preferences(context).edit().putBoolean(KEY_NOTIFICATIONS, enabled).apply();
    }

    public static boolean isPriceReminderEnabled(Context context) {
        return preferences(context).getBoolean(KEY_PRICE_REMINDER, false);
    }

    public static void setPriceReminderEnabled(Context context, boolean enabled) {
        preferences(context).edit().putBoolean(KEY_PRICE_REMINDER, enabled).apply();
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
