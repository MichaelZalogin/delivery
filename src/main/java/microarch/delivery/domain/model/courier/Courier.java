package microarch.delivery.domain.model.courier;

import libs.common.Timex;
import org.jilt.Builder;
import org.springframework.lang.Nullable;
import libs.ddd.Aggregate;
import org.jmolecules.ddd.types.AggregateRoot;
import microarch.delivery.domain.model.Id;
import microarch.delivery.domain.model.Location;
import microarch.delivery.domain.model.order.Order;
import org.springframework.util.Assert;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static microarch.delivery.domain.model.courier.StoragePlaceType.BACKPACK;

@Builder
public class Courier implements AggregateRoot<Courier, Id> {

    private final Id id;
    private final String name;
    private final int speed;
    private Location location;
    private final List<StoragePlace> storagePlaces;
    private OffsetDateTime createdAt;
    private String createdBy;
    @Nullable
    private OffsetDateTime modifiedAt;
    @Nullable
    private String modifiedBy;
    private long version;

    public Courier(Id id,
                   String name,
                   int speed,
                   Location location,
                   List<StoragePlace> storagePlaces,
                   OffsetDateTime createdAt,
                   String createdBy,
                   OffsetDateTime modifiedAt,
                   String modifiedBy,
                   long version) {
        this.id = id;
        this.name = name;
        this.speed = speed;
        this.location = location;
        this.storagePlaces = storagePlaces;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.modifiedAt = modifiedAt;
        this.modifiedBy = modifiedBy;
        this.version = version;
    }

    public static Courier create(String name, int speed, Location location) {
        Assert.hasText(name, "name must be not empty");
        Assert.isTrue(speed > 0, "speed must greater then 0");
        Assert.notNull(location, "location must be not null");
        List<StoragePlace> storagePlaces = List.of(StoragePlace.create(BACKPACK));

        return new Courier(Id.generate(), name, speed, location, storagePlaces, Timex.currentOffsetDateTime(), "default", null, null, 0);
    }

    public Courier addStoragePlace(StoragePlaceType storagePlaceType) {
        storagePlaces.add(StoragePlace.create(storagePlaceType));
        this.modifiedAt = Timex.currentOffsetDateTime();
        this.modifiedBy = "default";
        return this;
    }

    public boolean canTakeOrder(Order order) {
        Assert.notNull(order, "order must be not null");
        return storagePlaces.stream()
                .anyMatch(place -> place.canPut(order.getVolume()));
    }

    public Courier takeOrder(Order order) {
        Assert.notNull(order, "order must be not null");
        Assert.isTrue(canTakeOrder(order), "Courier cannot take this order. No storage capacity");

        StoragePlace bestPlace = storagePlaces.stream()
                .filter(place -> place.canPut(order.getVolume()))
                .min(Comparator.comparingInt(p -> p.getPlaceType().capacity()))
                .orElseThrow(() -> new IllegalStateException("No suitable storage place found"));

        bestPlace.putOrder(order.getId(), order.getVolume());
        this.modifiedAt = Timex.currentOffsetDateTime();
        this.modifiedBy = "default";
        return this;
    }

    public void completeOrder(Order order) {
        Assert.notNull(order, "order must be not null");
        Assert.isTrue(this.id == order.getCourierId(), "Courier cannot complete order assigned to another courier");
        findStorageByOrderId(order.getId()).ifPresent(StoragePlace::clear);
        this.modifiedAt = Timex.currentOffsetDateTime();
        this.modifiedBy = "default";
    }

    public void terminateOrder(Order order) {
        Assert.notNull(order, "order must be not null");
        Assert.isTrue(this.id == order.getCourierId(), "Courier cannot terminate order assigned to another courier");
        findStorageByOrderId(order.getId()).ifPresent(StoragePlace::clear);
        this.modifiedAt = Timex.currentOffsetDateTime();
        this.modifiedBy = "default";
    }

    public double calculateDeliveryTime(Location targetLocation) {
        Assert.notNull(targetLocation, "targetLocation must be not null");
        int distance = location.distanceTo(targetLocation);
        return (double) distance / speed;
    }

    public void move(Location targetLocation) {
        Assert.notNull(targetLocation, "targetLocation must be not null");
        int dx = targetLocation.x() - location.x();
        int dy = targetLocation.y() - location.y();

        int stepX = 0;
        int stepY = 0;

        int remaining = speed;

        int moveX = Math.abs(dx);
        if (moveX > 0) {
            int takeX = Math.min(remaining, moveX);
            stepX = (dx > 0) ? takeX : -takeX;
            remaining -= takeX;
        }

        int moveY = Math.abs(dy);
        if (remaining > 0 && moveY > 0) {
            int takeY = Math.min(remaining, moveY);
            stepY = (dy > 0) ? takeY : -takeY;
            remaining -= takeY;
        }

        this.location = Location.create(location.x() + stepX, location.y() + stepY);
        this.modifiedAt = Timex.currentOffsetDateTime();
        this.modifiedBy = "default";
    }

    private Optional<StoragePlace> findStorageByOrderId(Id orderId) {
        return storagePlaces.stream()
                .filter(storagePlace -> storagePlace.getOrderId() == orderId)
                .findFirst();
    }

    @Override
    public Id getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Location getLocation() {
        return location;
    }

    public List<StoragePlace> getStoragePlaces() {
        return storagePlaces;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    @Nullable
    public OffsetDateTime getModifiedAt() {
        return modifiedAt;
    }

    @Nullable
    public String getModifiedBy() {
        return modifiedBy;
    }

    public long getVersion() {
        return version;
    }
}
