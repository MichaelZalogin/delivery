package microarch.delivery.core.domain.services;

import microarch.delivery.core.domain.model.courier.Courier;
import microarch.delivery.core.domain.model.order.Order;

import java.util.List;

public interface OrderDispatcher {
    Courier dispatch(Order order, List<Courier> courierList);
}
