package de.andipopp.poodle.data.entity.polls;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import de.andipopp.poodle.data.entity.AbstractEntity;
import de.andipopp.poodle.data.entity.User;

@Entity
public abstract class AbstractPoll<O extends AbstractOption<?>> extends AbstractEntity {
	
	/* ==========
	 * = Fields =
	 * ========== */
	
	/**
	 * The maximum retention from the current time until the poll is deleted in days
	 */
	private static int MAX_RETENTION_DAYS = 500; //TODO load from config
	
	/**
	 * The default retention from the current time until the poll is deleted in days
	 */
	private static int DEFUALT_RETENTION_DAYS = 180; //TODO load from config
	
	/**
	 * The poll's title
	 */
	@NotNull
	@NotEmpty
	private String title;
	
	/**
	 * An optional poll description
	 */
	private String description;
	
	/**
	 * The poll's owner
	 */
	@ManyToOne
	@NotNull
	private User owner;
	
	/**
	 * The date this poll was created
	 */
	@NotNull
	private Instant createDate;
	
	/**
	 * The date by which this poll is to be deleted
	 * @return
	 */
	@NotNull
	private Instant deletyByDate;
	
	@NotNull
	@OneToMany(cascade = CascadeType.PERSIST, targetEntity=AbstractOption.class)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<O> options;
	
	/**
	 * Set of winning options.
	 * If this field is not null, this indicates that the poll is closed.
	 */
	@Nullable
	@OneToMany(cascade = CascadeType.MERGE, targetEntity=AbstractOption.class)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<O> winners;
	
	/* ================
	 * = Constructors =
	 * ================ */
	
	/**
	 * 
	 */
	public AbstractPoll() {
		this.options = new ArrayList<O>();
		this.createDate = Instant.now();
		this.deletyByDate = this.createDate.plusSeconds(DEFUALT_RETENTION_DAYS*24*60*60);
	}
	
	/**
	 * @param title
	 * @param description
	 */
	public AbstractPoll(String title, String description) {
		this();
		this.title = title;
		this.description = description;
	}
	
	/* ================================
	 * = Getters, setters and similar =
	 * ================================ */
	
	/**
	 * @return the name
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Setter for {@link #title}. Cleans all HTML.
	 * @param title the topic to set
	 */
	public void setTitle(String title) {
		this.title = Jsoup.clean(title, Safelist.none());
	}
	
	/**
	 * Getter for {@link #owner}
	 * @return the {@link #owner}
	 */
	public User getOwner() {
		return owner;
	}

	/**
	 * Setter for {@link #owner}
	 * @param owner the {@link #owner} to set
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	/**
	 * Getter for {@link #createDate}
	 * @return the {@link #createDate}
	 */
	public Instant getCreateDate() {
		return createDate;
	}
	
	public LocalDateTime getLocalCreateDate() {
		return LocalDateTime.ofInstant(createDate, TimeZone.getDefault().toZoneId()); //TODO adjust to user's time zone (from VaadinRequest?)
	}

	/**
	 * Setter for {@link #createDate}
	 * @param createDate the {@link #createDate} to set
	 */
	public void setCreateDate(Instant createDate) {
		this.createDate = createDate;
	}
	
	/**
	 * Getter for {@link #deletyByDate}
	 * @return the {@link #deletyByDate}
	 */
	public Instant getDeletyByDate() {
		return deletyByDate;
	}

	/**
	 * Setter for {@link #deletyByDate}.
	 * Respects the {@link #MAX_RETENTION_DAYS}
	 * @param deletyByDate the {@link #deletyByDate} to set
	 */
	public void setDeletyByDate(Instant deletyByDate) {
		Instant max = Instant.now().plusSeconds(MAX_RETENTION_DAYS*24*60*60);
		if (deletyByDate.compareTo(max) > 0) {
			this.deletyByDate = max;
		}else {
			this.deletyByDate = deletyByDate;
		}
	}

	/**
	 * Getter for {@link #description}
	 * @return the {@link #description}
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter for {@link #description}. Cleans dangerous HMTL.
	 * @param description the {@link #description} to set
	 */
	public void setDescription(String description) {
		this.description = Jsoup.clean(description, Safelist.basic());
	}
	
	/**
	 * Get the number of {@link #options}
	 * @return the number of {@link #options}
	 */
	public int getNumberOfOptions() {
		return options.size();
	}
	
	/**
	 * Add a new option to the poll
	 * @param option the new option
	 */
	public void addOption(O option) {
		options.add(option);
		setParent(option);
	}
	
	/**
	 * Set this poll as parent for the given option
	 * @param option the option
	 */
	protected abstract void setParent(O option);
	
	/**
	 * Remove a given option from the set of options
	 * @param option the options to remove
	 * @return true if the option was in the poll and has been removed, false otherwise
	 */
	public boolean removeOption(O option) {
		if (this.options.contains(option)) {
			this.options.remove(option);
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Remove the option with the given UUID 
	 * @param id the option's UUID
	 * @return true if the option was in the poll and has been removed, false otherwise
	 */
	public boolean removeOption(UUID id) {
		for (O option : this.options) {
			if (option.getId().equals(id)) {
				return removeOption(option);
			}
		}
		return false;
	}
	
	/**
	 * Remove the option with the given UUID 
	 * @param ruid the option's UUID's string representation
	 * @return true if the option was in the poll and has been removed, false otherwise
	 */
	public boolean removeOption(String id) {
		return removeOption(UUID.fromString(id));
	}
	
	/**
	 * Get the iterator for {@link #options}
	 * @return the iterator for {@link #options}
	 */
	public Iterator<O> getOptionIterator() {
		return options.iterator();
	}
	
	
	/**
	 * Check if the poll is closed
	 * @return true if the poll is closed, false otherwise
	 */
	public boolean isClosed() {
		return winners != null && !winners.isEmpty();
	}
	
	
	/**
	 * Get the iterator for {@link #winners}
	 * @return the iterator for {@link #winners, null if #winners is null
	 */
	public Iterator<O> getWinnerIterator() {
		if (winners == null) return null;
		return winners.iterator();
	}
	
	/* =================
	 * = Other Methods =
	 * ================= */
	
}
