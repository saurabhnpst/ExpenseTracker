
package com.saurabh.ExpenseTracker.service;

import com.saurabh.ExpenseTracker.dto.ExpenseFilterRequest;
import com.saurabh.ExpenseTracker.dto.ExpenseRequest;
import com.saurabh.ExpenseTracker.dto.ExpenseResponse;
import com.saurabh.ExpenseTracker.entity.Category;
import com.saurabh.ExpenseTracker.entity.Expense;
import com.saurabh.ExpenseTracker.entity.User;
import com.saurabh.ExpenseTracker.event.MonthlySummaryInvalidationEvent;
import com.saurabh.ExpenseTracker.exception.ResourceNotFoundException;
import com.saurabh.ExpenseTracker.repository.CategoryRepository;
import com.saurabh.ExpenseTracker.repository.ExpenseRepository;
import com.saurabh.ExpenseTracker.repository.UserRepository;
import com.saurabh.ExpenseTracker.specification.ExpenseSpecification;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher) {

        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    // =========================
    // CREATE EXPENSE
    // =========================

    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest request) {

        Category category =
                findCategoryById(request.getCategoryId());

        Expense expense = new Expense(
                request.getAmount(),
                request.getDate(),
                request.getDescription(),
                category
        );

        Expense savedExpense = expenseRepository.save(expense);

        eventPublisher.publishEvent(
                new MonthlySummaryInvalidationEvent(
                        savedExpense.getCategory().getId(),
                        YearMonth.from(savedExpense.getDate())
                )
        );

        return mapToResponse(savedExpense);
    }

    // =========================
    // GET ALL EXPENSES
    // =========================

    public Page<ExpenseResponse> getAllExpenses(
            ExpenseFilterRequest filter,
            Pageable pageable) {

        User user = getCurrentUser();

        Specification<Expense> specification =
                ExpenseSpecification.belongsToUser(user);

        if (filter.getCategoryId() != null) {
            specification = specification.and(
                    ExpenseSpecification.hasCategory(
                            filter.getCategoryId()
                    )
            );
        }

        if (filter.getFromDate() != null) {
            specification = specification.and(
                    ExpenseSpecification.dateGreaterThanOrEqualTo(
                            filter.getFromDate()
                    )
            );
        }

        if (filter.getToDate() != null) {
            specification = specification.and(
                    ExpenseSpecification.dateLessThanOrEqualTo(
                            filter.getToDate()
                    )
            );
        }

        if (filter.getMinAmount() != null) {
            specification = specification.and(
                    ExpenseSpecification.amountGreaterThanOrEqualTo(
                            filter.getMinAmount()
                    )
            );
        }

        if (filter.getMaxAmount() != null) {
            specification = specification.and(
                    ExpenseSpecification.amountLessThanOrEqualTo(
                            filter.getMaxAmount()
                    )
            );
        }

        Page<Expense> expenses =
                expenseRepository.findAll(specification, pageable);

        return expenses.map(this::mapToResponse);
    }

    // =========================
    // GET EXPENSE BY ID
    // =========================

    public ExpenseResponse getExpenseById(Long id) {

        Expense expense = findExpenseById(id);

        return mapToResponse(expense);
    }

    // =========================
    // UPDATE EXPENSE
    // =========================

    @Transactional
    public ExpenseResponse updateExpense(
            Long id,
            ExpenseRequest request) {

        Expense expense = findExpenseById(id);

        // Capture the original category and month before modification.
        Long oldCategoryId = expense.getCategory().getId();
        YearMonth oldMonth = YearMonth.from(expense.getDate());

        Category category =
                findCategoryById(request.getCategoryId());

        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        expense.setCategory(category);

        Expense updatedExpense = expenseRepository.save(expense);

        // Invalidate the original summary.
        eventPublisher.publishEvent(
                new MonthlySummaryInvalidationEvent(
                        oldCategoryId,
                        oldMonth
                )
        );

        // Invalidate the new summary.
        eventPublisher.publishEvent(
                new MonthlySummaryInvalidationEvent(
                        updatedExpense.getCategory().getId(),
                        YearMonth.from(updatedExpense.getDate())
                )
        );

        return mapToResponse(updatedExpense);
    }

    // =========================
    // DELETE EXPENSE
    // =========================

    @Transactional
    public void deleteExpense(Long id) {

        Expense expense = findExpenseById(id);

        // Capture details before deleting the expense.
        Long categoryId = expense.getCategory().getId();
        YearMonth month = YearMonth.from(expense.getDate());

        expenseRepository.delete(expense);

        eventPublisher.publishEvent(
                new MonthlySummaryInvalidationEvent(
                        categoryId,
                        month
                )
        );
    }

    // =========================
    // GET EXPENSES BY CATEGORY
    // =========================

    public List<ExpenseResponse> getExpensesByCategory(
            Long categoryId) {

        User user = getCurrentUser();

        // Verify category ownership.
        findCategoryById(categoryId);

        return expenseRepository
                .findByCategoryIdAndCategoryUser(categoryId, user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================
    // FIND EXPENSE
    // =========================

    private Expense findExpenseById(Long id) {

        User user = getCurrentUser();

        return expenseRepository
                .findByIdAndCategoryUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense not found with id: " + id
                        )
                );
    }

    // =========================
    // FIND CATEGORY
    // =========================

    private Category findCategoryById(Long id) {

        User user = getCurrentUser();

        return categoryRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id
                        )
                );
    }

    // =========================
    // CURRENT USER
    // =========================

    private User getCurrentUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + username
                        )
                );
    }

    // =========================
    // RESPONSE MAPPER
    // =========================

    private ExpenseResponse mapToResponse(Expense expense) {

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
