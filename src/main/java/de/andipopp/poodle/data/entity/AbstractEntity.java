package de.andipopp.poodle.data.entity;

import java.util.UUID;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AbstractEntity {

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
        if (!(obj instanceof AbstractAutoIdEntity)) {
            return false; // null or other class
        }
        AbstractAutoIdEntity other = (AbstractAutoIdEntity) obj;

        if (getId() != null) {
            return getId().equals(other.getId());
        }
        return super.equals(other);
    }
}
