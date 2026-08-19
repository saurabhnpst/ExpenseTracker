package com.saurabh.ExpenseTracker.repository;

import com.saurabh.ExpenseTracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}