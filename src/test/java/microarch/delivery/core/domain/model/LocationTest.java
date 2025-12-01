package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    @ParameterizedTest
    @DisplayName("Should create location for valid coordinates")
    @CsvSource({
            "2, 3",
            "5, 7",
            "9, 1",
            "3, 9"
    })
    void testCreateValidLocation(int x, int y) {
        Location loc = Location.create(x, y);

        assertEquals(x, loc.x());
        assertEquals(y, loc.y());
    }

    @ParameterizedTest
    @DisplayName("Should throw exception when X is out of range")
    @ValueSource(ints = {0, -1, 11, 100})
    void testInvalidX(int invalidX) {
        assertThrows(IllegalArgumentException.class,
                () -> Location.create(invalidX, 5));
    }

    @ParameterizedTest
    @DisplayName("Should throw exception when Y is out of range")
    @ValueSource(ints = {0, -5, 11, 100})
    void testInvalidY(int invalidY) {
        assertThrows(IllegalArgumentException.class,
                () -> Location.create(5, invalidY));
    }

    @ParameterizedTest
    @DisplayName("Locations with same coordinates must be equal")
    @CsvSource({
            "3, 4",
            "2, 2",
            "9, 5"
    })
    void testEquality(int x, int y) {
        Location a = Location.create(x, y);
        Location b = Location.create(x, y);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @ParameterizedTest
    @DisplayName("Locations with different coordinates must not be equal")
    @CsvSource({
            "3, 4, 4, 3",
            "2, 7, 2, 8",
            "8, 1, 9, 1"
    })
    void testInequality(int x1, int y1, int x2, int y2) {
        Location a = Location.create(x1, y1);
        Location b = Location.create(x2, y2);

        assertNotEquals(a, b);
    }

    @ParameterizedTest
    @DisplayName("compareTo compares X first, then Y")
    @MethodSource("compareToProvider")
    void testCompareTo(int x1, int y1, int x2, int y2) {
        Location a = Location.create(x1, y1);
        Location b = Location.create(x2, y2);

        assertEquals(a, b);
    }

    static Stream<Arguments> compareToProvider() {
        return Stream.of(
                Arguments.of(3, 4, 3, 4),
                Arguments.of(4, 5, 4, 5),
                Arguments.of(3, 5, 3, 5)
        );
    }

    @ParameterizedTest
    @DisplayName("distanceTo should calculate distance correctly")
    @CsvSource({
            "2, 3, 4, 6, 5",
            "5, 5, 5, 5, 0",
            "1, 1, 2, 4, 4",
            "3, 7, 9, 2, 11"
    })
    void testDistanceTo(int ax, int ay, int bx, int by, int expected) {
        Location a = Location.create(ax, ay);
        Location b = Location.create(bx, by);

        assertEquals(expected, a.distanceTo(b));
    }
}