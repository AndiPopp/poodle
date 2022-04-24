package de.andipopp.poodle.data.entity;

import java.time.ZoneId;
import java.util.Set;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.component.avatar.Avatar;

import de.andipopp.poodle.data.Role;
import de.andipopp.poodle.views.components.PoodleAvatar;

@Entity
@Table(name = "application_user")
public class User extends AbstractAutoIdEntity {

	private static final int MAX_USERNAME_SIZE = 32;
	
	@Column(length = MAX_USERNAME_SIZE, unique=true)
	@Size(max = MAX_USERNAME_SIZE)
    private String username;
    
    private static final int MAX_NAME_SIZE = 64;
    
    @Column(length = MAX_NAME_SIZE)
	@Size(max = MAX_NAME_SIZE)
    private String displayName;
    
    @JsonIgnore
    @Column(length = 64)
	@Size(max = 64)
    private String hashedPassword;
    
    @Transient
    private String hashedPassword2;
    
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    
    @Nullable
    @Column(length = 40)
	@Size(max = 40)
    private String zoneId;
	
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getHashedPassword() {
        return hashedPassword;
    }
    
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    
    /**
	 * Getter for {@link #hashedPassword2}
	 * @return the {@link #hashedPassword2}
	 */
	public String getHashedPassword2() {
		return hashedPassword2;
	}

	/**
	 * Setter for {@link #hashedPassword2}
	 * @param hashedPassword2 the {@link #hashedPassword2} to set
	 */
	public void setHashedPassword2(String hashedPassword2) {
		this.hashedPassword2 = hashedPassword2;
	}

	public Set<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
    /**
	 * Getter for a copy of {@link #avatar}
	 * @return a copy of {@link #avatar}
	 */
	public Avatar getAvatar() {
//		Avatar avatar = new Avatar(displayName);
		return PoodleAvatar.forUser(this);
	}
	
    /**
	 * Getter for {@link #zoneId}
	 * @return the {@link #zoneId}
	 */
	public String getZoneId() {
		return zoneId;
	}
	/**
	 * Setter for {@link #zoneId}
	 * @param zoneId the {@link #zoneId} to set
	 */
	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}
	/**
	 * Get the time zone specified by {@link #zoneId}
	 * @return the time zone specified by {@link #zoneId}, <code>null</code>l if {@link #zoneId} is <code>null</code>ull
	 */
	public ZoneId getTimeZone() {
		if (zoneId != null) return ZoneId.of(zoneId);
		else return null;
	}
	/**
	 * Set the {@link #zoneId} to the ID of the argument time zone
	 * @param timeZone whose ID to set for {@link #zoneId}
	 */
	public void setTimeZone(ZoneId timeZone) {
		if (timeZone != null) this.zoneId = timeZone.getId();
		else this.zoneId = null;
	}
}
