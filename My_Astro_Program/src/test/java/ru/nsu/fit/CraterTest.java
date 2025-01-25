package ru.nsu.fit;

import org.junit.jupiter.api.Test;

class CraterTest {
    @Test
    void test() {
        String radius = "64";
//        Main.main(new String[]{"src/test/resources/original_images/crater1.tif", "src/test/resources/craters/crater_1/" + radius + "_quadru.tif", radius});
//        Main.main(new String[]{"src/test/resources/original_images/crater2.tif", "src/test/resources/craters/crater_2/" + radius + "_quadru.tif", radius});
        Main.main(new String[]{"src/test/resources/original_images/circle2.tif", "src/test/resources/craters/circle/circle2_" + radius + "_quadru.tif", radius});
    }
}
