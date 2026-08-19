package com.saurabh.ExpenseTracker;

import com.saurabh.ExpenseTracker.dto.MonthlySummaryResponse;
import com.saurabh.ExpenseTracker.entity.Category;
import com.saurabh.ExpenseTracker.repository.CategoryRepository;
import com.saurabh.ExpenseTracker.repository.ExpenseRepository;
import com.saurabh.ExpenseTracker.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertFalse;


import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        categoryService = new CategoryService(
                categoryRepository,
                expenseRepository
        );
    }

    @Test
    void shouldFlagBudgetExceededWhenMonthlyTotalIsGreaterThanBudget() {

        // Arrange
        Category category = new Category(
                "Food",
                new BigDecimal("10000")
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(expenseRepository.calculateMonthlyTotal(
                1L,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 9, 1)
        )).thenReturn(new BigDecimal("12000"));

        // Act
        MonthlySummaryResponse response =
                categoryService.getMonthlySummary(
                        1L,
                        YearMonth.of(2026, 8)
                );

        // Assert
        assertEquals(
                new BigDecimal("12000"),
                response.getMonthlyTotal()
        );

        assertEquals(
                new BigDecimal("10000"),
                response.getBudgetLimit()
        );

        assertTrue(response.isBudgetExceeded());
    }

    @Test
    void shouldNotFlagBudgetExceededWhenMonthlyTotalIsLessThanBudget() {

        Category category = new Category(
                "Food",
                new BigDecimal("10000")
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(expenseRepository.calculateMonthlyTotal(
                1L,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 9, 1)
        )).thenReturn(new BigDecimal("8000"));

        MonthlySummaryResponse response =
                categoryService.getMonthlySummary(
                        1L,
                        YearMonth.of(2026, 8)
                );

        assertEquals(
                new BigDecimal("8000"),
                response.getMonthlyTotal()
        );

        assertEquals(
                new BigDecimal("10000"),
                response.getBudgetLimit()
        );

        assertFalse(response.isBudgetExceeded());
    }

    @Test
    void shouldNotFlagBudgetExceededWhenMonthlyTotalEqualsBudget() {

        Category category = new Category(
                "Food",
                new BigDecimal("10000")
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(expenseRepository.calculateMonthlyTotal(
                1L,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 9, 1)
        )).thenReturn(new BigDecimal("10000"));

        MonthlySummaryResponse response =
                categoryService.getMonthlySummary(
                        1L,
                        YearMonth.of(2026, 8)
                );

        assertEquals(
                new BigDecimal("10000"),
                response.getMonthlyTotal()
        );

        assertEquals(
                new BigDecimal("10000"),
                response.getBudgetLimit()
        );

        assertFalse(response.isBudgetExceeded());
    }

    @Test
    void shouldReturnZeroAndNotExceedBudgetWhenThereAreNoExpenses() {

        Category category = new Category(
                "Food",
                new BigDecimal("10000")
        );

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(expenseRepository.calculateMonthlyTotal(
                1L,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 9, 1)
        )).thenReturn(BigDecimal.ZERO);

        MonthlySummaryResponse response =
                categoryService.getMonthlySummary(
                        1L,
                        YearMonth.of(2026, 8)
                );

        assertEquals(
                BigDecimal.ZERO,
                response.getMonthlyTotal()
        );

        assertEquals(
                new BigDecimal("10000"),
                response.getBudgetLimit()
        );

        assertFalse(response.isBudgetExceeded());
    }

}