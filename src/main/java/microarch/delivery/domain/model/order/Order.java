package microarch.delivery.domain.model.order;

import libs.common.Timex;
import microarch.delivery.domain.model.Id;
import microarch.delivery.domain.model.Location;
import org.jilt.Builder;
import org.jmolecules.ddd.types.AggregateRoot;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.time.OffsetDateTime;

@Builder
public class Order implements AggregateRoot<Order, Id> {

    private final Id id;
    private final Location location;
    private final int volume;
    private OrderStatus status;
    @Nullable
    private Id courierId;
    private OffsetDateTime createdAt;
    private String createdBy;
    @Nullable
    private OffsetDateTime modifiedAt;
    @Nullable
    private String modifiedBy;
    private long version;

    public Order(Id id,
                 Location location,
                 int volume,
                 OrderStatus status,
                 Id courierId,
                 OffsetDateTime createdAt,
                 String createdBy,
                 OffsetDateTime modifiedAt,
                 String modifiedBy,
                 long version) {
        this.id = id;
        this.location = location;
        this.volume = volume;
        this.status = status;
        this.courierId = courierId;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.version = version;
    }

    public static Order create(Location location, int volume) {
        return new Order(Id.generate(), location, volume, OrderStatus.CREATED, null, Timex.currentOffsetDateTime(), "default", null, null, 0);
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

    @Override
    public Id getId() {
        return id;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public OffsetDateTime getModifiedAt() {
        return modifiedAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public long getVersion() {
        return version;
    }
}
