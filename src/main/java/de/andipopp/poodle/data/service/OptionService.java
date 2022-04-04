package de.andipopp.poodle.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OptionService {

    private final OptionRepository repository;

    @Autowired
    public OptionService(OptionRepository repository) {
        this.repository = repository;
    }

    public int count() {
        return (int) repository.count();
    }

}
