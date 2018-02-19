package net.apjanke.log4j1gui.internal;

import javax.swing.*;
import javax.swing.border.Border;

import static net.apjanke.log4j1gui.internal.Utils.px;

public class SwingUtils {

    public static Border createEmptyBorderPx(int top, int left, int bottom, int right) {
        //noinspection SuspiciousNameCombination
        return BorderFactory.createEmptyBorder(px(top), px(left), px(bottom), px(right));
    }

    public static Border createEmptyBorderPx(int margin) {
        return BorderFactory.createEmptyBorder(px(margin), px(margin), px(margin), px(margin));
    }
}
