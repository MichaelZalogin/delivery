package microarch.delivery.core.domain.model.order;

import libs.ddd.Aggregate;
import microarch.delivery.core.domain.model.Id;
import microarch.delivery.core.domain.model.Location;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class Order extends Aggregate<Id> {

    private final Location location;
    private final int volume;
    private OrderStatus status;
    @Nullable
    private Id courierId;

    private Order(Id id, Location location, int volume, OrderStatus status) {
        this.id = id;
        this.location = location;
        this.volume = volume;
        this.status = status;
    }

    public static Order create(Id id, Location location, int volume) {
        return new Order(id, location, volume, OrderStatus.CREATED);
    }

    public void assign(Id courierId) {
        Assert.notNull(courierId, "courierId must not be null");
        Assert.isTrue(!status.isProgressStatus(), "cannot assign order in progress or completed");
        this.status = OrderStatus.ASSIGNED;
        this.courierId = courierId;
    }

    public void complete() {
        Assert.isTrue(status == OrderStatus.ASSIGNED, "only assigned orders can be completed");
        this.status = OrderStatus.COMPLETED;
    }

    public Location getLocation() {
        return location;
    }

    public int getVolume() {
        return volume;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Id getCourierId() {
        return courierId;
    }
}
