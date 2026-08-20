package com.saurabh.ExpenseTracker;

import com.saurabh.ExpenseTracker.dto.MonthlySummaryResponse;
import com.saurabh.ExpenseTracker.entity.Category;
import com.saurabh.ExpenseTracker.entity.User;
import com.saurabh.ExpenseTracker.repository.CategoryRepository;
import com.saurabh.ExpenseTracker.repository.ExpenseRepository;
import com.saurabh.ExpenseTracker.repository.UserRepository;
import com.saurabh.ExpenseTracker.service.CategoryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    private CategoryService categoryService;

    private User user;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        categoryService = new CategoryService(
                categoryRepository,
                expenseRepository,
                userRepository
        );

        user = new User();

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(authentication.getName())
                .thenReturn("testuser");

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));
    }

    @Test
    void shouldFlagBudgetExceededWhenMonthlyTotalIsGreaterThanBudget() {

        // Arrange
        Category category = new Category(
                "Food",
                new BigDecimal("10000"),
                user
        );

        when(categoryRepository.findByIdAndUser(1L, user))
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

        assertTrue(
                response.isBudgetExceeded()
        );
    }

    @Test
    void shouldNotFlagBudgetExceededWhenMonthlyTotalIsLessThanBudget() {

        // Arrange
        Category category = new Category(
                "Food",
                new BigDecimal("10000"),
                user
        );

        when(categoryRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(category));

        when(expenseRepository.calculateMonthlyTotal(
                1L,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 9, 1)
        )).thenReturn(new BigDecimal("8000"));

        // Act
        MonthlySummaryResponse response =
                categoryService.getMonthlySummary(
                        1L,
                        YearMonth.of(2026, 8)
                );

        // Assert
        assertEquals(
                new BigDecimal("8000"),
                response.getMonthlyTotal()
        );

        assertEquals(
                new BigDecimal("10000"),
                response.getBudgetLimit()
        );

        assertFalse(
                response.isBudgetExceeded()
        );
    }

    @Test
    void shouldNotFlagBudgetExceededWhenMonthlyTotalEqualsBudget() {

        // Arrange
        Category category = new Category(
                "Food",
                new BigDecimal("10000"),
                user
        );

        when(categoryRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(category));

        when(expenseRepository.calculateMonthlyTotal(
                1L,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 9, 1)
        )).thenReturn(new BigDecimal("10000"));

        // Act
        MonthlySummaryResponse response =
                categoryService.getMonthlySummary(
                        1L,
                        YearMonth.of(2026, 8)
                );

        // Assert
        assertEquals(
                new BigDecimal("10000"),
                response.getMonthlyTotal()
        );

        assertEquals(
                new BigDecimal("10000"),
                response.getBudgetLimit()
        );

        assertFalse(
                response.isBudgetExceeded()
        );
    }

    @Test
    void shouldNotFlagBudgetExceededWhenThereAreNoExpenses() {

        // Arrange
        Category category = new Category(
                "Food",
                new BigDecimal("10000"),
                user
        );

        when(categoryRepository.findByIdAndUser(1L, user))
                .thenReturn(Optional.of(category));

        when(expenseRepository.calculateMonthlyTotal(
                1L,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 9, 1)
        )).thenReturn(BigDecimal.ZERO);

        // Act
        MonthlySummaryResponse response =
                categoryService.getMonthlySummary(
                        1L,
                        YearMonth.of(2026, 8)
                );

        // Assert
        assertEquals(
                BigDecimal.ZERO,
                response.getMonthlyTotal()
        );

        assertEquals(
                new BigDecimal("10000"),
                response.getBudgetLimit()
        );

        assertFalse(
                response.isBudgetExceeded()
        );
    }
}