package de.andipopp.poodle.data.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.jsoup.safety.Safelist;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.generator.ConfigLoader;
import de.andipopp.poodle.util.JSoupUtils;

@Entity
public class Config extends AbstractEntity {

	/* ==================
	 * = Static members =
	 * ================== */
	
	/**
	 * UUID v5 of ns:URL "config0.poodle.andi-popp.de".
	 * This UUID specifies the default config to load when poodle starts
	 */
	public static final UUID DEFAULT_CONFIG_ID = UUID.fromString("48e7f9ee-e121-5917-a4c4-cac7da7e47cd");
	
	/**
	 * The currently active config
	 */
	private static Config current;
	
	/**
	 * Getter for {@link #current}
	 * @return the {@link #current}
	 */
	public static Config getCurrent() {
		if (current == null) {
			if (ConfigLoader.configService != null) {
				setCurrent(ConfigLoader.configService.getDefaultConfig());
			} else {
				return new Config();
			}
		}
		return current;
	}

	/**
	 * Setter for {@link #current}
	 * @param currentConfig the {@link #current} to set
	 */
	public static void setCurrent(Config currentConfig) {
		Config.current = currentConfig;
	}
	
	//TODO a version of setCurrentConfig which tries to load a config from backend
	
	/* ================
	 * = Constructors =
	 * ================ */
	
	@Id
	UUID id;
	
	/**
	 * Getter for {@link #id}
	 * @return the {@link #id}
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * Setter for {@link #id}
	 * @param id the {@link #id} to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * Create a new config with the default id.
	 * For other ids use {@link #PoodleConfig(UUID)} instead.
	 */
	public Config() {
		this.setId(DEFAULT_CONFIG_ID);
	} 
	
	/**
	 * Create a new config with the given UUID.
	 * This constructor is to be used when a config other than the default config is to be 
	 * created (e.g. with {@link UUID#randomUUID()} as argument), since {@link #PoodleConfig()} always
	 * creates a new version of the default config with the {@link #DEFAULT_CONFIG_ID}.
	 * @param id
	 */
	public Config(UUID id) {
		this.setId(id);
	}
	
	/* ==================
	 * = Dynamic members =
	 * ================== */
	
	/**
	 * The title displayed at the welcome screen for unregistered users
	 */
	private String welcomeTitle = "Welcome to Poodle";
	
	/**
	 * Getter for {@link #welcomeTitle}
	 * @return the {@link #welcomeTitle}
	 */
	public String getWelcomeTitle() {
		return welcomeTitle;
	}

	/**
	 * Setter for {@link #welcomeTitle}
	 * @param welcomeTitle the {@link #welcomeTitle} to set
	 */
	public void setWelcomeTitle(String welcomeTitle) {
		this.welcomeTitle = welcomeTitle;
	}

	/**
	 * The message displayed below the {@link #welcomeTitle} at the welcome screen for unregistered users.
	 * Can contain {@link Safelist#basicWithImages()} HTML
	 */
	private String welcomeMessage = "Login to manage your polls.";
	
	/**
	 * Getter for {@link #welcomeMessage}
	 * @return the {@link #welcomeMessage}
	 */
	public String getWelcomeMessage() {
		return welcomeMessage; 
	}

