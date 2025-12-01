package microarch.delivery.core.domain.model.courier;

import microarch.delivery.core.domain.model.Id;
import org.jilt.Builder;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Objects;

@Entity
@Builder(toBuilder = "copy")
public class StoragePlace {

    @Identity
    private final Id id;
    private final StoragePlaceType placeType;
    @Nullable
    private Id orderId;

    StoragePlace(Id id, StoragePlaceType type, Id orderId) {
        Assert.notNull(type, "type must be not null");
        this.id = id;
        this.placeType = type;
        this.orderId = orderId;
    }

    public static StoragePlace create(StoragePlaceType type, Id orderId) {
        return new StoragePlace(Id.generate(), type, orderId);
    }

    public boolean isEmpty() {
        return this.orderId == null;
    }

    public boolean canPut(int orderVolume) {
        return isEmpty() && orderVolume <= placeType.capacity();
    }

    public void putOrder(Id orderId, int orderVolume) {
        Assert.notNull(orderId, "orderId must be not null");
        Assert.isTrue(isEmpty(), "storage already contains order");
        Assert.isTrue(orderVolume <= this.placeType.capacity(), "Order greater current capacity");
        this.orderId = orderId;
    }

    public Id removeOrder() {
        Id removed = orderId;
        this.orderId = null;
        return removed;
    }

    public Id getId() {
        return id;
    }

    public StoragePlaceType getPlaceType() {
        return placeType;
    }

    public Id getOrderId() {
        return orderId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        StoragePlace that = (StoragePlace) object;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
