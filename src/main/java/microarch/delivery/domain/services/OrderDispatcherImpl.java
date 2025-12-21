package microarch.delivery.domain.services;

import microarch.delivery.domain.model.courier.Courier;
import microarch.delivery.domain.model.order.Order;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.List;

import static microarch.delivery.domain.model.order.OrderStatus.CREATED;

@Service
public class OrderDispatcherImpl implements OrderDispatcher {

    @Override
    public Courier dispatch(Order order, List<Courier> courierList) {
        Assert.notNull(order, "order must be not null");
        Assert.notNull(courierList, "courierList must be not null");
        Assert.isTrue(order.getStatus() == CREATED, "Order must be in CREATED status for dispatch");

        List<Courier> availableCouriers = courierList.stream()
                .filter(c -> c.canTakeOrder(order))
                .toList();
        Assert.isTrue(!availableCouriers.isEmpty(), "No available couriers capable of delivering the order");

        Courier bestCourier = availableCouriers.stream()
                .min(Comparator.comparingDouble(c -> c.calculateDeliveryTime(order.getLocation())))
                .orElse(null);

        bestCourier.takeOrder(order);
        order.assign(bestCourier.getId());
        return bestCourier;
    }
}
