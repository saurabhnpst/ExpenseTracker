package com.saurabh.ExpenseTracker.specification;

import com.saurabh.ExpenseTracker.entity.Expense;
import com.saurabh.ExpenseTracker.entity.User;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Builds dynamic database filters for Expense queries.
 *
 * Why do we need this?
 *
 * We want the GET /api/expenses API to support multiple optional filters:
 *
 * - categoryId
 * - fromDate
 * - toDate
 * - minAmount
 * - maxAmount
 *
 * Instead of creating a separate repository method for every
 * possible combination of filters, we use Spring Data JPA Specification.
 *
 * Example:
 *
 * categoryId + fromDate + maxAmount
 *
 * can be combined dynamically using:
 *
 * specification1.and(specification2).and(specification3)
 *
 * This keeps the repository and service cleaner as more filters
 * are added in the future.
 */
public class ExpenseSpecification {

    /*
     * Utility class.
     *
     * We don't create objects of ExpenseSpecification.
     * We only use its static methods to create Specifications.
     */
    private ExpenseSpecification() {
        // Prevent object creation
    }

    /**
     * Security / ownership filter.
     *
     * Expense does not directly contain a User.
     *
     * The relationship is:
     *
     * Expense -> Category -> User
     *
     * This specification makes sure that only expenses belonging
     * to the currently authenticated user's categories are returned.
     */
    public static Specification<Expense> belongsToUser(User user) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("category").get("user"),
                        user
                );
    }

    /**
     * Filters expenses by category ID.
     *
     * Example:
     *
     * /api/expenses?categoryId=2
     *
     * Only expenses belonging to category 2 will be returned.
     */
    public static Specification<Expense> hasCategory(Long categoryId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("category").get("id"),
                        categoryId
                );
    }

    /**
     * Filters expenses whose date is on or after the given date.
     *
     * Example:
     *
     * fromDate = 2026-10-01
     *
     * Returns expenses from October 1 onwards.
     */
    public static Specification<Expense> dateGreaterThanOrEqualTo(
            LocalDate fromDate) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("date"),
                        fromDate
                );
    }

    /**
     * Filters expenses whose date is on or before the given date.
     *
     * Example:
     *
     * toDate = 2026-10-05
     *
     * Returns expenses up to October 5.
     */
    public static Specification<Expense> dateLessThanOrEqualTo(
            LocalDate toDate) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("date"),
                        toDate
                );
    }

    /**
     * Filters expenses whose amount is greater than or equal
     * to the given minimum amount.
     *
     * Example:
     *
     * minAmount = 100
     *
     * Returns expenses with amount >= 100.
     */
    public static Specification<Expense> amountGreaterThanOrEqualTo(
            BigDecimal minAmount) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("amount"),
                        minAmount
                );
    }

    /**
     * Filters expenses whose amount is less than or equal
     * to the given maximum amount.
     *
     * Example:
     *
     * maxAmount = 5000
     *
     * Returns expenses with amount <= 5000.
     */
    public static Specification<Expense> amountLessThanOrEqualTo(
            BigDecimal maxAmount) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("amount"),
                        maxAmount
                );
    }
}