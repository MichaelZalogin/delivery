package microarch.delivery.core.domain.model.courier;

import microarch.delivery.core.domain.model.Id;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.order.Order;
import microarch.delivery.core.domain.model.order.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class CourierTest {

    private Location start;
    private Courier courier;

    @BeforeEach
    void setup() {
        start = Location.create(1, 1);
        courier = Courier.create("Michael", 3, start);
    }

    @Test
    void shouldCreateCourier() {
        assertEquals("Michael", courier.getName());
        assertEquals(3, courier.getSpeed());
        assertEquals(start, courier.getLocation());
        assertEquals(1, courier.getStoragePlaces().size());
    }

    @Test
    void shouldNocreateCourier_when_invalidName() {
        assertThrows(IllegalArgumentException.class,
                () -> Courier.create("", 3, start));
    }

    @Test
    void shouldNocreateCourier_when_invalidSpeed() {
        assertThrows(IllegalArgumentException.class,
                () -> Courier.create("Michael", 0, start));
    }

    @Test
    void shouldNocreateCourier_when_invalidLocation() {
        assertThrows(IllegalArgumentException.class,
                () -> Courier.create("Michael", 2, null));
    }

    @Test
    void shouldMoveUpToSpeedTowardTarget() {
        courier.move(Location.create(10, 10));

        Location newLoc = courier.getLocation();

        assertTrue(Math.abs(newLoc.x() - 1) + Math.abs(newLoc.y() - 1) <= 3);

        assertTrue(newLoc.x() > 0);
        assertTrue(newLoc.y() > 0);
    }

    @Test
    void shouldReachTargetIfCloserThanSpeed() {
        courier.move(Location.create(2, 1));

        assertEquals(2, courier.getLocation().x());
        assertEquals(1, courier.getLocation().y());
    }

    @Test
    void move_onlyX_ifYAligned() {
        courier.move(Location.create(5, 1));

        assertEquals(4, courier.getLocation().x());
        assertEquals(1, courier.getLocation().y());
    }

    @Test
    void move_onlyY_ifXAligned() {
        courier.move(Location.create(1, 5));

        assertEquals(1, courier.getLocation().x());
        assertEquals(4, courier.getLocation().y());
    }

    @Test
    void move_negativeDirection() {
        courier = Courier.create("Michael", 3, Location.create(5, 5));

        courier.move(Location.create(3, 3));

        assertEquals(2, Math.abs(courier.getLocation().x() - 5));
        assertEquals(1, Math.abs(courier.getLocation().y() - 5));
    }

    @Test
    void shouldNotMove_when_nullTarget() {
        assertThrows(IllegalArgumentException.class, () -> courier.move(null));
    }

    @Test
    void shouldCalculateDeliveryTime() {
        double time = courier.calculateDeliveryTime(Location.create(6, 2));

        assertEquals(2.0, time);
    }

    @Test
    void shouldNotCalculateDeliveryTime_when_TargetLocationIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> courier.calculateDeliveryTime(null));
    }

    @Test
    void shouldCanTakeOrder() {
        Order order = Order.create(Id.generate(), start, 2);
        assertTrue(courier.canTakeOrder(order));
    }

    @Test
    void shouldNotCanTakeOrder_when_VolumeIsBig() {
        Order order = Order.create(Id.generate(), start, 9999);
        assertFalse(courier.canTakeOrder(order));
    }

    @Test
    void shouldTakeOrder() {
        Order order = Order.create(Id.generate(), start, 2);

        courier.takeOrder(order);

        StoragePlace place = courier.getStoragePlaces().get(0);
        assertEquals(order.getId(), place.getOrderId());
    }

    @Test
    void shouldCompleteOrder() {
        Order order = Order.create(Id.generate(), start, 2);
        order.assign(courier.getId());
        courier.takeOrder(order);

        courier.completeOrder(order);

        StoragePlace place = courier.getStoragePlaces().get(0);
        assertNull(place.getOrderId());
    }

    @Test
    void shouldTerminateOrder() {
        Order order = Order.create(Id.generate(), start, 2);
        order.assign(courier.getId());
        courier.takeOrder(order);

        courier.terminateOrder(order);

        StoragePlace place = courier.getStoragePlaces().get(0);
        assertNull(place.getOrderId());
    }

    @Test
    void shouldNotCompleteOrder_when_WrongCourier() {
        Order order = Order.create(Id.generate(), start, 2);
        Courier other = Courier.create("Other", 3, start);

        other.takeOrder(order);

        assertThrows(IllegalArgumentException.class, () -> courier.completeOrder(order));
    }
}