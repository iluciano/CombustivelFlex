package igorluciano.com.br.combustivelflex;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class KmTextWatcher implements TextWatcher {

    private static final long MAX_KM = 9_999_999L;
    private final EditText editText;
    private boolean formatting;

    public KmTextWatcher(EditText editText) {
        this.editText = editText;
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if (formatting) return;
        formatting = true;
        String digits = s.toString().replaceAll("[^0-9]", "");
        if (!digits.isEmpty()) {
            try {
                long value = Long.parseLong(digits);
                if (value > MAX_KM) value = MAX_KM;
                DecimalFormat fmt = new DecimalFormat(
                        "#,###",
                        DecimalFormatSymbols.getInstance(new Locale("pt", "BR"))
                );
                s.replace(0, s.length(), fmt.format(value));
            } catch (NumberFormatException e) {
                s.clear();
            }
        }
        formatting = false;
    }

    public static long parseKm(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        try {
            String digits = text.replaceAll("[^0-9]", "");
            return digits.isEmpty() ? 0 : Long.parseLong(digits);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
