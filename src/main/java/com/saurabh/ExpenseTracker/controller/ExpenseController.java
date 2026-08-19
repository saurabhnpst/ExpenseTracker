package com.saurabh.ExpenseTracker.controller;

import com.saurabh.ExpenseTracker.dto.ExpenseRequest;
import com.saurabh.ExpenseTracker.dto.ExpenseResponse;
import com.saurabh.ExpenseTracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse createExpense(
            @Valid @RequestBody ExpenseRequest request) {

        return expenseService.createExpense(request);
    }

//    @GetMapping
//    public List<ExpenseResponse> getAllExpenses() {
//
//        return expenseService.getAllExpenses();
//    }
@GetMapping
public Page<ExpenseResponse> getAllExpenses(
        @RequestParam(required = false) Long categoryId,
        Pageable pageable) {

    return expenseService.getAllExpenses(
            categoryId,
            pageable
    );
}

    @GetMapping("/{id}")
    public ExpenseResponse getExpenseById(
            @PathVariable Long id) {

        return expenseService.getExpenseById(id);
    }

    @PutMapping("/{id}")
    public ExpenseResponse updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) {

        return expenseService.updateExpense(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExpense(@PathVariable Long id) {

        expenseService.deleteExpense(id);
    }


}