# Expense Tracker

A full-stack Expense Tracker application built using Spring Boot and React.

The application allows users to manage expense categories and expenses, track monthly spending, and identify when a category exceeds its monthly budget.

---

## Features

- User registration and login
- JWT-based authentication
- Category CRUD operations
- Expense CRUD operations
- View expenses by category
- Pagination for expense listing
- Field validation
- Centralized error handling
- Monthly expense summary
- Category-wise budget tracking
- Budget exceeded detection
- Dashboard with spending overview
- Recent expenses
- Monthly spending chart
- Responsive frontend UI

---

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- JWT Authentication
- Maven
- MySQL

### Frontend

- React
- TypeScript
- Vite
- Tailwind CSS
- Recharts
- Axios

---

## Project Structure

```text
ExpenseTracker/
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── saurabh/
│                   └── ExpenseTracker/
│
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   └── types/
│   └── package.json
│
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md


Prerequisites

Make sure the following are installed:

Java 21
MySQL
Node.js
npm
Backend Setup
1. Clone the Repository
git clone git@github.com:saurabhnpst/ExpenseTracker.git
cd ExpenseTracker
2. Configure Database

Create the MySQL database:

CREATE DATABASE expense_tracker;

Update the database username and password in:

src/main/resources/application.properties

Example:

spring.datasource.url=jdbc:mysql://localhost:3306/expense_tracker
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

Replace YOUR_USERNAME and YOUR_PASSWORD with your local MySQL credentials.

3. Run the Backend

Using Maven Wrapper:

./mvnw spring-boot:run

On Windows:

mvnw.cmd spring-boot:run

The backend runs on:

http://localhost:8080
Frontend Setup

Open a new terminal:

cd frontend

Install dependencies:

npm install

Start the frontend:

npm run dev

The frontend runs on:

http://localhost:5173
Authentication

The application uses JWT-based authentication.

Users can register and log in to obtain authentication credentials.

Protected category and expense operations require authentication.

API Overview
Authentication
Register
POST /auth/register
Login
POST /auth/login
Category APIs
Create Category
POST /categories

Example request:

{
  "name": "Food",
  "budgetLimit": 5000
}
Get All Categories
GET /categories
Get Category by ID
GET /categories/{id}
Update Category
PUT /categories/{id}
Delete Category
DELETE /categories/{id}
Expense APIs
Create Expense
POST /expenses

Example request:

{
  "amount": 1500,
  "date": "2026-08-20",
  "description": "Dinner",
  "categoryId": 1
}
Get Expenses
GET /expenses?page=0&size=10

The expense listing supports pagination.

Get Expense by ID
GET /expenses/{id}
Update Expense
PUT /expenses/{id}
Delete Expense
DELETE /expenses/{id}
Category Expense Listing

Expenses belonging to a specific category can be retrieved using:

GET /categories/{id}/expenses
Monthly Budget Summary

The application provides a monthly summary for each category.

GET /categories/{id}/monthly-summary?month=2026-08

Example response:

{
  "categoryId": 1,
  "categoryName": "Food",
  "month": "2026-08",
  "monthlyTotal": 8000,
  "budgetLimit": 5000,
  "budgetExceeded": true
}
Budget Edge Case

The application handles the required budget edge case explicitly.

For each category:

The expenses for the selected month are calculated.
The monthly total is compared with the category's budgetLimit.
If the monthly total exceeds the budget limit, budgetExceeded is set to true.
The frontend displays the category as Budget Exceeded.

Example:

Category: Food


Monthly Total: ₹8,000
Budget Limit:  ₹5,000


Status: Budget Exceeded

Expenses belonging to other months are not included in the selected month's calculation.

Validation

The application validates incoming requests.

Category Validation
Category name is required.
Category name must contain between 2 and 100 characters.
Budget limit is required.
Budget limit cannot be negative.
Expense Validation
Amount is required.
Amount must be greater than 0.
Date is required.
Category ID is required.
Description cannot exceed 500 characters.
Error Handling

The application uses centralized exception handling.

Bad Request

Invalid input or validation errors return:

HTTP 400 BAD_REQUEST

The response contains field-level validation errors where applicable.

Resource Not Found

When a requested resource does not exist:

HTTP 404 NOT_FOUND
Internal Server Error

Unexpected server-side errors return:

HTTP 500 INTERNAL_SERVER_ERROR

A generic error message is returned to the client.

Pagination

The expense list supports pagination.

Example:

GET /expenses?page=0&size=10

The backend returns paginated expense data including page information.

The frontend provides:

Previous page
Next page
Current page
Total pages
Dashboard

The dashboard provides an overview of the user's finances.

It includes:

Total monthly expenses
Monthly budget
Category count
Monthly spending chart
Budget overview
Category-wise budget status
Recent expenses

The category-wise budget status displays:

✓ Within Budget

or:

⚠ Budget Exceeded

based on the monthly budget calculation returned by the backend.

Testing

Backend tests can be executed using:

./mvnw test

On Windows:

mvnw.cmd test
Running the Application

Start the backend first:

./mvnw spring-boot:run

Then start the frontend:

cd frontend
npm install
npm run dev

Open the frontend in the browser:

http://localhost:5173
Git Repository

Repository:

git@github.com:saurabhnpst/ExpenseTracker.git
Author

Saurabh Soni


