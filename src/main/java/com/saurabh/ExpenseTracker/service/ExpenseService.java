package com.saurabh.ExpenseTracker.service;

import com.saurabh.ExpenseTracker.dto.ExpenseRequest;
import com.saurabh.ExpenseTracker.dto.ExpenseResponse;
import com.saurabh.ExpenseTracker.entity.Category;
import com.saurabh.ExpenseTracker.entity.Expense;
import com.saurabh.ExpenseTracker.entity.User;
import com.saurabh.ExpenseTracker.exception.ResourceNotFoundException;
import com.saurabh.ExpenseTracker.repository.CategoryRepository;
import com.saurabh.ExpenseTracker.repository.ExpenseRepository;
import com.saurabh.ExpenseTracker.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository) {

        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // CREATE EXPENSE
    // =========================

    public ExpenseResponse createExpense(
            ExpenseRequest request) {

        Category category =
                findCategoryById(request.getCategoryId());

        Expense expense = new Expense(
                request.getAmount(),
                request.getDate(),
                request.getDescription(),
                category
        );

        Expense savedExpense =
                expenseRepository.save(expense);

        return mapToResponse(savedExpense);
    }

    // =========================
    // GET ALL EXPENSES
    // =========================

    public Page<ExpenseResponse> getAllExpenses(
            Long categoryId,
            Pageable pageable) {

        User user = getCurrentUser();

        Page<Expense> expenses;

        if (categoryId != null) {

            // Make sure category belongs to current user
            findCategoryById(categoryId);

            expenses =
                    expenseRepository
                            .findByCategoryIdAndCategoryUser(
                                    categoryId,
                                    user,
                                    pageable
                            );

        } else {

            expenses =
                    expenseRepository
                            .findByCategoryUser(
                                    user,
                                    pageable
                            );
        }

        return expenses.map(this::mapToResponse);
    }

    // =========================
    // GET EXPENSE BY ID
    // =========================

    public ExpenseResponse getExpenseById(
            Long id) {

        Expense expense =
                findExpenseById(id);

        return mapToResponse(expense);
    }

    // =========================
    // UPDATE EXPENSE
    // =========================

    public ExpenseResponse updateExpense(
            Long id,
            ExpenseRequest request) {

        Expense expense =
                findExpenseById(id);

        Category category =
                findCategoryById(
                        request.getCategoryId()
                );

        expense.setAmount(
                request.getAmount()
        );

        expense.setDate(
                request.getDate()
        );

        expense.setDescription(
                request.getDescription()
        );

        expense.setCategory(category);

        Expense updatedExpense =
                expenseRepository.save(expense);

        return mapToResponse(updatedExpense);
    }

    // =========================
    // DELETE EXPENSE
    // =========================

    public void deleteExpense(
            Long id) {

        Expense expense =
                findExpenseById(id);

        expenseRepository.delete(expense);
    }

    // =========================
    // GET EXPENSES BY CATEGORY
    // =========================

    public List<ExpenseResponse> getExpensesByCategory(
            Long categoryId) {

        User user = getCurrentUser();

        // Make sure category belongs to current user
        findCategoryById(categoryId);

        return expenseRepository
                .findByCategoryIdAndCategoryUser(
                        categoryId,
                        user
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================
    // FIND EXPENSE
    // =========================

    private Expense findExpenseById(
            Long id) {

        User user = getCurrentUser();

        return expenseRepository
                .findByIdAndCategoryUser(
                        id,
                        user
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense not found with id: "
                                        + id
                        )
                );
    }

    // =========================
    // FIND CATEGORY
    // =========================

    private Category findCategoryById(
            Long id) {

        User user = getCurrentUser();

        return categoryRepository
                .findByIdAndUser(
                        id,
                        user
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: "
                                        + id
                        )
                );
    }

    // =========================
    // CURRENT USER
    // =========================

    private User getCurrentUser() {

        String username =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: "
                                        + username
                        )
                );
    }

    // =========================
    // RESPONSE MAPPER
    // =========================

    private ExpenseResponse mapToResponse(
            Expense expense) {

        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getDate(),
                expense.getDescription(),
                expense.getCategory().getId(),
                expense.getCategory().getName()
        );
    }
}