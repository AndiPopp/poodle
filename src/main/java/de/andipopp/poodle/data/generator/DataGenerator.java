package de.andipopp.poodle.data.generator;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vaadin.flow.spring.annotation.SpringComponent;

import de.andipopp.poodle.data.Role;
import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.DateOption;
import de.andipopp.poodle.data.entity.polls.DatePoll;
import de.andipopp.poodle.data.entity.polls.Vote;
import de.andipopp.poodle.data.service.OptionRepository;
import de.andipopp.poodle.data.service.PollRepository;
import de.andipopp.poodle.data.service.UserRepository;
import de.andipopp.poodle.data.service.VoteRepository;

@SpringComponent
public class DataGenerator {

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

            logger.info("Generating demo data");

            logger.info("... generating 2 User entities...");
            User user = new User();
            user.setName("John Normal");
            user.setUsername("user");
            user.setHashedPassword(passwordEncoder.encode("user"));
            user.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            user.setRoles(Collections.singleton(Role.USER));
            userRepository.save(user);
            User admin = new User();
            admin.setName("Emma Powerful");
            admin.setUsername("admin");
            admin.setHashedPassword(passwordEncoder.encode("admin"));
            admin.setProfilePictureUrl(
                    "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            admin.setRoles(Set.of(Role.USER, Role.ADMIN));
            userRepository.save(admin);

            //polls
            populatePollRepository(logger,pollRepository, optionRepository, voteRepository, user, admin);
            
            logger.info("Generated demo data");
        };
    }
    
    private void populatePollRepository(
    		Logger logger,
    		PollRepository pollRepository,
    		OptionRepository optionRepository,
    		VoteRepository voteRepository,
    		User user, 
    		User admin) {
    	DatePoll poll = new DatePoll();
    	poll.setTitle("Master of the Universe get-together");
    	poll.setDescription("He-Man will be there. So <a href=\"https://www.google.com\">google</a> yourself your finest magic sword and feline steed.");
    	poll.setLocation("Castle Greyskull (unless specified otherwise)");
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
    	poll.addOption(new DateOption(
    			"Option 7", 
    			new GregorianCalendar(2022, 5-1, 16, 15, 15).getTime(), 
    			new GregorianCalendar(2022, 5-1, 16, 16, 45).getTime(),
    			null 
    		));
    	NameGenerator.addRandomVotes(poll, 12);
    	Vote<?,?> vote = NameGenerator.addRandomVote(poll);
    	vote.setOwner(user);
    	vote.setDisplayName(user.getName());
    	vote = NameGenerator.addRandomVote(poll);
    	vote.setOwner(admin);
    	vote.setDisplayName(admin.getName());
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
    	NameGenerator.addRandomVote(poll);
    	pollRepository.save(poll);
    	
    	poll = new DatePoll();
    	poll.setTitle("This is a really long title just to see what happens if we have a really long title and even longer titles");
    	poll.setDescription("Man this title is long");
    	poll.setOwner(user);
    	poll.addOption(new DateOption(
    			new GregorianCalendar(2022, 1-1, 1, 8, 0).getTime(), 
    			new GregorianCalendar(2022, 1-1, 1, 10, 0).getTime()
    		));
    	pollRepository.save(poll);
    	
    	poll = new DatePoll();
    	poll.setTitle("Only for admin's eyes");
    	poll.setDescription("Only the admin should see this");
    	poll.setOwner(admin);
    	poll.addOption(new DateOption(
    			new GregorianCalendar(2022, 1-1, 1, 8, 0).getTime(), 
    			new GregorianCalendar(2022, 1-1, 1, 10, 0).getTime()
    		));
    	pollRepository.save(poll);
    	
    	try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	logger.info("Created " + pollRepository.count() + " example polls with " 
    			+ optionRepository.count() + " options and "
    			+ voteRepository.count() + " votes.");
    	
    }

}