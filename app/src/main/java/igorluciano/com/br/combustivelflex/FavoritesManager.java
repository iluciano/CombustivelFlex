package igorluciano.com.br.combustivelflex;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesManager {

    private static final String PREFS = "favorites";
    private static final String KEY_IDS = "favorite_ids";

    public static boolean isFavorite(Context ctx, String postoId) {
        return getIds(ctx).contains(postoId);
    }

    public static void addFavorite(Context ctx, Posto posto) {
        SharedPreferences prefs = prefs(ctx);
        Set<String> ids = new HashSet<>(getIds(ctx));
        ids.add(posto.getId());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(KEY_IDS, ids);
        String json = serialize(posto);
        if (json != null) editor.putString(postoKey(posto.getId()), json);
        editor.apply();
    }

    public static void removeFavorite(Context ctx, String postoId) {
        SharedPreferences prefs = prefs(ctx);
        Set<String> ids = new HashSet<>(getIds(ctx));
        ids.remove(postoId);
        prefs.edit()
                .putStringSet(KEY_IDS, ids)
                .remove(postoKey(postoId))
                .apply();
    }

    public static List<Posto> getFavorites(Context ctx) {
        SharedPreferences prefs = prefs(ctx);
        List<Posto> result = new ArrayList<>();
        for (String id : getIds(ctx)) {
            String json = prefs.getString(postoKey(id), null);
            if (json == null) continue;
            Posto posto = deserialize(json);
            if (posto != null) result.add(posto);
        }
        return result;
    }

    private static Set<String> getIds(Context ctx) {
        return new HashSet<>(prefs(ctx).getStringSet(KEY_IDS, new HashSet<>()));
    }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static String postoKey(String id) {
        return "posto_" + id;
    }

    private static String serialize(Posto p) {
        try {
            JSONObject j = new JSONObject();
            j.put("id",               p.getId() != null ? p.getId() : "");
            j.put("nome",             p.getNome() != null ? p.getNome() : "");
            j.put("bandeira",         p.getBandeira() != null ? p.getBandeira() : "");
            j.put("latitude",         p.getLatitude());
            j.put("longitude",        p.getLongitude());
            j.put("precoGasolina",    p.getPrecoGasolinaComum());
            j.put("precoEtanol",      p.getPrecoEtanol());
            j.put("distanciaMetros",  p.getDistanciaMetros());
            j.put("rua",              p.getRua() != null ? p.getRua() : "");
            j.put("numero",           p.getNumero() != null ? p.getNumero() : "");
            j.put("bairro",           p.getBairro() != null ? p.getBairro() : "");
            j.put("cidade",           p.getCidade() != null ? p.getCidade() : "");
            j.put("estado",           p.getEstado() != null ? p.getEstado() : "");
            j.put("dataUltimaColeta", p.getDataUltimaColeta() != null ? p.getDataUltimaColeta() : "");
            return j.toString();
        } catch (JSONException e) {
            return null;
        }
    }

    private static Posto deserialize(String json) {
        try {
            JSONObject j = new JSONObject(json);
            Posto p = new Posto();
            p.setId(j.optString("id"));
            p.setNome(j.optString("nome"));
            p.setBandeira(j.optString("bandeira"));
            p.setLatitude(j.optDouble("latitude", 0));
            p.setLongitude(j.optDouble("longitude", 0));
            p.setPrecoGasolinaComum(j.optDouble("precoGasolina", 0));
            p.setPrecoEtanol(j.optDouble("precoEtanol", 0));
            p.setDistanciaMetros((float) j.optDouble("distanciaMetros", 0));
            p.setRua(j.optString("rua"));
            p.setNumero(j.optString("numero"));
            p.setBairro(j.optString("bairro"));
            p.setCidade(j.optString("cidade"));
            p.setEstado(j.optString("estado"));
            p.setDataUltimaColeta(j.optString("dataUltimaColeta"));
            return p;
        } catch (JSONException e) {
            return null;
        }
    }
}
