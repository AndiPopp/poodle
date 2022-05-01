package de.andipopp.poodle.data.entity;

import java.util.UUID;

import javax.persistence.MappedSuperclass;

import de.andipopp.poodle.data.util.HasUuid;

@MappedSuperclass
public abstract class AbstractEntity implements HasUuid {

    public abstract UUID getId();

    public abstract void setId(UUID id);
	
	@Override
    public int hashCode() {
        if (getId() != null) {
            return getId().hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractEntity)) {
            return false; // null or other class
        }
        AbstractEntity other = (AbstractEntity) obj;

        if (getId() != null) {
            return getId().equals(other.getId());
        }
        return super.equals(other);
    }
}
