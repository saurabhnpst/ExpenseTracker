package com.saurabh.ExpenseTracker.controller;

import com.saurabh.ExpenseTracker.dto.CategoryRequest;
import com.saurabh.ExpenseTracker.dto.CategoryResponse;
import com.saurabh.ExpenseTracker.dto.ExpenseResponse;
import com.saurabh.ExpenseTracker.service.CategoryService;
import com.saurabh.ExpenseTracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.saurabh.ExpenseTracker.dto.MonthlySummaryResponse;
import java.time.YearMonth;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ExpenseService expenseService;

    public CategoryController(
            CategoryService categoryService,
            ExpenseService expenseService) {

        this.categoryService = categoryService;
        this.expenseService = expenseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(
            @Valid @RequestBody CategoryRequest request) {

        return categoryService.createCategory(request);
    }

    @GetMapping
    public List<CategoryResponse> getAllCategories() {

        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryResponse getCategoryById(
            @PathVariable Long id) {

        return categoryService.getCategoryById(id);
    }

    @PutMapping("/{id}")
    public CategoryResponse updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {

        categoryService.deleteCategory(id);
    }

    @GetMapping("/{categoryId}/expenses")
    public List<ExpenseResponse> getExpensesByCategory(
            @PathVariable Long categoryId) {

        return expenseService.getExpensesByCategory(categoryId);
    }

    @GetMapping("/{categoryId}/monthly-summary")
    public MonthlySummaryResponse getMonthlySummary(
            @PathVariable Long categoryId,
            @RequestParam String month) {

        YearMonth yearMonth = YearMonth.parse(month);

        return categoryService.getMonthlySummary(
                categoryId,
                yearMonth
        );
    }
}