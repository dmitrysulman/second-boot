package org.dmitrysulman.spring.secondboot.controllers;

import org.dmitrysulman.spring.secondboot.models.Book;
import org.dmitrysulman.spring.secondboot.models.Person;
import org.dmitrysulman.spring.secondboot.services.BookService;
import org.dmitrysulman.spring.secondboot.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;
    private final PersonService personService;

    @Autowired
    public BooksController(BookService bookService, PersonService personService) {
        this.bookService = bookService;
        this.personService = personService;
    }

    @GetMapping()
    public String index(@RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                        @RequestParam(value = "sort_by_year", required = false) Boolean sortByYear,
                        Model model) {
        model.addAttribute("bookPage", bookService.findAll(page, booksPerPage, sortByYear));
        return "books/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model, @ModelAttribute("person") Person person) {
        Optional<Book> book = bookService.findOne(id);
        if (book.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        } else {
            Person bookOwner =  book.get().getPerson();
            if (Objects.nonNull(bookOwner)) {
                model.addAttribute("owner", bookOwner);
            } else {
                model.addAttribute("people", personService.findAll());
            }
            model.addAttribute("book", book.get());
        }

        return "books/show";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("book") Book book) {
        book.setYear(1900);

        return "books/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/add";
        }
        int id = bookService.save(book);

        return "redirect:/books/" + id;
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        Optional<Book> book = bookService.findOne(id);
        if (book.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        } else {
            model.addAttribute("book", book.get());
        }

        return "books/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") int id, @ModelAttribute("book") @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "books/edit";
        }
        if (!bookService.update(id, book)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        return "redirect:/books/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") int id) {
        if (!bookService.delete(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        return "redirect:/books/";
    }

    @PostMapping("/{id}/assign")
    public String assign(@PathVariable("id") int id, @ModelAttribute("person") Person person) {
        if (!bookService.assign(id, person.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        return "redirect:/books/" + id;
    }

    @PostMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        if (!bookService.release(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        return "redirect:/books/" + id;
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "text", required = false) String text, Model model) {
        if (Objects.nonNull(text)) {
            if (text.equals("")) {
                return "redirect:/books/search";
            }
            model.addAttribute("text", text);
            model.addAttribute("books", bookService.findByTitleStartingWith(text));
        }

        return "/books/search";
    }
}
