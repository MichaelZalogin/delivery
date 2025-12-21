package microarch.delivery.application;

import microarch.delivery.domain.model.Id;
import microarch.delivery.domain.model.order.Order;
import microarch.delivery.domain.model.order.OrderStatus;
import org.jmolecules.ddd.types.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderRepository extends Repository<Order, Id> {

    @Transactional(rollbackFor = Exception.class)
    Order save(Order order);

    Order findById(Id orderId);

    Order getNewCreatedOrder();

    List<Order> getOrdersByStatus(OrderStatus orderStatus);
}
