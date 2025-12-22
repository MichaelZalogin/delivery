package microarch.delivery.domain.services;

import microarch.delivery.domain.model.courier.Courier;
import microarch.delivery.domain.model.order.Order;

import java.util.List;

public interface OrderDispatcher {
    Courier dispatch(Order order, List<Courier> courierList);
}
