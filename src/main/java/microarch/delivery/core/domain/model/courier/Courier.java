package microarch.delivery.core.domain.model.courier;

import libs.ddd.Aggregate;
import microarch.delivery.core.domain.model.Id;
import microarch.delivery.core.domain.model.Location;
import microarch.delivery.core.domain.model.order.Order;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.List;

import static microarch.delivery.core.domain.model.courier.StoragePlaceType.BACKPACK;

public class Courier extends Aggregate<Id> {

    private final String name;
    private final int speed;
    private Location location;
    private final List<StoragePlace> storagePlaces;

    private Courier(String name, int speed, Location location, List<StoragePlace> storagePlaces) {
        super(Id.generate());
        this.name = name;
        this.speed = speed;
        this.location = location;
        this.storagePlaces = storagePlaces;
    }

    public static Courier create(String name, int speed, Location location) {
        Assert.hasText(name, "name must be not empty");
        Assert.isTrue(speed > 0, "speed must greater then 0");
        Assert.notNull(location, "location must be not null");
        List<StoragePlace> storagePlaces = List.of(StoragePlace.create(BACKPACK));

        return new Courier(name, speed, location, storagePlaces);
    }

    public Courier addStoragePlace(StoragePlaceType storagePlaceType) {
        storagePlaces.add(StoragePlace.create(storagePlaceType));
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
        order.assign(getId());

        StoragePlace bestPlace = storagePlaces.stream()
                .filter(place -> place.canPut(order.getVolume()))
                .min(Comparator.comparingInt(p -> p.getPlaceType().capacity()))
                .orElseThrow(() -> new IllegalStateException("No suitable storage place found"));

        bestPlace.putOrder(order.getId(), order.getVolume());
        return this;
    }

    public void completeOrder(Order order) {
        Assert.notNull(order, "order must be not null");
        Assert.isTrue(getId().equals(order.getCourierId()), "Courier cannot complete order assigned to another courier");
        order.complete();
        storagePlaces.stream()
                .filter(place -> order.getId().equals(place.getOrderId()))
                .findFirst()
                .ifPresent(StoragePlace::clear);
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
}
