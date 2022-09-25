package org.dmitrysulman.spring.secondboot.services;

import org.dmitrysulman.spring.secondboot.models.Book;
import org.dmitrysulman.spring.secondboot.models.Person;
import org.dmitrysulman.spring.secondboot.repositories.PersonRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Optional<Person> findOne(int id) {
        return personRepository.findById(id);
    }

    public Optional<Person> findByFullName(String fullName) {
        return personRepository.findByFullName(fullName);
    }

    @Transactional
    public int save(Person person) {
        person = personRepository.save(person);
        return person.getId();
    }

    @Transactional
    public Boolean update(int id, Person person) {
        if (!personRepository.existsById(id)) {
            return false;
        } else {
            person.setId(id);
            personRepository.save(person);
        }
        return true;
    }

    @Transactional
    public Boolean delete(int id) {
        if (!personRepository.existsById(id)) {
            return false;
        } else {
            personRepository.deleteById(id);
        }
        return true;
    }

    public List<Book> getBooksOfPersonById(int id) {
        Optional<Person> person = personRepository.findById(id);
        if (person.isPresent()) {
            Hibernate.initialize(person.get().getBooks());
            person.get().getBooks().forEach(
                    book -> book.setBookOverdue(
                            book.getDateTaken().before(Date.from(LocalDateTime.now().minusDays(10).toInstant(ZoneOffset.UTC)))
                    )
            );
            return person.get().getBooks();
        } else {
            return Collections.emptyList();
        }
    }
}
