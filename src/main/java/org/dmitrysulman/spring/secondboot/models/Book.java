package org.dmitrysulman.spring.secondboot.models;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @NotEmpty(message = "Name should not be empty")
    @Size(min = 3, max = 200, message = "Name should be between 3 and 200")
    @Column(name = "title")
    private String title;

    @NotEmpty(message = "Author should not be empty")
    @Size(min = 3, max = 400, message = "Author should be between 3 and 400")
    @Column(name = "author")
    private String author;

    @Min(value = 1900, message = "Year of publication should be greater than 1899")
    @Column(name = "year")
    private int year;

    @Column(name = "date_taken")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateTaken;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "person_id")
    private Person person;

    public Book(int id, String title, String author, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
    }

    @Transient
    private Boolean isBookOverdue;

    public Book() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(Date dateTaken) {
        this.dateTaken = dateTaken;
    }


    public Boolean getBookOverdue() {
        return isBookOverdue;
    }

    public void setBookOverdue(Boolean bookOverdue) {
        isBookOverdue = bookOverdue;
    }
}
