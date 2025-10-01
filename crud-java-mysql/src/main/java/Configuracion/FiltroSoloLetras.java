package Configuracion;

import javax.swing.text.*;

public class FiltroSoloLetras extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {
        if (string.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+") || string.isEmpty()) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {
        if (text.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+") || text.isEmpty()) {
            super.replace(fb, offset, length, text, attrs);
        }
    }
}
