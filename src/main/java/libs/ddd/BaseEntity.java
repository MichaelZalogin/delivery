package libs.ddd;

import jakarta.persistence.*;
import jakarta.persistence.InheritanceType;
import org.jmolecules.ddd.annotation.Entity;

import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class BaseEntity<TId extends Comparable<TId>> implements Comparable<BaseEntity<TId>> {

    @Id
    @Column(name = "id")
    protected TId id;

    public TId getId() {
        return id;
    }

    protected BaseEntity() {
    }

    protected BaseEntity(TId id) {
        this.id = id;
    }

    protected boolean isTransient() {
        return id == null || id.equals(defaultValue());
    }

    protected TId defaultValue() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (this == obj)
            return true;

        if (!(obj instanceof BaseEntity<?> other))
            return false;

        if (!this.getClass().equals(other.getClass()))
            return false;

        if (this.isTransient() || other.isTransient())
            return false;

        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return (getClass() + (id != null ? id.toString() : "")).hashCode();
    }

    @Override
    public int compareTo(BaseEntity<TId> other) {
        if (other == null)
            return 1;

        if (this == other)
            return 0;

        return this.id.compareTo(other.id);
    }
}