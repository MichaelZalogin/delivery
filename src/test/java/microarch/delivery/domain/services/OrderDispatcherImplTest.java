package microarch.delivery.domain.services;

import microarch.delivery.domain.model.Id;
import microarch.delivery.domain.model.Location;
import microarch.delivery.domain.model.courier.Courier;
import microarch.delivery.domain.model.order.Order;
import microarch.delivery.domain.model.order.OrderStatus;
import microarch.delivery.domain.services.OrderDispatcherImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderDispatcherImplTest {

    private final OrderDispatcherImpl dispatcher = new OrderDispatcherImpl();

    private final Location locA = Location.create(1, 1);
    private final Location locB = Location.create(10, 1);

    @Test
    void shouldDispatchOrderToFastestCourier() {
        Order order = Order.create(locB, 5);

        Courier slow = Courier.create("Slow", 1, locA);
        Courier fast = Courier.create("Fast", 5, locA);

        Courier selected = dispatcher.dispatch(order, List.of(slow, fast));

        assertEquals("Fast", selected.getName());
        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
        assertEquals(selected.getId(), order.getCourierId());
    }

    @Test
    void shouldAssignOrderToCourier_when_CourierHasStorageCapacity() {
        Order order = Order.create(locB, 5);
        Courier courier = Courier.create("John", 3, locA);

        Courier selected = dispatcher.dispatch(order, List.of(courier));

        assertEquals(courier, selected);
        assertEquals(OrderStatus.ASSIGNED, order.getStatus());
        assertNotNull(courier.getStoragePlaces().get(0).getOrderId());
    }

    @Test
    void shouldNotDispatchOrder_when_NoCouriersProvided() {
        Order order = Order.create(locA, 5);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> dispatcher.dispatch(order, List.of())
        );

        assertEquals("No available couriers capable of delivering the order", ex.getMessage());
    }

    @Test
    void shouldNotDispatchOrder_when_NoCourierCanTakeOrder() {
        Order order = Order.create(locA, 9999);

        Courier courier = Courier.create("John", 3, locA);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> dispatcher.dispatch(order, List.of(courier))
        );

        assertEquals("No available couriers capable of delivering the order", ex.getMessage());
    }

    @Test
    void shouldNotDispatchOrder_when_OrderNotCreated() {
        Order order = Order.create(locA, 5);
        order.assign(Id.generate());

        Courier courier = Courier.create("John", 3, locA);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> dispatcher.dispatch(order, List.of(courier))
        );

        assertEquals("Order must be in CREATED status for dispatch", ex.getMessage());
    }
}