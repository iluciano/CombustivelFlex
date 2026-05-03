package igorluciano.com.br.combustivelflex;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

public final class MaskedDecimalTextWatcher implements TextWatcher {
    private static final int MAX_DIGITS = 4;
    private static final int MAX_INTEGER_DIGITS = 2;
    private static final int MIN_DECIMAL_DIGITS = 2;

    private final EditText input;
    private boolean updating;
    private boolean deletingDecimalPoint;

    public MaskedDecimalTextWatcher(EditText input) {
        this.input = input;
    }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        deletingDecimalPoint = count == 1
                && after == 0
                && start < text.length()
                && text.charAt(start) == '.';
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (updating) {
            return;
        }

        if (deletingDecimalPoint) {
            deletingDecimalPoint = false;
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
        if (updating) {
            return;
        }

        String original = input.getText().toString();
        if (TextUtils.isEmpty(original)) {
            return;
        }

        String formatted = padDecimalPart(formatInput(original));
        if (!original.equals(formatted)) {
            updateText(formatted);
        }
    }

    private void updateText(String value) {
        updating = true;
        input.setText(value);
        input.setSelection(value.length());
        updating = false;
    }

    private String formatInput(String value) {
        String digits = collectDigits(value);
        if (digits.isEmpty()) {
            return "";
        }

        int integerDigits = digits.length() < MAX_DIGITS
                ? 1
                : MAX_INTEGER_DIGITS;
        String integerPart = digits.substring(0, integerDigits);
        String decimalPart = digits.substring(integerDigits);
        return integerPart + "." + decimalPart;
    }

    private String padDecimalPart(String value) {
        int pointIndex = value.indexOf('.');
        if (pointIndex < 0) {
            return value + ".00";
        }

        String integerPart = value.substring(0, pointIndex);
        String decimalPart = value.substring(pointIndex + 1);
        while (decimalPart.length() < MIN_DECIMAL_DIGITS) {
            decimalPart += "0";
        }

        return integerPart + "." + decimalPart;
    }

    private String collectDigits(String value) {
        StringBuilder digits = new StringBuilder();

        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            if (Character.isDigit(character) && digits.length() < MAX_DIGITS) {
                digits.append(character);
            }
        }

        return digits.toString();
    }
}
