package de.andipopp.poodle.data.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.andipopp.poodle.data.entity.polls.Vote;

@Service
public class VoteService {

    private final VoteRepository repository;

    @Autowired
    public VoteService(VoteRepository repository) {
        this.repository = repository;
    }

    public int count() {
        return (int) repository.count();
    }

    public Vote<?,?> update(Vote<?,?> vote) {
    	return repository.save(vote);
    }
    
    public void delete(Vote<?,?> vote) {
    	Logger logger = LoggerFactory.getLogger(getClass());
    	logger.info("Trying to delete vote "+vote.getId()+" from repository with "+repository.count()+" votes.");
    	repository.delete(vote);
    	logger.info("Now with "+repository.count()+" votes.");
    }
}
