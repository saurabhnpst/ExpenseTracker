package com.saurabh.ExpenseTracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI expenseTrackerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Expense Tracker API")
                        .description(
                                "REST API for managing personal expenses, "
                                        + "categories, budgets, authentication, "
                                        + "and expense filtering."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Saurabh Soni")));
    }
}