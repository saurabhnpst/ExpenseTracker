package com.saurabh.ExpenseTracker.repository;

import com.saurabh.ExpenseTracker.entity.Expense;
import com.saurabh.ExpenseTracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByCategoryId(Long categoryId);

    Page<Expense> findByCategoryId(
            Long categoryId,
            Pageable pageable
    );

    // All expenses belonging to current user's categories
    Page<Expense> findByCategoryUser(
            User user,
            Pageable pageable
    );

    // One expense only if it belongs to current user
    Optional<Expense> findByIdAndCategoryUser(
            Long id,
            User user
    );

    // Category expenses for current user
    List<Expense> findByCategoryIdAndCategoryUser(
            Long categoryId,
            User user
    );

    // Category expenses for current user with pagination
    Page<Expense> findByCategoryIdAndCategoryUser(
            Long categoryId,
            User user,
            Pageable pageable
    );

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