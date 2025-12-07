package libs.ddd;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.context.ApplicationEvent;

import java.time.Instant;
import java.util.UUID;

public abstract class DomainEvent extends ApplicationEvent {
    private final UUID eventId = UUID.randomUUID();
    private final Instant occurredOnUtc = Instant.now();

    public DomainEvent(Object source) {
        super(source);
    }

    // Fake Ctr or Jackson / JPA
    protected DomainEvent() {
        super("default");
    }

    public UUID getEventId() {
        return eventId;
    }

    public Instant getOccurredOnUtc() {
        return occurredOnUtc;
    }

    @JsonIgnore
    @Override
    public Object getSource() {
        return super.getSource();
    }
}