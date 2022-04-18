package de.andipopp.poodle.data.entity;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.data.generator.ConfigLoader;

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
	private static Config currentConfig;
	
	/**
	 * Getter for {@link #currentConfig}
	 * @return the {@link #currentConfig}
	 */
	public static Config getCurrent() {
		if (currentConfig == null) {
			if (ConfigLoader.configService != null) {
				setCurrentConfig(ConfigLoader.configService.getDefaultConfig());
			} else {
				return new Config();
			}
		}
		return currentConfig;
	}

	/**
	 * Setter for {@link #currentConfig}
	 * @param currentConfig the {@link #currentConfig} to set
	 */
	public static void setCurrentConfig(Config currentConfig) {
		Config.currentConfig = currentConfig;
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
		return ChronoUnit.DAYS.between(LocalDate.now(), deleteDate) <= maxPollRetentionDays;
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
	
}
