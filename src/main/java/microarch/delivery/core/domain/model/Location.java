package microarch.delivery.core.domain.model;

import ddd.ValueObject;
import org.springframework.util.Assert;

import java.util.List;

public class Location extends ValueObject<Location> {

    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 10;

    private final int x;
    private final int y;

    private Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Location create(int x, int y) {
        Assert.isTrue(x >= MIN_VALUE && x <= MAX_VALUE, "Coordinate x must be greater than 1 and less than 10");
        Assert.isTrue(y >= MIN_VALUE && y <= MAX_VALUE, "Coordinate y must be greater than 1 and less than 10");

        return new Location(x, y);
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.x, this.y);
    }

    public int distanceTo(Location other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}