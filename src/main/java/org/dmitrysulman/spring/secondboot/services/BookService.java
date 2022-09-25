package org.dmitrysulman.spring.secondboot.services;

import org.dmitrysulman.spring.secondboot.models.Book;
import org.dmitrysulman.spring.secondboot.models.Person;
import org.dmitrysulman.spring.secondboot.repositories.BookRepository;
import org.dmitrysulman.spring.secondboot.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final PersonRepository personRepository;

    @Autowired
    public BookService(BookRepository bookRepository, PersonRepository personRepository) {
        this.bookRepository = bookRepository;
        this.personRepository = personRepository;
    }

    public Page<Book> findAll(Integer page, Integer booksPerPage, Boolean sortByYear) {
        Sort sort = Sort.unsorted();
        if (Objects.nonNull(sortByYear) && sortByYear) {
            sort = Sort.by("year");
        }
        if (Objects.isNull(page) && Objects.isNull(booksPerPage) && Objects.isNull(sortByYear)) {
            return bookRepository.findAll(Pageable.unpaged());
        }
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(booksPerPage)) {
            booksPerPage = 10;
        }
        return bookRepository.findAll(PageRequest.of(page, booksPerPage, sort));
    }

    public Optional<Book> findOne(int id) {
        return bookRepository.findById(id);
    }

    @Transactional
    public int save(Book book) {
        book = bookRepository.save(book);
        return book.getId();
    }

    @Transactional
    public Boolean update(int id, Book book) {
        if (!bookRepository.existsById(id)) {
            return false;
        } else {
            book.setId(id);
            bookRepository.save(book);
        }
        return true;
    }

    @Transactional
    public Boolean delete(int id) {
        if (!bookRepository.existsById(id)) {
            return false;
        } else {
            bookRepository.deleteById(id);
        }
        return true;
    }

    @Transactional
    public Boolean assign(int bookId, int personId) {
        Optional<Book> book = bookRepository.findById(bookId);
        if (book.isEmpty()) {
            return false;
        } else {
            Optional<Person> personToAssign = personRepository.findById(personId);
            if (personToAssign.isEmpty()) {
                return false;
            } else {
                book.get().setPerson(personToAssign.get());
                book.get().setDateTaken(new Date());
                bookRepository.save(book.get());
            }
        }
        return true;
    }

    @Transactional
    public Boolean release(int id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            return false;
        } else {
            book.get().setPerson(null);
            book.get().setDateTaken(null);
            bookRepository.save(book.get());
        }
        return true;
    }

    public List<Book> findByTitleStartingWith(String title) {
        return bookRepository.findByTitleStartingWithIgnoreCase(title);
    }
}
