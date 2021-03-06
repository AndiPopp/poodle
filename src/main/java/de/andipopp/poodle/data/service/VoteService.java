package de.andipopp.poodle.data.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.andipopp.poodle.data.entity.User;
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
    	repository.delete(vote);
    }
    
    public void delete(UUID id) {
    	repository.deleteById(id);
    }
    
    public void orphenateVotes(User user) {
    	for(Vote<?,?> vote : findByOwner(user)) {
    		//double-check
    		if (vote.getOwner().equals(user)) {
    			vote.setOwner(null);
    			update(vote);
    		}
    	}
    }
    
    public List<Vote<?,?>> findByOwner(User user) {
    	return repository.findByOwner(user);
    }
}

