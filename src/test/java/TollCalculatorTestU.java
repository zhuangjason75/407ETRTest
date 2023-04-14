import org.example.TollCalculator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class TollCalculatorTestU {

    private static final String TEST_FILE_NAME = "test.json";

    private static final double TOLL_RATE = 2.0d;
    private TollCalculator tollCalculator;

    @Before
    public void setup() throws URISyntaxException, IOException {
        tollCalculator = new TollCalculator();
        tollCalculator.initialize(TEST_FILE_NAME);
    }

    @Test
    public void testGetLocationId() {
        assertEquals(tollCalculator.getLocatonId("lot 4"), 4);
        assertEquals(tollCalculator.getLocatonId("Lot 3"), 3);
        assertThrows(IllegalArgumentException.class, () -> tollCalculator.getLocatonId("lot 5"));
    }

    @Test
    public void testGetTollCost() {
        double result[] = tollCalculator.getTollCost(1, 2, TOLL_RATE);
        assertEquals(result[0], 1.5d, 0.001);
        assertEquals(result[1], 3.0d, 0.001);

        result = tollCalculator.getTollCost(1, 3, TOLL_RATE);
        assertEquals(result[0], 4.0d, 0.001);
        assertEquals(result[1], 8.0d, 0.001);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> tollCalculator.getTollCost(1, 6, TOLL_RATE));
        assertTrue(e.getMessage().contains("Cannot exist"));

        e = assertThrows(IllegalArgumentException.class, () -> tollCalculator.getTollCost(4, 6, TOLL_RATE));
        assertTrue(e.getMessage().contains("Cannot exist"));

        e = assertThrows(IllegalArgumentException.class, () -> tollCalculator.getTollCost(3, 2, TOLL_RATE));
        assertTrue(e.getMessage().contains("Cannot enter"));

        result = tollCalculator.getTollCost(6, 4, TOLL_RATE);
        assertEquals(result[0], 3.5d, 0.001);
        assertEquals(result[1], 7.0d, 0.001);

    }
}
