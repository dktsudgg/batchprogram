package com.spring.yup.batchprogram.tests.webmvctest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bookstore")
public class BookController {

    @Autowired
    BookService bookService;

    @GetMapping("/books")
    public String getBookList(Model model){
        model.addAttribute("bookList", bookService.getBookList());
        return "books";
    }
}
