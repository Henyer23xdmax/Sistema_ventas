package Configuracion;

import javax.swing.text.*;

public class FiltroAlfanumerico extends DocumentFilter {
    private int maxLength;

    public FiltroAlfanumerico(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string.matches("[A-Za-z0-9]+") && (fb.getDocument().getLength() + string.length() <= maxLength)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (text.matches("[A-Za-z0-9]+") && (fb.getDocument().getLength() - length + text.length() <= maxLength)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
