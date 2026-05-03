package igorluciano.com.br.combustivelflex;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
}
