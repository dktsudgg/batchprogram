package com.spring.yup.batchprogram.web.repository;

import com.spring.yup.batchprogram.web.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
