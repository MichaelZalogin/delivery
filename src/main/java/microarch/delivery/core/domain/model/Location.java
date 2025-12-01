package microarch.delivery.core.domain.model;

import org.jilt.Builder;
import org.jmolecules.ddd.annotation.ValueObject;
import org.springframework.util.Assert;

@ValueObject
@Builder(buildMethod = "copy")
public record Location(int x, int y) {

    private static final int MIN_VALUE = 1;
    private static final int MAX_VALUE = 10;

    public Location {
        Assert.isTrue(x >= MIN_VALUE && x <= MAX_VALUE,
                "Coordinate x must be greater than 1 and less than 10");
        Assert.isTrue(y >= MIN_VALUE && y <= MAX_VALUE,
                "Coordinate y must be greater than 1 and less than 10");
    }

    public static Location create(int x, int y) {
        return new Location(x, y);
    }

    public int distanceTo(Location other) {
        return Math.abs(this.x - other.x) + Math.abs(this.y - other.y);
    }
}