package de.andipopp.poodle.data.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.andipopp.poodle.data.entity.Config;

@Service
public class ConfigService {

    private final ConfigRepository repository;

    @Autowired
    public ConfigService(ConfigRepository repository) {
        this.repository = repository;
    }

    public int count() {
        return (int) repository.count();
    }

    public Config getDefaultConfig() {
    	if (repository.existsById(Config.DEFAULT_CONFIG_ID)) {
    		return repository.getById(Config.DEFAULT_CONFIG_ID);
    	}else {
    		Config newDefaultConfig = new Config();
    		repository.save(newDefaultConfig);
    		return newDefaultConfig; 
    	}
    }
    
    public boolean hasConfig(UUID id) {
    	return false;
    }
    
    public Config save(Config config) {
    	return repository.save(config);
    }
}
