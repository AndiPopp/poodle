package de.andipopp.poodle.data.generator;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.flow.spring.annotation.SpringComponent;

import de.andipopp.poodle.data.Role;
import de.andipopp.poodle.data.entity.Config;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.data.entity.polls.Vote;
import de.andipopp.poodle.data.service.OptionRepository;
import de.andipopp.poodle.data.service.PollRepository;
import de.andipopp.poodle.data.service.UserRepository;
import de.andipopp.poodle.data.service.VoteRepository;
import de.andipopp.poodle.util.UUIDUtils;
import de.andipopp.poodle.views.components.ImageUpload;

@SpringComponent
public class ExampleDataGenerator {

    @Bean
    @Order(99)
    public CommandLineRunner loadData(
    		PasswordEncoder passwordEncoder, 
    		UserRepository userRepository, 
    		PollRepository pollRepository,
    		OptionRepository optionRepository,
    		VoteRepository voteRepository
    ) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }

            cleanAvatarFolders();
            
            logger.info("Generating demo data");

            logger.info("... generating 2 User entities...");
            User user = new User();
            user.setDisplayName("John Normal");
            user.setUsername("user");
            user.setHashedPassword(passwordEncoder.encode("user"));
            user.setRoles(Collections.singleton(Role.USER));
            user.setZoneId("Japan");
            user.addIcsPath("https://calendar.google.com/calendar/ical/en-gb.german%23holiday%40group.v.calendar.google.com/public/basic.ics");
            user.addIcsPath("https://raw.githubusercontent.com/AndiPopp/poodle/main/examples/johns_calendar.ics");
            user = userRepository.save(user);
            File userAvatar = new File(Config.getCurrent().getUserImagePath() + System.getProperty("file.separator") + "john.png");
            File userAvatarCopy = new File(Config.getCurrent().getUserImagePath() + System.getProperty("file.separator") + UUIDUtils.uuidToBase64url(user.getId()) + ImageUpload.FILE_EXTENSION);
            FileUtils.copyFile(userAvatar, userAvatarCopy);
            
            User admin = new User();
            admin.setDisplayName("Emma Powerful");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));
            admin = userRepository.save(admin);

            File adminAvatar = new File(Config.getCurrent().getUserImagePath() + System.getProperty("file.separator") + "emma.png");
            File adminAvatarCopy = new File(Config.getCurrent().getUserImagePath() + System.getProperty("file.separator") + UUIDUtils.uuidToBase64url(admin.getId()) + ImageUpload.FILE_EXTENSION);
            FileUtils.copyFile(adminAvatar, adminAvatarCopy);
            
            //polls
            populatePollRepository(logger,pollRepository, optionRepository, voteRepository, user, admin);
            
            logger.info("Generated demo data");
        };
    }
    
    private void cleanAvatarFolders() {
    	File pollImageFolder = new File(Config.getCurrent().getPollImagePath());
    	File userImageFolder = new File(Config.getCurrent().getUserImagePath());
    	cleanImageFolder(pollImageFolder);
    	cleanImageFolder(userImageFolder);
    	
    }
    
    private void cleanImageFolder(File folder) {
    	if (folder.isDirectory()) {
    		for(File file : folder.listFiles()) {
    			if (file.getName().length()==26) file.delete();
    		}
    	}
    }
    
    private void populatePollRepository(
    		Logger logger,
    		PollRepository pollRepository,
    		OptionRepository optionRepository,
    		VoteRepository voteRepository,
    		User user, 
    		User admin) throws IOException {
    	
    	DatePoll poll = new DatePoll("Example Poll", "Description", "Location");
    	pollRepository.save(poll);    	
    	
    	poll = new DatePoll();
    	poll.setTitle("Masters of the Universe get-together");
    	poll.setDescription("He-Man will be there. So <a href=\"https://www.google.com\">google</a> yourself your finest magic sword and feline steed.");
    	poll.setLocation("Castle Greyskull (unless specified otherwise)");
    	poll.generateEditKey();
    	poll.setOwner(user);
    	poll.setEnableIfNeedBe(true);
    	poll.addOption(new DateOption(
				"Option 1", 
				new GregorianCalendar(2022, 2-1, 28, 9, 30).getTime(), 
				new GregorianCalendar(2022, 2-1, 28, 11, 0).getTime(), 
				"Snake Mountain"
			));
    	poll.addOption(new DateOption(
    			"Option 2", 
				new GregorianCalendar(2022, 2-1, 28, 15, 0).getTime(), 
				new GregorianCalendar(2022, 2-1, 28, 16, 30).getTime(), 
				"at Man-at-Arms's"
			));
    	poll.addOption(new DateOption(
    			"Option 7", 
    			new GregorianCalendar(2022, 5-1, 31, 22, 15).getTime(), 
    			new GregorianCalendar(2022, 5-1, 31, 23, 45).getTime(),
    			null 
    		));
    	poll.addOption(new DateOption(
    			"Option 3", 
    			new GregorianCalendar(2022, 4-1, 11, 8, 0).getTime(), 
    			new GregorianCalendar(2022, 4-1, 11, 10, 0).getTime(),
    			null
    		));
    	poll.addOption(new DateOption(
    			"Option 4", 
    			new GregorianCalendar(2022, 4-1, 11, 15, 15).getTime(), 
    			new GregorianCalendar(2022, 4-1, 12, 16, 45).getTime(),
    			null 
    		));
    	poll.addOption(new DateOption(
    			"Option 5", 
    			new GregorianCalendar(2022, 5-1, 12, 15, 15).getTime(), 
    			new GregorianCalendar(2022, 5-1, 13, 10, 45).getTime(),
    			null 
    		));
    	poll.addOption(new DateOption(
    			"Option 6", 
    			new GregorianCalendar(2022, 5-1, 13, 15, 15).getTime(), 
    			new GregorianCalendar(2022, 5-1, 13, 16, 45).getTime(),
    			null 
    		));
    	
    	NameGenerator.addRandomVotes(poll, 12);
    	Vote<?,?>  vote = NameGenerator.addRandomVote(poll);
    	vote.setOwner(user);
    	vote.setDisplayName(user.getDisplayName());
    	vote = NameGenerator.addRandomVote(poll);
    	vote.setOwner(admin);
    	vote.setDisplayName(admin.getDisplayName());
    	pollRepository.save(poll);
    	
    	poll = new DatePoll();
    	poll.setTitle("Becoming Hokage");
    	poll.setLocation("Konohagekure");
    	poll.setOwner(user);
    	poll.addOption(new DateOption(
    			new GregorianCalendar(2022, 11-1, 23, 9, 10).getTime(), 
    			new GregorianCalendar(2022, 11-1, 23, 10, 40).getTime()
    		));
    	poll.addOption(new DateOption(
    			new GregorianCalendar(2022, 11-1, 23, 19, 10).getTime(), 
    			new GregorianCalendar(2022, 11-1, 23, 20, 40).getTime()
    		));
    	NameGenerator.addRandomVotes(poll, 2);
    	poll = pollRepository.save(poll);
    	
    	File pollAvatar = new File(Config.getCurrent().getPollImagePath() + System.getProperty("file.separator") + "Hokage.drawio.png");
        File pollAvatarCopy = new File(Config.getCurrent().getPollImagePath() + System.getProperty("file.separator") + UUIDUtils.uuidToBase64url(poll.getId()) + ImageUpload.FILE_EXTENSION);
        FileUtils.copyFile(pollAvatar, pollAvatarCopy);
    	
    	
    	poll = new DatePoll();
    	poll.setTitle("This is a really long title just to see what happens when we actually hit the 80");
    	poll.setDescription("Lorem ipsum dolor sit amet, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. \n"
    			+ "</br>"
    			+ "Duis autem vel eum iriure dolor in hendrerit in vulputate velit esset, vel illum dolore eu feugiat nulla facilisis ");
    	poll.setOwner(user);
    	poll.generateEditKey();
    	poll.addOption(new DateOption(
    			new GregorianCalendar(2022, 1-1, 1, 8, 0).getTime(), 
    			new GregorianCalendar(2022, 1-1, 1, 10, 0).getTime()
    		));
    	pollRepository.save(poll);
    	
    	poll = new DatePoll();
    	poll.setTitle("This belongs to admin");
    	poll.setOwner(admin);
    	poll.generateEditKey();
    	poll.addOption(new DateOption(
			new GregorianCalendar(2022, 1-1, 1, 8, 0).getTime(), 
			new GregorianCalendar(2022, 1-1, 1, 10, 0).getTime()
		));
    	vote = NameGenerator.addRandomVote(poll);
    	vote.setOwner(user);
    	vote.setDisplayName(user.getDisplayName());
    	pollRepository.save(poll);
    	

    	
    	
    	logger.info("Created " + pollRepository.count() + " example polls with " 
    			+ optionRepository.count() + " options and "
    			+ voteRepository.count() + " votes.");
    	
    }

}