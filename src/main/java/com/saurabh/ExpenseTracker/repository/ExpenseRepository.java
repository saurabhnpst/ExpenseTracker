package com.saurabh.ExpenseTracker.repository;

import com.saurabh.ExpenseTracker.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Get all expenses belonging to a category
    List<Expense> findByCategoryId(Long categoryId);

    // Get category expenses with pagination
    Page<Expense> findByCategoryId(
            Long categoryId,
            Pageable pageable
    );

    // Calculate total expenses for a category within a date range
    @Query("""
            SELECT COALESCE(SUM(e.amount), 0)
            FROM Expense e
            WHERE e.category.id = :categoryId
            AND e.date >= :startDate
            AND e.date < :endDate
            """)
    BigDecimal calculateMonthlyTotal(
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}