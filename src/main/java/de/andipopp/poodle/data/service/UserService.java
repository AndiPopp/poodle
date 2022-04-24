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
import de.andipopp.poodle.util.InvalidException;

@Service
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public Optional<User> get(UUID id) {
        return repository.findById(id);
    }
    
    public User get(String userName) {
    	return repository.findByUsername(userName);
    }

    public User update(User entity) throws InvalidException {
    	User other = repository.findByUsername(entity.getUsername());
    	if (other != null && !other.equals(entity)) throw new InvalidException("User name already in use!");
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<User> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<User> getAll() {
    	return repository.findAll();
    }
    
    public List<User> filter(String userNameFilter, String displayNameFilter) {
    	List<User> users = getAll();
    	List<User> results = new LinkedList<>();
    	for(User user : users) {
    		if (user.getUsername().toLowerCase().contains(userNameFilter.toLowerCase())
    				&& user.getDisplayName().toLowerCase().contains(displayNameFilter.toLowerCase())) {
    			results.add(user);
    		}
    	}
    	return results;
    }
    
    public int count() {
        return (int) repository.count();
    }

}
