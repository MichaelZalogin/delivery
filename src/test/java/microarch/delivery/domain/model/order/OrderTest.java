package microarch.delivery.domain.model.order;

import microarch.delivery.domain.model.Id;
import microarch.delivery.domain.model.Location;
import microarch.delivery.domain.model.order.Order;
import microarch.delivery.domain.model.order.OrderStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateSuccessOrder() {
        Location location = Location.create(10, 10);

        Order order = Order.create(location, 50);

        assertNotNull(order);
        assertEquals(location, order.getLocation());
        assertEquals(50, order.getVolume());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertNull(order.getCourierId());
    }

    @Test
    void shouldAssignOrder() {
        Order order = Order.create(Location.create(1, 2), 30);
        Id courierId = Id.generate();

        order.assign(courierId);

        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
        assertEquals(courierId, order.getCourierId());
    }

    @Test
    void shouldNotAssignOrder_when_OrderAlreadyAssignedAnotherCourier() {
        Order order = Order.create(Location.create(1, 2), 30);
        order.assign(Id.generate());

        Id anotherCourier = Id.generate();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> order.assign(anotherCourier)
        );

        assertEquals("cannot assign order in progress or completed", ex.getMessage());
    }

    @Test
    void shouldNotAssignOrder_when_OrderAlreadyCompleted() {
        Order order = Order.create(Location.create(1, 2), 30);
        order.assign(Id.generate());
        order.complete();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> order.assign(Id.generate())
        );

        assertEquals("cannot assign order in progress or completed", ex.getMessage());
    }

    @Test
    void shouldNotAssignOrder_when_CourierIdNull() {
        Order order = Order.create(Location.create(1, 2), 30);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> order.assign(null)
        );

        assertEquals("courierId must not be null", ex.getMessage());
    }

    @Test
    void shouldNotCompleteOrder_when_WhenOrderNotAssigned() {
        Order order = Order.create(Location.create(1, 2), 30);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                order::complete
        );

        assertEquals("only assigned orders can be completed", ex.getMessage());
    }

    @Test
    void shouldNotCompleteOrder_when_OrderAlreadyCompleted() {
        Order order = Order.create(Location.create(1, 2), 30);
        order.assign(Id.generate());
        order.complete();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                order::complete
        );

        assertEquals("only assigned orders can be completed", ex.getMessage());
    }
}
