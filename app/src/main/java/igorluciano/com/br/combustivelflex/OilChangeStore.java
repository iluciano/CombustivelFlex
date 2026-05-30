package igorluciano.com.br.combustivelflex;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OilChangeStore {
    private static final String PREFS = "oil_change";
    private static final String KEY_LATEST = "latest";
    private static final String KEY_HISTORY = "history";
    private static final int MAX_HISTORY = 25;

    public static OilChangeRecord getLatest(Context ctx) {
        String json = prefs(ctx).getString(KEY_LATEST, null);
        if (json == null) return null;
        return deserialize(json);
    }

    public static List<OilChangeRecord> getHistory(Context ctx) {
        List<OilChangeRecord> list = new ArrayList<>();
        String json = prefs(ctx).getString(KEY_HISTORY, null);
        if (json == null) return list;
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                OilChangeRecord r = deserialize(arr.getString(i));
                if (r != null) list.add(r);
            }
        } catch (Exception ignored) {}
        return list;
    }

    public static void save(Context ctx, OilChangeRecord record) {
        record.timestamp = System.currentTimeMillis();
        String json = serialize(record);

        List<OilChangeRecord> history = getHistory(ctx);
        history.add(0, record);
        if (history.size() > MAX_HISTORY) {
            history = history.subList(0, MAX_HISTORY);
        }
        JSONArray arr = new JSONArray();
        for (OilChangeRecord r : history) arr.put(serialize(r));

        prefs(ctx).edit()
                .putString(KEY_LATEST, json)
                .putString(KEY_HISTORY, arr.toString())
                .apply();
    }

    public static void delete(Context ctx, long timestamp) {
        List<OilChangeRecord> history = getHistory(ctx);
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i).timestamp == timestamp) {
                history.remove(i);
                break;
            }
        }
        persistHistory(ctx, history);
    }

    public static void update(Context ctx, OilChangeRecord updated) {
        List<OilChangeRecord> history = getHistory(ctx);
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).timestamp == updated.timestamp) {
                history.set(i, updated);
                break;
            }
        }
        persistHistory(ctx, history);
    }

    private static void persistHistory(Context ctx, List<OilChangeRecord> history) {
        JSONArray arr = new JSONArray();
        for (OilChangeRecord r : history) arr.put(serialize(r));
        SharedPreferences.Editor editor = prefs(ctx).edit()
                .putString(KEY_HISTORY, arr.toString());
        if (!history.isEmpty()) {
            editor.putString(KEY_LATEST, serialize(history.get(0)));
        } else {
            editor.remove(KEY_LATEST);
        }
        editor.apply();
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    static String serialize(OilChangeRecord r) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("timestamp", r.timestamp);
            obj.put("date", r.date != null ? r.date : "");
            obj.put("km", r.km);
            obj.put("nextKm", r.nextKm);
            obj.put("nextDate", r.nextDate != null ? r.nextDate : "");
            obj.put("engine_oil", r.changedEngineOil);
            obj.put("oil_filter", r.changedOilFilter);
            obj.put("air_filter", r.changedAirFilter);
            obj.put("fuel_filter", r.changedFuelFilter);
            obj.put("cabin_filter", r.changedCabinFilter);
            obj.put("brake_fluid", r.changedBrakeFluid);
            obj.put("spark_plugs", r.changedSparkPlugs);
            obj.put("oil_type", r.oilType != null ? r.oilType : "");
            obj.put("notes", r.notes != null ? r.notes : "");
            return obj.toString();
        } catch (Exception e) {
            return "{}";
        }
    }

    static OilChangeRecord deserialize(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            OilChangeRecord r = new OilChangeRecord();
            r.timestamp = obj.optLong("timestamp");
            r.date = obj.optString("date");
            r.km = obj.optLong("km");
            r.nextKm = obj.optLong("nextKm");
            r.nextDate = obj.optString("nextDate");

            // Backward compatibility: if nextKm/nextDate absent, derive from old fields
            if (r.nextKm == 0 && r.km > 0) {
                r.nextKm = r.km + obj.optInt("interval_km", 5000);
            }
            if ((r.nextDate == null || r.nextDate.isEmpty()) && r.date != null && !r.date.isEmpty()) {
                r.nextDate = addMonths(r.date, obj.optInt("interval_months", 6));
            }

            r.changedEngineOil = obj.optBoolean("engine_oil");
            r.changedOilFilter = obj.optBoolean("oil_filter");
            r.changedAirFilter = obj.optBoolean("air_filter");
            r.changedFuelFilter = obj.optBoolean("fuel_filter");
            r.changedCabinFilter = obj.optBoolean("cabin_filter");
            r.changedBrakeFluid = obj.optBoolean("brake_fluid");
            r.changedSparkPlugs = obj.optBoolean("spark_plugs");
            r.oilType = obj.optString("oil_type");
            r.notes = obj.optString("notes");
            return r;
        } catch (Exception e) {
            return null;
        }
    }

    private static String addMonths(String date, int months) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
            Calendar cal = Calendar.getInstance();
            cal.setTime(sdf.parse(date));
            cal.add(Calendar.MONTH, months);
            return sdf.format(cal.getTime());
        } catch (Exception e) {
            return date;
        }
    }
}
