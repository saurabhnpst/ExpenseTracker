package com.saurabh.ExpenseTracker.repository;

import com.saurabh.ExpenseTracker.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByCategoryId(Long categoryId);
    Page<Expense> findByCategoryId(Long categoryId, Pageable pageable);
}