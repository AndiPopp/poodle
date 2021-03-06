package de.andipopp.poodle.data.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;

@Service
public class PollService {

	PollRepository repository;
	
    @Autowired
    public PollService(PollRepository repository) {
        this.repository = repository;
    }

    public Optional<AbstractPoll<?,?>> get(UUID id) {
        return repository.findById(id);
    }

    public AbstractPoll<?,?> update(AbstractPoll<?,?> entity) {
        return repository.save(entity);
    }

    public void delete(AbstractPoll<?, ?> poll) {
    	repository.delete(poll);
    }
    
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public void delete(User owner) {
    	for(AbstractPoll<?,?> poll : findByOwner(owner)) {
    		//double-check
    		if (poll.getOwner().equals(owner)) delete(poll);
    	}
    }
    
    public void orphenatePolls(User user) {
    	for(AbstractPoll<?,?> poll : findByOwner(user)) {
    		//double-check
    		if (poll.getOwner().equals(user)) {
    			poll.setOwner(null);
    			update(poll);
    		}
    	}
    }
    
    public Page<AbstractPoll<?,?>> list(Pageable pageable) {
        return repository.findAll(pageable);
    }
    
    public List<AbstractPoll<?,?>> findAll() {
    	return repository.findAll();
    }
    
    public List<AbstractPoll> findAllNewest() {
    	return repository.findAllByOrderByCreateDateDesc();
    }
    
    public List<AbstractPoll> findAllNewest(String titleFilter, Boolean closedFilter) { //TODO probably faster when done correctly in the backend
    	List<AbstractPoll> list = findAllNewest();
    	List<AbstractPoll> result = new LinkedList<AbstractPoll>();
    	for(AbstractPoll poll : list) {
    		if ((titleFilter == null || titleFilter.isEmpty() || poll.getTitle().toLowerCase().contains(titleFilter.toLowerCase()))
    				&& (closedFilter == null || poll.isClosed() == closedFilter)
    			) result.add(poll);
    	}
    	return result;
    }
    
    public List<AbstractPoll<?,?>> findByOwner(User owner) {
    	return repository.findByOwner(owner);
    }
    
    public List<AbstractPoll<?,?>> findNewestByOwner(User owner) {
    	return repository.findByOwnerOrderByCreateDateDesc(owner);
    }

    public List<AbstractPoll> findNewest(String titleFilter, String ownerFiler, Boolean closedFilter) {
    	List<AbstractPoll> list = repository.findAllByOrderByCreateDateDesc();
    	List<AbstractPoll> result = new LinkedList<AbstractPoll>();
    	for(AbstractPoll poll : list) {
    		if ((titleFilter == null || titleFilter.isBlank() || poll.getTitle().toLowerCase().contains(titleFilter.strip().toLowerCase()))
    				&& (ownerFiler == null || ownerFiler.isBlank() || (poll.getOwner() != null && poll.getOwner().getDisplayName().toLowerCase().contains(ownerFiler.strip().toLowerCase())))
    				&& (closedFilter == null || poll.isClosed() == closedFilter)
    			) result.add(poll);
    	}
    	return result;
    }
    
    public List<AbstractPoll<?,?>> findNewestByOwner(User owner, String titleFilter, Boolean closedFilter) { //TODO probably faster when done correctly in the backend
    	List<AbstractPoll<?,?>> list = findNewestByOwner(owner);
    	List<AbstractPoll<?,?>> result = new LinkedList<>();
    	for(AbstractPoll<?,?> poll : list) {
    		if ((titleFilter == null || titleFilter.isEmpty() || poll.getTitle().toLowerCase().contains(titleFilter.toLowerCase()))
    				&& (closedFilter == null || poll.isClosed() == closedFilter)
    			) result.add(poll);
    	}
    	return result;
    }
    
    public int count() {
        return (int) repository.count();
    }

	
}
