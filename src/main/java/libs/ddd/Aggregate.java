package libs.ddd;

import java.util.ArrayList;
import java.util.List;

public abstract class Aggregate<TId extends Comparable<TId>> extends BaseEntity<TId> implements AggregateRoot<TId> {
    protected final List<DomainEvent> domainEvents = new ArrayList<>();

    protected Aggregate() {}

    protected Aggregate(TId id) {
        super(id);
    }

    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents); // immutable view
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    public void raiseDomainEvent(DomainEvent domainEvent) {
        domainEvents.add(domainEvent);
    }
}