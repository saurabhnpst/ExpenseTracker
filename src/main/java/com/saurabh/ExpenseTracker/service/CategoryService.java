
package com.saurabh.ExpenseTracker.service;

import com.saurabh.ExpenseTracker.dto.CategoryRequest;
import com.saurabh.ExpenseTracker.dto.CategoryResponse;
import com.saurabh.ExpenseTracker.dto.MonthlySummaryResponse;
import com.saurabh.ExpenseTracker.entity.Category;
import com.saurabh.ExpenseTracker.entity.User;
import com.saurabh.ExpenseTracker.event.MonthlySummaryCacheClearEvent;
import com.saurabh.ExpenseTracker.exception.ResourceNotFoundException;
import com.saurabh.ExpenseTracker.repository.CategoryRepository;
import com.saurabh.ExpenseTracker.repository.ExpenseRepository;
import com.saurabh.ExpenseTracker.repository.UserRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final MonthlySummaryCacheService monthlySummaryCacheService;
    private final ApplicationEventPublisher eventPublisher;

    public CategoryService(
            CategoryRepository categoryRepository,
            ExpenseRepository expenseRepository,
            UserRepository userRepository,
            MonthlySummaryCacheService monthlySummaryCacheService,
            ApplicationEventPublisher eventPublisher) {

        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.monthlySummaryCacheService = monthlySummaryCacheService;
        this.eventPublisher = eventPublisher;
    }

    // CREATE CATEGORY
    public CategoryResponse createCategory(CategoryRequest request) {

        User user = getCurrentUser();

        if (categoryRepository.existsByNameAndUser(
                request.getName(), user)) {
            throw new IllegalArgumentException(
                    "Category already exists for this user.");
        }

        Category category = new Category(
                request.getName(),
                request.getBudgetLimit(),
                user);

        Category savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    // GET ALL CATEGORIES
    public List<CategoryResponse> getAllCategories() {

        User user = getCurrentUser();

        return categoryRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET CATEGORY BY ID
    public CategoryResponse getCategoryById(Long id) {

        Category category = findCategoryById(id);

        return mapToResponse(category);
    }

    // UPDATE CATEGORY
    @Transactional
    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        User user = getCurrentUser();
        Category category = findCategoryById(id);

        if (categoryRepository.existsByNameAndUserAndIdNot(
                request.getName(), user, id)) {
            throw new IllegalArgumentException(
                    "Category already exists for this user.");
        }

        category.setName(request.getName());
        category.setBudgetLimit(request.getBudgetLimit());

        Category updatedCategory = categoryRepository.save(category);

        // Clear cached summaries after the transaction commits.
        eventPublisher.publishEvent(new MonthlySummaryCacheClearEvent());

        return mapToResponse(updatedCategory);
    }

    // DELETE CATEGORY
    @Transactional
    public void deleteCategory(Long id) {

        Category category = findCategoryById(id);

        boolean hasExpenses = !expenseRepository
                .findByCategoryId(id)
                .isEmpty();

        if (hasExpenses) {
            throw new IllegalStateException(
                    "Cannot delete category because it has existing expenses.");
        }

        categoryRepository.delete(category);

        // Clear cached summaries after the transaction commits.
        eventPublisher.publishEvent(new MonthlySummaryCacheClearEvent());
    }

    // FIND CATEGORY WITH OWNERSHIP CHECK
    private Category findCategoryById(Long id) {

        User user = getCurrentUser();

        return categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id));
    }

    // GET CURRENT USER
    private User getCurrentUser() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found: " + username));
    }

    // MAP CATEGORY TO RESPONSE
    private CategoryResponse mapToResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getBudgetLimit());
    }

    // GET MONTHLY SUMMARY
    public MonthlySummaryResponse getMonthlySummary(
            Long categoryId,
            YearMonth month) {

        // Verify ownership on every request, including cache hits.
        Category category = findCategoryById(categoryId);

        // The separate Spring bean performs the cached calculation.
        return monthlySummaryCacheService.getMonthlySummary(
                category.getId(),
                category.getName(),
                category.getBudgetLimit(),
                month);
    }
}
