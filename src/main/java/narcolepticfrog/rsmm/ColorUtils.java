package narcolepticfrog.rsmm;

import java.awt.*;
import java.util.Random;

public class ColorUtils {

    private static Random rand = new Random();

    /**
     * Generates a random color, excluding colors that are too dark.
     */
    public static int randomColor() {
        Color c = Color.getHSBColor(rand.nextFloat(),  rand.nextFloat()*0.3F+0.7F, rand.nextFloat()*0.3F + 0.7F);
        int color = 0xFF000000;
        color |= c.getBlue();
        color |= c.getGreen() << 8;
        color |= c.getRed() << 16;
        return color;
    }

    public static int parseColor(String str) {
        if (str.length() < 6) {
            return 0;
        }
        try {
            int r = Integer.valueOf(str.substring(str.length() - 6, str.length() - 4), 16);
            int g = Integer.valueOf(str.substring(str.length() - 4, str.length() - 2), 16);
            int b = Integer.valueOf(str.substring(str.length() - 2), 16);
            System.out.println(r + ", " + g + ", " + b);
            return 0xFF << 24 | (r << 16) | (g << 8) | b;
        } catch (Exception e) {
            return 0;
        }
    }

}
