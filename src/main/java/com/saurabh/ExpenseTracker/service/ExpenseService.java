package com.saurabh.ExpenseTracker.service;

import com.saurabh.ExpenseTracker.dto.ExpenseRequest;
import com.saurabh.ExpenseTracker.dto.ExpenseResponse;
import com.saurabh.ExpenseTracker.entity.Category;
import com.saurabh.ExpenseTracker.entity.Expense;
import com.saurabh.ExpenseTracker.exception.ResourceNotFoundException;
import com.saurabh.ExpenseTracker.repository.CategoryRepository;
import com.saurabh.ExpenseTracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            CategoryRepository categoryRepository) {

        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
    }

    public ExpenseResponse createExpense(ExpenseRequest request) {

        Category category = findCategoryById(request.getCategoryId());

        Expense expense = new Expense(
                request.getAmount(),
                request.getDate(),
                request.getDescription(),
                category
        );

        Expense savedExpense = expenseRepository.save(expense);

        return mapToResponse(savedExpense);
    }

//    public List<ExpenseResponse> getAllExpenses() {
//
//        return expenseRepository.findAll()
//                .stream()
//                .map(this::mapToResponse)
//                .toList();
//    }

    public Page<ExpenseResponse> getAllExpenses(
            Long categoryId,
            Pageable pageable) {

        Page<Expense> expenses;

        if (categoryId != null) {

            // Check category exists
            findCategoryById(categoryId);

            expenses = expenseRepository
                    .findByCategoryId(categoryId, pageable);

        } else {

            expenses = expenseRepository
                    .findAll(pageable);
        }

        return expenses.map(this::mapToResponse);
    }

    public ExpenseResponse getExpenseById(Long id) {

        Expense expense = findExpenseById(id);

        return mapToResponse(expense);
    }

    public ExpenseResponse updateExpense(
            Long id,
            ExpenseRequest request) {

        Expense expense = findExpenseById(id);

        Category category = findCategoryById(request.getCategoryId());

        expense.setAmount(request.getAmount());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        expense.setCategory(category);

        Expense updatedExpense = expenseRepository.save(expense);

        return mapToResponse(updatedExpense);
    }

    public void deleteExpense(Long id) {

        Expense expense = findExpenseById(id);

        expenseRepository.delete(expense);
    }

    private Expense findExpenseById(Long id) {

        return expenseRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Expense not found with id: " + id
                        )
                );
    }

    private Category findCategoryById(Long id) {

        return categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found with id: " + id
                        )
                );
    }

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

    public List<ExpenseResponse> getExpensesByCategory(Long categoryId) {

        // First check whether category exists
        findCategoryById(categoryId);

        return expenseRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}