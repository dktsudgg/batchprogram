package com.spring.yup.batchprogram.tests.webmvctest;

import com.spring.yup.batchprogram.tests.Book;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private List<Book> bookList;

    public BookService(){
        this.bookList = new ArrayList<Book>();
    }

    public List<Book> getBookList(){
        return this.bookList;
    }
}
