package org.dmitrysulman.spring.secondboot.controllers;

import org.dmitrysulman.spring.secondboot.models.Person;
import org.dmitrysulman.spring.secondboot.services.PersonService;
import org.dmitrysulman.spring.secondboot.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PersonService personService;
    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PersonService personService, PersonValidator personValidator) {
        this.personService = personService;
        this.personValidator = personValidator;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", personService.findAll());

        return "people/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) {
        Optional<Person> person = personService.findOne(id);
        if (person.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        } else {
            model.addAttribute("person", person.get());
            model.addAttribute("books", personService.getBooksOfPersonById(person.get().getId()));
        }

        return "people/show";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("person") Person person) {
        person.setYearOfBirth(2000);

        return "people/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            return "people/add";
        }
        int id = personService.save(person);

        return "redirect:/people/" + id;
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {
        Optional<Person> person = personService.findOne(id);
        if (person.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        } else {
            model.addAttribute("person", person.get());
        }

        return "people/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") int id, @ModelAttribute("person") @Valid Person person, BindingResult bindingResult) {
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()) {
            return "people/edit";
        }
        if (!personService.update(id, person)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        return "redirect:/people/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") int id) {
        if (!personService.delete(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request");
        }

        return "redirect:/people/";
    }
}
