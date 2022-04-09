package de.andipopp.poodle.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService {

    private final AnswerRepository repository;

    @Autowired
    public AnswerService(AnswerRepository repository) {
        this.repository = repository;
    }

    public int count() {
        return (int) repository.count();
    }

}
