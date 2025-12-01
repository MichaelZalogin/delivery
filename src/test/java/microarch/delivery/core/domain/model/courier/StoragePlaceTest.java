package microarch.delivery.core.domain.model.courier;

import microarch.delivery.core.domain.model.Id;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StoragePlaceTest {

    @Test
    void createStoragePlace_success() {
        StoragePlace place = StoragePlace.create(StoragePlaceType.BACKPACK, null);

        assertNotNull(place);
        assertNotNull(place.getId());
        assertEquals(StoragePlaceType.BACKPACK, place.getPlaceType());
        assertTrue(place.isEmpty());
    }

    @Test
    void isEmpty_shouldReturnTrueWhenOrderIdIsNull() {
        StoragePlace place = StoragePlace.create(StoragePlaceType.TRUNK, null);
        assertTrue(place.isEmpty());
    }

    @Test
    void isEmpty_shouldReturnFalseWhenOrderExists() {
        Id orderId = Id.generate();
        StoragePlace place = StoragePlace.create(StoragePlaceType.TRUNK, orderId);
        assertFalse(place.isEmpty());
    }

    @Test
    void canPut_shouldReturnTrueWhenEmptyAndVolumeFits() {
        StoragePlace place = StoragePlace.create(StoragePlaceType.BACKPACK, null);
        assertTrue(place.canPut(10));
    }

    @Test
    void canPut_shouldReturnFalseWhenPlaceIsNotEmpty() {
        StoragePlace place = StoragePlace.create(StoragePlaceType.BACKPACK, Id.generate());
        assertFalse(place.canPut(5));
    }

    @Test
    void canPut_shouldReturnFalseWhenVolumeTooLarge() {
        StoragePlace place = StoragePlace.create(StoragePlaceType.BACKPACK, null);
        assertFalse(place.canPut(100));
    }

    @Test
    void putOrder_successfullyPutsOrder() {
        StoragePlace place = StoragePlace.create(StoragePlaceType.BACKPACK, null);
        Id order = Id.generate();

        place.putOrder(order, 20);

        assertFalse(place.isEmpty());
        assertEquals(order, place.getOrderId());
    }

    @Test
    void putOrder_shouldFailWhenPlaceNotEmpty() {
        StoragePlace place = StoragePlace.create(StoragePlaceType.BACKPACK, Id.generate());
        Id newOrder = Id.generate();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> place.putOrder(newOrder, 15)
        );
        assertEquals("storage already contains order", ex.getMessage());
    }

    @Test
    void putOrder_shouldFailWhenVolumeTooLarge() {
        StoragePlace place = StoragePlace.create(StoragePlaceType.BACKPACK, null);
        Id order = Id.generate();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> place.putOrder(order, 100)
        );

        assertEquals("Order greater current capacity", ex.getMessage());
    }

    @Test
    void putOrder_shouldFailWhenOrderIdNull() {
        StoragePlace place = StoragePlace.create(StoragePlaceType.BACKPACK, null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> place.putOrder(null, 10)
        );

        assertEquals("orderId must be not null", ex.getMessage());
    }

    @Test
    void removeOrder_shouldClearOrderAndReturnId() {
        Id order = Id.generate();
        StoragePlace place = StoragePlace.create(StoragePlaceType.TRUNK, order);

        Id removed = place.removeOrder();

        assertEquals(order, removed);
        assertTrue(place.isEmpty());
    }

    @Test
    void removeOrder_fromEmptyPlaceReturnsNull() {
        StoragePlace place = StoragePlace.create(StoragePlaceType.TRUNK, null);

        Id removed = place.removeOrder();

        assertNull(removed);
        assertTrue(place.isEmpty());
    }

    @Test
    void equals_shouldReturnTrueForSameId() {
        StoragePlace place1 = StoragePlace.create(StoragePlaceType.BACKPACK, null);
        StoragePlace place2 = StoragePlaceBuilder.copy(place1)
                .placeType(StoragePlaceType.TRUNK)
                .build();

        assertEquals(place1, place2);
    }

    @Test
    void equals_shouldReturnFalseForDifferentId() {
        StoragePlace place1 = StoragePlace.create(StoragePlaceType.BACKPACK, null);
        StoragePlace place2 = StoragePlace.create(StoragePlaceType.BACKPACK, null);

        assertNotEquals(place1, place2);
    }
}