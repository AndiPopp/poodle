package de.andipopp.poodle.data.entity;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vaadin.flow.component.avatar.Avatar;

import de.andipopp.poodle.data.Role;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.entity.polls.Vote;

@Entity
@Table(name = "application_user")
public class User extends AbstractAutoIdEntity {

    private String username;
    private String name;
    @JsonIgnore
    private String hashedPassword;
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
    @Lob
    private String profilePictureUrl;

    private Avatar avatar;
    
    private ZoneId timeZone;
    

	@OneToMany(cascade = CascadeType.ALL, targetEntity=AbstractPoll.class, orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.FALSE)
    List<AbstractPoll<?,?>> polls = new ArrayList<>();
    
	@OneToMany(cascade = CascadeType.ALL, targetEntity=AbstractPoll.class, orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.FALSE)
    List<Vote<?,?>> votes = new ArrayList<>();
    
	
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
	
    /**
	 * Getter for a copy of {@link #avatar}
	 * @return a copy of {@link #avatar}
	 */
	public Avatar getAvatarCopy() {
		Avatar avatar = new Avatar(name);
		if (this.avatar != null && this.avatar.getImage() != null) avatar.setImage(this.avatar.getImage());
		else if (this.avatar != null && this.avatar.getImageResource() != null) avatar.setImageResource(this.avatar.getImageResource());
		else if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) avatar.setImage(profilePictureUrl);
		return avatar;
	}
	
	
	/**
	 * Setter for {@link #avatar}
	 * @param avatar the {@link #avatar} to set
	 */
	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}
	
	/**
	 * Getter for {@link #avatar}
	 * @return the {@link #avatar}
	 */
	public Avatar getAvatar() {
		return avatar;
	}
	/**
	 * Getter for {@link #polls}
	 * @return the {@link #polls}
	 */
	public List<AbstractPoll<?, ?>> getPolls() {
		return polls;
	}
	/**
	 * Setter for {@link #polls}
	 * @param polls the {@link #polls} to set
	 */
	public void setPolls(List<AbstractPoll<?, ?>> polls) {
		this.polls = polls;
	}

	public void removePoll(AbstractPoll<?,?> poll) {
		poll.setOwner(null);
		polls.remove(poll);
	}
	
	public void addPoll(AbstractPoll<?,?> poll) {
		polls.add(poll);
		poll.setOwner(this);
	}
	
    /**
	 * Getter for {@link #timeZone}
	 * @return the {@link #timeZone}
	 */
	public ZoneId getTimeZone() {
		return timeZone;
	}
	/**
	 * Setter for {@link #timeZone}
	 * @param timeZone the {@link #timeZone} to set
	 */
	public void setTimeZone(ZoneId timeZone) {
		this.timeZone = timeZone;
	}
}
