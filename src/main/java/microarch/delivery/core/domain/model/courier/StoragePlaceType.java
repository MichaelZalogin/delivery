package microarch.delivery.core.domain.model.courier;

import org.springframework.util.Assert;

public enum StoragePlaceType {
    BACKPACK(30),
    TRUNK(120);

    private final int defaultCapacity;

    StoragePlaceType(int defaultCapacity) {
        Assert.isTrue(defaultCapacity > 0, "id must greater then 0");
        this.defaultCapacity = defaultCapacity;
    }

    public int capacity() {
        return defaultCapacity;
    }
}
