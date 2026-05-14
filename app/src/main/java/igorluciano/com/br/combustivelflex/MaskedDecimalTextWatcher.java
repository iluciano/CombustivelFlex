package igorluciano.com.br.combustivelflex;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

// Entrada da direita para esquerda: digitar "599" exibe "5.99", não "5.99".
// Cada dígito novo entra pela direita e desloca os demais para a esquerda.
public final class MaskedDecimalTextWatcher implements TextWatcher {
    private static final int MAX_DIGITS = 4;
    private static final int DECIMAL_PLACES = 2;

    private final EditText input;
    private boolean updating;

    public MaskedDecimalTextWatcher(EditText input) {
        this.input = input;
    }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (updating) {
            return;
        }

        String original = editable.toString();
        String formatted = formatInput(original);
        if (original.equals(formatted)) {
            return;
        }

        updateText(formatted);
    }

    public void padDecimals() {
        // Com a nova máscara sempre há 2 casas decimais — método mantido por compatibilidade.
    }

    private void updateText(String value) {
        updating = true;
        input.setText(value);
        input.setSelection(value.length());
        updating = false;
    }

    private String formatInput(String value) {
        String digits = collectDigits(value);

        if (digits.isEmpty() || isAllZeros(digits)) {
            return "";
        }

        // Garante mínimo de DECIMAL_PLACES + 1 dígitos com zeros à esquerda
        while (digits.length() < DECIMAL_PLACES + 1) {
            digits = "0" + digits;
        }

        String integerPart = digits.substring(0, digits.length() - DECIMAL_PLACES);
        String decimalPart = digits.substring(digits.length() - DECIMAL_PLACES);

        // Remove zeros desnecessários à esquerda da parte inteira
        while (integerPart.length() > 1 && integerPart.charAt(0) == '0') {
            integerPart = integerPart.substring(1);
        }

        return integerPart + "." + decimalPart;
    }

    private boolean isAllZeros(String digits) {
        for (int i = 0; i < digits.length(); i++) {
            if (digits.charAt(i) != '0') {
                return false;
            }
        }
        return true;
    }

    private String collectDigits(String value) {
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            if (Character.isDigit(value.charAt(i))) {
                digits.append(value.charAt(i));
            }
        }
        // Mantém apenas os últimos MAX_DIGITS dígitos (entrada da direita para esquerda)
        if (digits.length() > MAX_DIGITS) {
            digits.delete(0, digits.length() - MAX_DIGITS);
        }
        return digits.toString();
    }
}
