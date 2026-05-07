package igorluciano.com.br.combustivelflex;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

final class CalculationHistoryStore {
    private static final String PREFERENCES_NAME = "calculation_history";
    private static final String KEY_ITEMS = "items";
    private static final int MAX_ITEMS = 25;

    private CalculationHistoryStore() {
    }

    static void save(Context context, CalculationHistoryItem item) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES_NAME,
                Context.MODE_PRIVATE
        );
        JSONArray existingItems = parseItems(preferences.getString(KEY_ITEMS, "[]"));
        JSONArray updatedItems = new JSONArray();

        updatedItems.put(toJson(item));
        int itemsToCopy = Math.min(existingItems.length(), MAX_ITEMS - 1);
        for (int index = 0; index < itemsToCopy; index++) {
            JSONObject existingItem = existingItems.optJSONObject(index);
            if (existingItem != null) {
                updatedItems.put(existingItem);
            }
        }

        preferences.edit().putString(KEY_ITEMS, updatedItems.toString()).apply();
    }

    static List<CalculationHistoryItem> list(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES_NAME,
                Context.MODE_PRIVATE
        );
        JSONArray items = parseItems(preferences.getString(KEY_ITEMS, "[]"));
        List<CalculationHistoryItem> historyItems = new ArrayList<>();

        for (int index = 0; index < items.length(); index++) {
            JSONObject json = items.optJSONObject(index);
            if (json != null) {
                historyItems.add(fromJson(json));
            }
        }

        return historyItems;
    }

    static void clear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES_NAME,
                Context.MODE_PRIVATE
        );
        preferences.edit().remove(KEY_ITEMS).apply();
    }

    private static JSONArray parseItems(String value) {
        try {
            return new JSONArray(value);
        } catch (JSONException exception) {
            return new JSONArray();
        }
    }

    private static JSONObject toJson(CalculationHistoryItem item) {
        JSONObject json = new JSONObject();
        try {
            json.put("createdAt", item.createdAt);
            json.put("gasoline", item.gasoline);
            json.put("ethanol", item.ethanol);
            json.put("gasolineConsumption", item.gasolineConsumption);
            json.put("ethanolConsumption", item.ethanolConsumption);
            json.put("result", item.result);
            json.put("savings", item.savings);
            json.put("usedConsumption", item.usedConsumption);
        } catch (JSONException ignored) {
        }

        return json;
    }

    private static CalculationHistoryItem fromJson(JSONObject json) {
        return new CalculationHistoryItem(
                json.optLong("createdAt"),
                json.optDouble("gasoline"),
                json.optDouble("ethanol"),
                json.optDouble("gasolineConsumption"),
                json.optDouble("ethanolConsumption"),
                json.optString("result"),
                json.optDouble("savings"),
                json.optBoolean("usedConsumption")
        );
    }
}