	/**
	 * Setter for {@link #welcomeMessage}
	 * @param welcomeMessage the {@link #welcomeMessage} to set
	 */
	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = JSoupUtils.cleanBasicWithImages(welcomeMessage);
	}

	/**
	 * The minimum number of days from the current day {@link AbstractPoll#setDeleteDate(LocalDate)} will accept
	 */
	private int minPollRetentionDays = 7;
	
	/**
	 * Getter for {@link #minPollRetentionDays}
	 * @return the {@link #minPollRetentionDays}
	 */
	public int getMinPollRetentionDays() {
		return minPollRetentionDays;
	}

	/**
	 * Setter for {@link #minPollRetentionDays}
	 * @param minPollRetentionDays the {@link #minPollRetentionDays} to set
	 */
	public void setMinPollRetentionDays(int minPollRetentionDays) {
		this.minPollRetentionDays = minPollRetentionDays;
	}

	/**
	 * Convenience function calculating the latest days for poll retention based on {@link #maxPollRetentionDays}
	 * @return the latest date for poll retention
	 */
	public LocalDate getEarliestPollRetentionDate() {
		return LocalDate.now().plusDays(minPollRetentionDays);
	}
	
	/**
	 * The maximum number of days from the current day {@link AbstractPoll#setDeleteDate()} will accept
	 */
	private int maxPollRetentionDays = 500;
	
	/**
	 * Getter for {@link #maxPollRetentionDays}
	 * @return the {@link #maxPollRetentionDays}
	 */
	public int getMaxPollRetentionDays() {
		return maxPollRetentionDays;
	}

	/**
	 * Convenience function calculating the latest days for poll retention based on {@link #maxPollRetentionDays}
	 * @return the latest date for poll retention
	 */
	public LocalDate getLatestPollRetentionDate() {
		return LocalDate.now().plusDays(maxPollRetentionDays);
	}
	
	public boolean checkDeleteDate(LocalDate deleteDate) {
		long daysBetween = ChronoUnit.DAYS.between(LocalDate.now(), deleteDate);
		return daysBetween <= maxPollRetentionDays && daysBetween >= minPollRetentionDays;
	}
	
	/**
	 * Setter for {@link #maxPollRetentionDays}
	 * @param maxPollRetentionDays the {@link #maxPollRetentionDays} to set
	 */
	public void setMaxPollRetentionDays(int maxPollRetentionDays) {
		this.maxPollRetentionDays = maxPollRetentionDays;
	}

	/**
	 * The default number of day a poll will be stored before it gets auto-deleted
	 */
	private int defaultPollRententionDays = 180;

	/**
	 * Getter for {@link #defaultPollRententionDays}
	 * @return the {@link #defaultPollRententionDays}
	 */
	public int getDefaultPollRententionDays() {
		return defaultPollRententionDays;
	}

	/**
	 * Setter for {@link #defaultPollRententionDays}
	 * @param defaultPollRententionDays the {@link #defaultPollRententionDays} to set
	 */
	public void setDefaultPollRententionDays(int defaultPollRententionDays) {
		this.defaultPollRententionDays = defaultPollRententionDays;
	}

	/**
	 * Allow polls which do no have an owner
	 */
	private boolean allowOrphanPolls = false;

	/**
	 * Getter for {@link #allowOrphanPolls}
	 * @return the {@link #allowOrphanPolls}
	 */
	public boolean isAllowOrphanPolls() {
		return allowOrphanPolls;
	}

	/**
	 * Setter for {@link #allowOrphanPolls}
	 * @param allowOrphanPolls the {@link #allowOrphanPolls} to set
	 */
	public void setAllowOrphanPolls(boolean allowOrphanPolls) {
		this.allowOrphanPolls = allowOrphanPolls;
	}
	
	/**
	 * The maximum size of poll and user images in kilobytes
	 */
	private int imageSizeLimitKiloBytes = 200;
	
	/**
	 * Getter for {@link #imageSizeLimitKiloBytes}
	 * @return the {@link #imageSizeLimitKiloBytes}
	 */
	public int getImageSizeLimitKiloBytes() {
		return imageSizeLimitKiloBytes;
	}

	/**
	 * Setter for {@link #imageSizeLimitKiloBytes}
	 * @param imageSizeLimitKiloBytes the {@link #imageSizeLimitKiloBytes} to set
	 */
	public void setImageSizeLimitKiloBytes(int imageSizeLimitKiloBytes) {
		this.imageSizeLimitKiloBytes = imageSizeLimitKiloBytes;
	}

	/**
	 * The path to store the poll images
	 */
	private String pollImagePath = "poll-images";
	
	/**
	 * Getter for {@link #pollImagePath}
	 * @return the {@link #pollImagePath}
	 */
	public String getPollImagePath() {
		return pollImagePath;
	}

	/**
	 * Setter for {@link #pollImagePath}
	 * @param pollImagePath the {@link #pollImagePath} to set
	 */
	public void setPollImagePath(String pollImagePath) {
		this.pollImagePath = pollImagePath;
	}

	/**
	 * The path to store the user images
	 */
	private String userImagePath = "user-images";

	/**
	 * Getter for {@link #userImagePath}
	 * @return the {@link #userImagePath}
	 */
	public String getUserImagePath() {
		return userImagePath;
	}

	/**
	 * Setter for {@link #userImagePath}
	 * @param userImagePath the {@link #userImagePath} to set
	 */
	public void setUserImagePath(String userImagePath) {
		this.userImagePath = userImagePath;
	}
	
	
}
