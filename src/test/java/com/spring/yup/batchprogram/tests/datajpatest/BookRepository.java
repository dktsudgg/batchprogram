package com.spring.yup.batchprogram.tests.datajpatest;

import com.spring.yup.batchprogram.tests.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
