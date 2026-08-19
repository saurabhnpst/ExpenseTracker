package com.saurabh.ExpenseTracker.service;

import com.saurabh.ExpenseTracker.dto.CategoryRequest;
import com.saurabh.ExpenseTracker.dto.CategoryResponse;
import com.saurabh.ExpenseTracker.entity.Category;
import com.saurabh.ExpenseTracker.exception.ResourceNotFoundException;
import com.saurabh.ExpenseTracker.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import com.saurabh.ExpenseTracker.dto.MonthlySummaryResponse;
import com.saurabh.ExpenseTracker.repository.ExpenseRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            ExpenseRepository expenseRepository) {

        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
    }

    public CategoryResponse createCategory(CategoryRequest request) {

        Category category = new Category(
                request.getName(),
                request.getBudgetLimit()
        );

        Category savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    public List<CategoryResponse> getAllCategories() {

        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CategoryResponse getCategoryById(Long id) {

        Category category = findCategoryById(id);

        return mapToResponse(category);
    }

    public CategoryResponse updateCategory(
            Long id,
            CategoryRequest request) {

        Category category = findCategoryById(id);

        category.setName(request.getName());
        category.setBudgetLimit(request.getBudgetLimit());

        Category updatedCategory = categoryRepository.save(category);

        return mapToResponse(updatedCategory);
    }

    public void deleteCategory(Long id) {

        Category category = findCategoryById(id);

        categoryRepository.delete(category);
    }

    private Category findCategoryById(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id
                        )
                );
    }

    private CategoryResponse mapToResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getBudgetLimit()
        );
    }

    public MonthlySummaryResponse getMonthlySummary(
            Long categoryId,
            YearMonth month) {

        Category category = findCategoryById(categoryId);

        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.plusMonths(1).atDay(1);

        BigDecimal monthlyTotal =
                expenseRepository.calculateMonthlyTotal(
                        categoryId,
                        startDate,
                        endDate
                );

        boolean budgetExceeded =
                monthlyTotal.compareTo(category.getBudgetLimit()) > 0;

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