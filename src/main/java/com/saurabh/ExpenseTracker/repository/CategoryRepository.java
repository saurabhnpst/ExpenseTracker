package com.saurabh.ExpenseTracker.repository;

import com.saurabh.ExpenseTracker.entity.Category;
import com.saurabh.ExpenseTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    List<Category> findByUser(User user);

    Optional<Category> findByIdAndUser(
            Long id,
            User user
    );

    boolean existsByNameAndUser(
            String name,
            User user
    );

    boolean existsByNameAndUserAndIdNot(
            String name,
            User user,
            Long id
    );
}