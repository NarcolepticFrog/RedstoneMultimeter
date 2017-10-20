package narcolepticfrog.rsmm;

import java.awt.*;
import java.util.Random;

public class RandomColors {

    private static Random rand = new Random();

    /**
     * Generates a random color, excluding colors that are too dark.
     */
    public static int randomColor() {
        Color c = Color.getHSBColor(rand.nextFloat(),  rand.nextFloat()*0.3F+0.3F, rand.nextFloat()*0.6F + 0.4F);
        int color = 0xFF000000;
        color |= c.getBlue();
        color |= c.getGreen() << 8;
        color |= c.getRed() << 16;
        return color;
    }

}
