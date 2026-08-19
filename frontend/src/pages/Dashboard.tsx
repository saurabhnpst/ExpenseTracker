import { useEffect, useState } from "react"

import Sidebar from "../components/Sidebar"
import Topbar from "../components/Topbar"
import StatCard from "../components/StatCard"

import { getExpenses } from "../services/expenseService"
import {
  getCategories,
  getMonthlySummary,
} from "../services/categoryService"

import type { Expense } from "../types/expense"

function Dashboard() {
  const [totalExpenses, setTotalExpenses] = useState(0)
  const [categoryCount, setCategoryCount] = useState(0)
  const [recentExpenses, setRecentExpenses] = useState<Expense[]>([])

  const [monthlySpent, setMonthlySpent] = useState(0)
  const [monthlyBudget, setMonthlyBudget] = useState(0)
  const [budgetPercentage, setBudgetPercentage] = useState(0)

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  useEffect(() => {
    const loadDashboardData = async () => {
      try {
        setLoading(true)
        setError("")

        // Get expenses and categories
        const [expenseData, categories] = await Promise.all([
          getExpenses(0, 10),
          getCategories(),
        ])

        // Recent expenses
        setRecentExpenses(
          expenseData.content.slice(0, 3)
        )

        // Number of categories
        setCategoryCount(categories.length)

        // Current month in YYYY-MM format
        const currentMonth = new Date()
          .toISOString()
          .slice(0, 7)

        // Get monthly summary for every category
        const summaries = await Promise.all(
          categories.map((category) =>
            getMonthlySummary(
              category.id,
              currentMonth
            )
          )
        )

        // Calculate total monthly spending
        const totalMonthlySpent = summaries.reduce(
          (sum, summary) =>
            sum + Number(summary.monthlyTotal),
          0
        )

        // Calculate total monthly budget
        const totalMonthlyBudget = summaries.reduce(
          (sum, summary) =>
            sum + Number(summary.budgetLimit),
          0
        )

        setTotalExpenses(totalMonthlySpent)
        setMonthlySpent(totalMonthlySpent)
        setMonthlyBudget(totalMonthlyBudget)

        // Calculate budget usage percentage
        const percentage =
          totalMonthlyBudget > 0
            ? (totalMonthlySpent / totalMonthlyBudget) * 100
            : 0

        setBudgetPercentage(
          Math.min(Math.round(percentage), 100)
        )

      } catch (error) {
        console.error(
          "Failed to load dashboard data:",
          error
        )

        setError(
          "Unable to load dashboard data."
        )

      } finally {
        setLoading(false)
      }
    }

    loadDashboardData()
  }, [])

  return (
    <div className="min-h-screen bg-slate-100 flex">

      <Sidebar />

      <div className="flex-1 min-w-0">

        <Topbar />

        <main className="p-6">

          {/* Welcome */}
          <div className="mb-8">
            <h1 className="text-2xl font-bold text-slate-900">
              Good evening, Saurabh 👋
            </h1>

            <p className="mt-1 text-slate-500">
              Here's what's happening with your finances.
            </p>
          </div>

          {/* Loading */}
          {loading && (
            <div className="mb-6 bg-white rounded-2xl border border-slate-200 p-5">
              <p className="text-sm text-slate-500">
                Loading your financial data...
              </p>
            </div>
          )}

          {/* Error */}
          {error && (
            <div className="mb-6 bg-red-50 border border-red-200 rounded-2xl p-5">
              <p className="text-sm text-red-600">
                {error}
              </p>
            </div>
          )}

          {/* Stats */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">

            <StatCard
              title="Total Expenses"
              value={`₹${totalExpenses.toLocaleString("en-IN")}`}
              description="This month"
            />

            <StatCard
              title="Monthly Budget"
              value={`₹${monthlyBudget.toLocaleString("en-IN")}`}
              description="Current budget"
            />

            <StatCard
              title="Categories"
              value={categoryCount.toString()}
              description="Active categories"
            />

          </div>

          {/* Main Content */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-5 mt-6">

            {/* Monthly Spending */}
            <div className="lg:col-span-2 bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">

              <div className="flex items-center justify-between mb-6">

                <div>
                  <h2 className="font-semibold text-slate-900">
                    Monthly Spending
                  </h2>

                  <p className="text-sm text-slate-500 mt-1">
                    Your spending overview
                  </p>
                </div>

                <select className="border border-slate-200 rounded-lg px-3 py-2 text-sm text-slate-600 outline-none">
                  <option>2026</option>
                </select>

              </div>

              <div className="h-64 rounded-xl bg-slate-50 flex items-center justify-center">

                <p className="text-sm text-slate-400">
                  Spending chart will appear here
                </p>

              </div>

            </div>

            {/* Budget Overview */}
            <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">

              <h2 className="font-semibold text-slate-900">
                Budget Overview
              </h2>

              <p className="text-sm text-slate-500 mt-1">
                Monthly spending
              </p>

              <div className="mt-8">

                <div className="flex justify-between text-sm mb-2">

                  <span className="text-slate-500">
                    Used
                  </span>

                  <span className="font-semibold text-slate-900">
                    {budgetPercentage}%
                  </span>

                </div>

                <div className="w-full h-3 bg-slate-100 rounded-full overflow-hidden">

                  <div
                    className="h-full bg-indigo-600 rounded-full transition-all duration-500"
                    style={{
                      width: `${budgetPercentage}%`,
                    }}
                  />

                </div>

                <div className="flex justify-between mt-3 text-sm">

                  <span className="text-slate-500">
                    ₹{monthlySpent.toLocaleString("en-IN")} spent
                  </span>

                  <span className="text-slate-500">
                    ₹{monthlyBudget.toLocaleString("en-IN")}
                  </span>

                </div>

              </div>

            </div>

          </div>

          {/* Recent Expenses */}
          <div className="mt-6 bg-white rounded-2xl border border-slate-200 shadow-sm">

            <div className="p-6 border-b border-slate-100">

              <h2 className="font-semibold text-slate-900">
                Recent Expenses
              </h2>

              <p className="text-sm text-slate-500 mt-1">
                Your latest transactions
              </p>

            </div>

            <div className="divide-y divide-slate-100">

              {loading ? (

                <div className="p-6 text-center">
                  <p className="text-sm text-slate-400">
                    Loading expenses...
                  </p>
                </div>

              ) : recentExpenses.length === 0 ? (

                <div className="p-6 text-center">
                  <p className="text-sm text-slate-400">
                    No expenses found.
                  </p>
                </div>

              ) : (

                recentExpenses.map((expense) => (

                  <div
                    key={expense.id}
                    className="p-5 flex items-center justify-between"
                  >

                    <div>

                      <p className="font-medium text-slate-900">
                        {expense.description || "Expense"}
                      </p>

                      <p className="text-sm text-slate-500">
                        {expense.categoryName || "Uncategorized"}
                        {" • "}
                        {expense.date}
                      </p>

                    </div>

                    <p className="font-semibold text-slate-900">
                      ₹{Math.abs(expense.amount).toLocaleString("en-IN")}
                    </p>

                  </div>

                ))

              )}

            </div>

          </div>

        </main>

      </div>

    </div>
  )
}

export default Dashboard