package net.apjanke.log4j1gui.internal;

import javax.swing.*;
import javax.swing.border.Border;

import static net.apjanke.log4j1gui.internal.Utils.px;

public class SwingUtils {

    /**
     * Default height for text fields that are a single line. This is an unscaled value;
     * callers should pass it through px() for HiDPI scaling.
     */
    public static final int singleRowTextFieldHeight = 15;

    public static Border createEmptyBorderPx(int top, int left, int bottom, int right) {
        //noinspection SuspiciousNameCombination
        return BorderFactory.createEmptyBorder(px(top), px(left), px(bottom), px(right));
    }

    public static Border createEmptyBorderPx(int margin) {
        return BorderFactory.createEmptyBorder(px(margin), px(margin), px(margin), px(margin));
    }

    public static class IntegerInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            String str = ((JTextField)input).getText();
            try {
                //noinspection ResultOfMethodCallIgnored
                Integer.parseInt(str);
                return true;
            } catch (NumberFormatException nfe) {
                return false;
            }
        }
    }

    private static class DoubleInputVerifier extends InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            String str = ((JTextField)input).getText();
            try {
                //noinspection ResultOfMethodCallIgnored
                Double.parseDouble(str);
                return true;
            } catch (NumberFormatException nfe) {
                return false;
            }
        }
    }
}
