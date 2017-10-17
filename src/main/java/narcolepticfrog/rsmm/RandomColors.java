package narcolepticfrog.rsmm;

import java.util.Random;

public class RandomColors {

    private static Random rand = new Random();

    /**
     * Generates a random color, excluding colors that are too dark.
     */
    public static int randomColor() {
        int color = 0xFF000000;
        color |= (rand.nextInt(150) + 106);
        color |= (rand.nextInt(150) + 106) << 8;
        color |= (rand.nextInt(150) + 106) << 16;
        return color;
    }

}
