package microarch.delivery.core.domain.model;

import io.hypersistence.tsid.TSID;
import org.jmolecules.ddd.annotation.ValueObject;
import org.jmolecules.ddd.types.Identifier;
import org.springframework.util.Assert;

@ValueObject
public record Id(long id) implements Identifier, Comparable<Id> {

    public Id {
        Assert.isTrue(id > 0, "id must greater then 0");
    }

    public static Id of(long id) {
        return new Id(id);
    }

    public static Id of(String id) {
        return new Id(Long.parseLong(id));
    }

    public static Id generate() {
        return Id.of(TSID.Factory.getTsid().toLong());
    }

    @Override
    public int compareTo(Id other) {
        return Long.compare(this.id, other.id);
    }
}