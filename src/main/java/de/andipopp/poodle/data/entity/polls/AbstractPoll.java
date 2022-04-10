package de.andipopp.poodle.data.entity.polls;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import de.andipopp.poodle.data.entity.AbstractAutoIdEntity;
import de.andipopp.poodle.data.entity.Config;
import de.andipopp.poodle.data.entity.User;

@Entity(name = "Poll")
//public abstract class AbstractPoll<O extends AbstractOption<? extends AbstractPoll<O>>> extends AbstractEntity {
public abstract class AbstractPoll<P extends AbstractPoll<P,O>, O extends AbstractOption<P,O>> extends AbstractAutoIdEntity {
	
	/* ==========
	 * = Fields =
	 * ========== */
	
	/**
	 * The poll's title
	 */
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
	private LocalDate deleteDate;
	
	@OneToMany(cascade = CascadeType.ALL, targetEntity=AbstractOption.class, mappedBy = "parent", orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<O> options;
	
	/**
	 * Enable "if need by vote"
	 */
	private boolean enableIfNeedBe;
	
	/**
	 * Enable abstaining
	 */
	private boolean enableAbstain;
	
	private boolean closed;
	
	@OneToMany(cascade = CascadeType.ALL, targetEntity=Vote.class, mappedBy = "parent",  orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<Vote<P,O>> votes;
	
	/* ================
	 * = Constructors =
	 * ================ */
	
	/**
	 * 
	 */
	public AbstractPoll() {
		this.options = new ArrayList<O>();
		this.createDate = Instant.now();
		this.deleteDate = LocalDate.now().plusDays(Config.getCurrentConfig().getDefaultPollRententionDays());
		this.votes = new ArrayList<>();
	}
	
	/**
	 * @param title
	 * @param description
	 */
	public AbstractPoll(String title, String description) {
		this();
		setTitle(title);
		setDescription(description);
	}
	
	public AbstractPoll(User owner) {
		this();
		setOwner(owner);
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
	
	public LocalDate getLocalCreateDate() {
		return LocalDate.ofInstant(createDate, TimeZone.getDefault().toZoneId()); //TODO adjust to user's time zone (from VaadinRequest?)
	}

	/**
	 * Setter for {@link #createDate}
	 * @param createDate the {@link #createDate} to set
	 */
	public void setCreateDate(Instant createDate) {
		this.createDate = createDate;
	}
	
	/**
	 * Getter for {@link #deleteDate}
	 * @return the {@link #deleteDate}
	 */
	public LocalDate getDeleteDate() {
		return deleteDate;
	}

	/**
	 * Setter for {@link #deleteDate}.
	 * Respects the {@link #MAX_RETENTION_DAYS}
	 * @param deleteDate the {@link #deleteDate} to set
	 */
	public void setDeleteDate(LocalDate deleteDate) {
		LocalDate max = LocalDate.now().plusDays(Config.getCurrentConfig().getMaxPollRetentionDays());
		if (deleteDate.compareTo(max) > 0) {
			this.deleteDate = max;
		}else {
			this.deleteDate = deleteDate;
		}
	}
	

	/**
	 * Getter for {@link #closed}
	 * @return the {@link #closed}
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * Setter for {@link #closed}
	 * @param closed the {@link #closed} to set
	 */
	public void setClosed(boolean closed) {
		this.closed = closed;
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
	 * Getter for {@link #enableIfNeedBe}
	 * @return the {@link #enableIfNeedBe}
	 */
	public boolean isEnableIfNeedBe() {
		return enableIfNeedBe;
	}

	/**
	 * Setter for {@link #enableIfNeedBe}
	 * @param enableIfNeedBe the {@link #enableIfNeedBe} to set
	 */
	public void setEnableIfNeedBe(boolean enableIfNeedBe) {
		this.enableIfNeedBe = enableIfNeedBe;
	}

	/**
	 * Getter for {@link #enableAbstain}
	 * @return the {@link #enableAbstain}
	 */
	public boolean isEnableAbstain() {
		return enableAbstain;
	}

	/**
	 * Setter for {@link #enableAbstain}
	 * @param enableAbstain the {@link #enableAbstain} to set
	 */
	public void setEnableAbstain(boolean enableAbstain) {
		this.enableAbstain = enableAbstain;
	}

	/**
	 * Getter for {@link #options}
	 * @return the {@link #options}
	 */
	public List<O> getOptions() {
		return options;
	}

	/**
	 * Setter for {@link #options}
	 * @param options the {@link #options} to set
	 */
	public void setOptions(List<O> options) {
		this.options = options;
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
		setOptionParent(option);
	}
	
	/**
	 * Set this poll as parent for the given option
	 * @param option the option
	 */
	protected abstract void setOptionParent(O option);
	
	/**
	 * Remove a given option from the set of options
	 * @param option the option to remove
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
	 * Getter for {@link #votes}
	 * @return the {@link #votes}
	 */
	public List<Vote<P,O>> getVotes() {
		return votes;
	}

	/**
	 * Search a vote by this user in {@link #votes} and return it
	 * @param user the user whose vote to search
	 * @return the vote by this user, null if the user has not yet voted
	 */
	public Vote<P,O> getVote(User user) {
		for(Vote<P,O> vote : votes) {
			if (vote.getOwner() != null && vote.getOwner().equals(user)) return vote;
		}
		return null;
	}
	
	/**
	 * Setter for {@link #votes}
	 * @param votes the {@link #votes} to set
	 */
	public void setVotes(List<Vote<P,O>> votes) {
		this.votes = votes;
	}
	
	/**
	 * Create a new empty vote for this poll and add it to its votes 
	 * @return a new empty vote for this poll's option 
	 */
	public Vote<P,O> addEmptyVote(){
		Vote<P,O> vote = new Vote<P,O>(this);
		votes.add(vote);
		return vote;
	}
	
	/**
	 * Add a vote to {@link #votes}
	 * @param vote the vote to add
	 */
	public void addVote(Vote<P,O> vote) {
		votes.add(vote);
	}
	/**
	 * Remove a specific vote from {@link #votes}
	 * @param vote the vote to remove
	 * @return true if the vote was in the poll and has been removed, false otherwise
	 */
	public boolean removeVote(Vote<P,O> vote) {
		if (votes.contains(vote)) {
			for(AbstractOption<P, O> option : options) {
				option.removeAnswer(vote);
			}
			votes.remove(vote);
			vote.setParent(null);
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Remove a vote with a given UUID from {@link #votes}
	 * @param id the UUID of the vote to remove
	 * @return if the a vote with the given UUID was in {@link #votes} and has been removed
	 */
	public boolean removeVote(UUID id) {
		for(Vote<P,O> vote : votes) {
			if (vote.getId().equals(id)) return removeVote(vote);
		}
		return false;
	}
	
	/**
	 * Remove a vote with a given UUID from {@link #votes}
	 * @param id string representation of the UUID of the vote to remove
	 * @return if the a vote with the given UUID was in {@link #votes} and has been removed
	 */
	public boolean removeVote(String id) {
		return removeVote(UUID.fromString(id));
	}
	
	/* ========================
	 * = UI auxiliary methods =
	 * ======================== */
	
//	public abstract Component buildListView();
	
	/* =================
	 * = Other Methods =
	 * ================= */
	
	@Transient
	private List<O> sorted;
	
	public List<O> getOptionsByPositiveAnswers() {
		if (options == null) return null;
		if (sorted == null) {
			sorted = new ArrayList<>(options.size());
			sorted.addAll(options);
			sorted.sort(OptionComparatorByPositiveVotes.GET);
		}
		return sorted;
	}
	
	public void clearSortedOptionsByPositiveAnswers() {;
		sorted = null;
	}

}
