package com.saurabh.ExpenseTracker.service;

import com.saurabh.ExpenseTracker.dto.CategoryRequest;
import com.saurabh.ExpenseTracker.dto.CategoryResponse;
import com.saurabh.ExpenseTracker.dto.MonthlySummaryResponse;
import com.saurabh.ExpenseTracker.entity.Category;
import com.saurabh.ExpenseTracker.entity.User;
import com.saurabh.ExpenseTracker.exception.ResourceNotFoundException;
import com.saurabh.ExpenseTracker.repository.CategoryRepository;
import com.saurabh.ExpenseTracker.repository.ExpenseRepository;
import com.saurabh.ExpenseTracker.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            ExpenseRepository expenseRepository,
            UserRepository userRepository) {

        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    // =========================
    // CREATE CATEGORY
    // =========================

    public CategoryResponse createCategory(
            CategoryRequest request) {

        User user = getCurrentUser();

        // Same user cannot create duplicate category
        if (categoryRepository.existsByNameAndUser(
                request.getName(),
                user
        )) {
            throw new IllegalArgumentException(
                    "Category already exists for this user."
            );
        }

        Category category = new Category(
                request.getName(),
                request.getBudgetLimit(),
                user
        );

        Category savedCategory =
                categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    // =========================
    // GET ALL CATEGORIES
    // =========================

    public List<CategoryResponse> getAllCategories() {

        User user = getCurrentUser();

        return categoryRepository
                .findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =========================
    // GET CATEGORY BY ID
    // =========================

    public CategoryResponse getCategoryById(
            Long id) {

        Category category =
                findCategoryById(id);

        return mapToResponse(category);
    }

    // =========================
    // UPDATE CATEGORY
    // =========================

    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        User user = getCurrentUser();

        Category category =
                findCategoryById(id);

        // Prevent duplicate name for same user
        if (categoryRepository
                .existsByNameAndUserAndIdNot(
                        request.getName(),
                        user,
                        id
                )) {

            throw new IllegalArgumentException(
                    "Category already exists for this user."
            );
        }

        category.setName(
                request.getName()
        );

        category.setBudgetLimit(
                request.getBudgetLimit()
        );

        Category updatedCategory =
                categoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }

    // =========================
    // DELETE CATEGORY
    // =========================

    public void deleteCategory(Long id) {

        Category category =
                findCategoryById(id);

        boolean hasExpenses =
                !expenseRepository
                        .findByCategoryId(id)
                        .isEmpty();

        if (hasExpenses) {
            throw new IllegalStateException(
                    "Cannot delete category because it has existing expenses."
            );
        }

        categoryRepository.delete(category);
    }

    // =========================
    // FIND CATEGORY
    // =========================

    private Category findCategoryById(
            Long id) {

        User user = getCurrentUser();

        return categoryRepository
                .findByIdAndUser(id, user)
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

    private CategoryResponse mapToResponse(
            Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getBudgetLimit()
        );
    }

    // =========================
    // MONTHLY SUMMARY
    // =========================

    public MonthlySummaryResponse getMonthlySummary(
            Long categoryId,
            YearMonth month) {

        Category category =
                findCategoryById(categoryId);

        LocalDate startDate =
                month.atDay(1);

        LocalDate endDate =
                month.plusMonths(1).atDay(1);

        BigDecimal monthlyTotal =
                expenseRepository.calculateMonthlyTotal(
                        categoryId,
                        startDate,
                        endDate
                );

        boolean budgetExceeded =
                monthlyTotal.compareTo(
                        category.getBudgetLimit()
                ) > 0;

        return new MonthlySummaryResponse(
                category.getId(),
                category.getName(),
                month.toString(),
                monthlyTotal,
                category.getBudgetLimit(),
                budgetExceeded
        );
    }
}