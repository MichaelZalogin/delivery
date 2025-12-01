package microarch.delivery.core.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import static org.junit.jupiter.api.Assertions.*;

class IdTest {

    @ParameterizedTest
    @DisplayName("Should create Id for valid positive values")
    @ValueSource(longs = {1, 5, 10, Long.MAX_VALUE})
    void testCreateValidId(long value) {
        Id id = Id.of(value);

        assertEquals(value, id.id());
    }

    @ParameterizedTest
    @DisplayName("Should throw exception when creating Id with non-positive value")
    @ValueSource(longs = {0, -1, -5, -100})
    void testInvalidId(long invalidValue) {
        assertThrows(IllegalArgumentException.class,
                () -> Id.of(invalidValue));
    }

    @ParameterizedTest
    @DisplayName("Should create Id from valid String")
    @ValueSource(strings = {"1", "42", "999999", "123456789"})
    void testCreateValidIdFromString(String str) {
        Id id = Id.of(str);

        assertEquals(Long.parseLong(str), id.id());
    }

    @ParameterizedTest
    @DisplayName("Should throw exception for invalid numeric strings")
    @ValueSource(strings = {"0", "-1", "-50"})
    void testInvalidStringIdValue(String str) {
        assertThrows(IllegalArgumentException.class,
                () -> Id.of(str));
    }

    @ParameterizedTest
    @DisplayName("Should throw NumberFormatException for invalid non-numeric strings")
    @ValueSource(strings = {"abc", "12a", "", " ", "!", "123 456"})
    void testInvalidStringFormat(String str) {
        assertThrows(NumberFormatException.class,
                () -> Id.of(str));
    }

    @Test
    @DisplayName("generate() must create a valid positive Id")
    void testGenerate() {
        Id id = Id.generate();

        assertNotNull(id);
        assertTrue(id.id() > 0);
    }

    @Test
    @DisplayName("Two Id objects with same value must be equal")
    void testEquality() {
        Id a = Id.of(10);
        Id b = Id.of(10);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("Two Id objects with different values must not be equal")
    void testInequality() {
        Id a = Id.of(10);
        Id b = Id.of(20);

        assertNotEquals(a, b);
    }
}