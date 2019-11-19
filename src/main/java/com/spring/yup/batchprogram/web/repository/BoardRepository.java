package com.spring.yup.batchprogram.web.repository;

import com.spring.yup.batchprogram.web.domain.Board;
import com.spring.yup.batchprogram.web.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Board findByUser(User user);
}
