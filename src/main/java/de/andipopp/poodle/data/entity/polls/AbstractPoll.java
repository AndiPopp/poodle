package de.andipopp.poodle.data.entity.polls;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import com.vaadin.flow.router.QueryParameters;

import de.andipopp.poodle.data.Role;
import de.andipopp.poodle.data.entity.AbstractAutoIdEntity;
import de.andipopp.poodle.data.entity.Config;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.util.UUIDUtils;

@Entity(name = "Poll")
public abstract class AbstractPoll<P extends AbstractPoll<P,O>, O extends AbstractOption<P,O>> extends AbstractAutoIdEntity {
	
	/* ==========
	 * = Fields =
	 * ========== */
	
	/**
	 * The query parameter name for the {@link #getId()}
	 */
	public static final String ID_PARAMETER_NAME = "pollId";
	
	/**
	 * The query paramter name for the {@link #editKey}
	 */
	public static final String EDIT_KEY_PARAMETER_NAME = "editKey";
	
	/**
	 * Secure RNG to generate {@link #editKey} values
	 */
	public static final SecureRandom RNG = new SecureRandom();
	
	/**
	 * A key which has to be present for the UI to allow editing
	 */
	@Nullable
	@Column(length = 16)
	private String editKey;
	
	/**
	 * Max length of the {@link #title}
	 */
	public static final int MAX_TITLE_LENGTH = 127;
	
	/**
	 * The poll's title
	 */
	@NotEmpty
	@NotNull
	@Column(length = MAX_TITLE_LENGTH)
	@Size(max = MAX_TITLE_LENGTH)
	private String title;
	
	/**
	 * Max length of the {@link #description}
	 */
	public static final int MAX_DESCRIPTION_LENGTH = 511;
	
	/**
	 * An optional poll description
	 */
	@Nullable
	@Column(length = MAX_DESCRIPTION_LENGTH) 
	@Size(max = MAX_DESCRIPTION_LENGTH)
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
	@NotNull
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
	
	/**
	 * If true, the poll is closed
	 */
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
		this.deleteDate = LocalDate.now().plusDays(Config.getCurrent().getDefaultPollRententionDays());
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
	 * Getter for {@link #editKey}
	 * @return the {@link #editKey}
	 */
	public String getEditKey() {
		return editKey;
	}

	/**
	 * Setter for {@link #editKey}
	 * @param editKey the {@link #editKey} to set
	 */
	public void setEditKey(String editKey) {
		this.editKey = editKey;
	}
	
	/**
	 * Uses {@link #RNG} to generate a new random {@link #editKey}
	 */
	public void generateEditKey() {
		byte[] bytes = new byte[12];
		RNG.nextBytes(bytes);
		setEditKey(UUIDUtils.enc.encodeToString(bytes));
	}
	
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
		LocalDate max = LocalDate.now().plusDays(Config.getCurrent().getMaxPollRetentionDays());
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
		Vote<P,O> vote = new Vote<P,O>(this, true);
		votes.add(vote);
		return vote;
	}
	
	/**
	 * Create an empty vote for this poll, but do not attacht it to the poll or any of its childrent
	 * @return the newly created empty vote
	 */
	public Vote<P,O> createEmptyVote(){
		return new Vote<P,O>(this, false);
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
	
	/**
	 * Check if a given user can edit this poll
	 * @param user the user who wants to edit the poll
	 * @return true if the user can edit the poll, false otherwise
	 */
	public boolean canEdit(User user) {
		//admins may edit any polls
		if (user != null && user.getRoles().contains(Role.ADMIN)) return true;
		
		//case for orphan polls
		if (owner == null) {
			//orphan polls can be edit by anyone with the edit key (which must be present), 
			//if the settings allow it
			return getEditKey() != null && Config.getCurrent().isAllowOrphanPolls();
		}
		
		//case for owned polls, only owner may edit
		return user != null && owner.equals(user);
	}
	
	/**
	 * Construct http {@link QueryParameters} for this poll.
	 * This always contains the {@link #getId()} as base64url. If flagged, it also contains the
	 * {@link #editKey} as hex string
	 * @param withEditKey if true, add the {@link #editKey} as hex string
	 * @return the constructed query parameters
	 */
	public QueryParameters buildQueryParameters(boolean withEditKey) {
		Map<String, List<String>> params = new HashMap<>(2);
		//add the poll id as base 64
		List<String> idList = new ArrayList<>(1);
		idList.add(UUIDUtils.uuidToBase64url(getId()));
		params.put(ID_PARAMETER_NAME, idList);
		//add the edit key as hex string if specified
		if (withEditKey && this.editKey != null) {
			List<String> editKeyList = new ArrayList<>(1);
			editKeyList.add(editKey);
			params.put(EDIT_KEY_PARAMETER_NAME, editKeyList);
		}
		//return result
		return new QueryParameters(params);
	}
	
	/* =================
	 * = Other Methods =
	 * ================= */
	
	@Transient
	private List<O> sorted;
	
	@Transient
	public List<O> getOptionsByPositiveAnswers() {
		if (options == null) return null;
		if (sorted == null) {
			sorted = new ArrayList<>(options.size());
			sorted.addAll(options);
			sorted.sort(OptionComparatorByPositiveVotes.GET);
		}
		return sorted;
	}
	
	@Transient
	public void clearSortedOptionsByPositiveAnswers() {;
		sorted = null;
	}
	
}
