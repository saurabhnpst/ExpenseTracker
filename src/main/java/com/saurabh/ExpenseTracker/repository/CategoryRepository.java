package com.saurabh.ExpenseTracker.repository;

import com.saurabh.ExpenseTracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}