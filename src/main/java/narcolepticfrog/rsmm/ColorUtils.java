package narcolepticfrog.rsmm;

import java.awt.*;
import java.util.Random;

public class ColorUtils {

    private static Random rand = new Random();

    private static int[] fixedColors = {
            parseColor("#FFC312"),
            parseColor("#C4E538"),
            parseColor("#12CBC4"),
            parseColor("#FDA7DF"),
            parseColor("#ED4C67"),
            parseColor("#EE5A24"),
            parseColor("#009432"),
            parseColor("#0652DD"),
            parseColor("#9980FA"),
            parseColor("#833471")
    };
    private static int colorIndex = 0;

    /**
     * Generates a random color, excluding colors that are too dark.
     */
    public static int randomColor() {
        return hsb2int(rand.nextFloat(),  rand.nextFloat()*0.3F+0.7F, rand.nextFloat()*0.3F + 0.7F);
    }

    public static int hsb2int(float h, float s, float b) {
        Color c = Color.getHSBColor(h,s,b);
        int color = 0xFF000000;
        color |= c.getBlue();
        color |= c.getGreen() << 8;
        color |= c.getRed() << 16;
        return color;
    }

    public static int nextColor() {
        //int color = fixedColors[colorIndex];
        System.out.println("Color index = " + colorIndex);
        float hueIndex = (colorIndex*11) % 8;
        float satIndex = (float)Math.floor(colorIndex / 8.0);

        int color = hsb2int((hueIndex+satIndex/2f) / 8f, 0.7f, 1.0f);
        colorIndex = (colorIndex + 1) % 16;
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
            return 0xFF << 24 | (r << 16) | (g << 8) | b;
        } catch (Exception e) {
            return 0;
        }
    }

}
