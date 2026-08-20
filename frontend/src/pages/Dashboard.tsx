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
import type { MonthlySummary } from "../services/categoryService"

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts"

function getCurrentUsername(): string {
  const token = localStorage.getItem("token")

  if (!token) {
    return "User"
  }

  try {
    const payload = JSON.parse(
      atob(token.split(".")[1])
    )

    return payload.sub || "User"
  } catch {
    return "User"
  }
}

function Dashboard() {
  const [totalExpenses, setTotalExpenses] = useState(0)
  const [categoryCount, setCategoryCount] = useState(0)
  const [recentExpenses, setRecentExpenses] = useState<Expense[]>([])

  const [monthlySpent, setMonthlySpent] = useState(0)
  const [monthlyBudget, setMonthlyBudget] = useState(0)
  const [budgetPercentage, setBudgetPercentage] = useState(0)

  const [categorySummaries, setCategorySummaries] = useState<
    MonthlySummary[]
  >([])

  const [selectedYear, setSelectedYear] = useState(
    new Date().getFullYear()
  )

  const [monthlyChartData, setMonthlyChartData] = useState<
    { month: string; spending: number }[]
  >([])

  const [categories, setCategories] = useState<
    { id: number }[]
  >([])

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  const username = getCurrentUsername()

  // ----------------------------------------
  // Dashboard data
  // ----------------------------------------

  useEffect(() => {
    const loadDashboardData = async () => {
      try {
        setLoading(true)
        setError("")

        const [expenseData, categories] = await Promise.all([
          getExpenses(0, 10),
          getCategories(),
        ])

        setCategories(categories)

        setRecentExpenses(
          expenseData.content.slice(0, 3)
        )

        setCategoryCount(categories.length)

        const currentMonth = new Date()
          .toISOString()
          .slice(0, 7)

        const summaries = await Promise.all(
          categories.map((category) =>
            getMonthlySummary(
              category.id,
              currentMonth
            )
          )
        )

        // Store category-wise monthly summaries
        setCategorySummaries(summaries)

        const totalMonthlySpent = summaries.reduce(
          (sum, summary) =>
            sum + Number(summary.monthlyTotal),
          0
        )

        const totalMonthlyBudget = summaries.reduce(
          (sum, summary) =>
            sum + Number(summary.budgetLimit),
          0
        )

        setTotalExpenses(totalMonthlySpent)
        setMonthlySpent(totalMonthlySpent)
        setMonthlyBudget(totalMonthlyBudget)

        const percentage =
          totalMonthlyBudget > 0
            ? (totalMonthlySpent / totalMonthlyBudget) * 100
            : 0

        setBudgetPercentage(
          Math.round(percentage)
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

  // ----------------------------------------
  // Monthly spending chart
  // ----------------------------------------

  useEffect(() => {
    const loadMonthlyChart = async () => {
      if (categories.length === 0) {
        setMonthlyChartData([])
        return
      }

      try {
        const months = [
          "Jan",
          "Feb",
          "Mar",
          "Apr",
          "May",
          "Jun",
          "Jul",
          "Aug",
          "Sep",
          "Oct",
          "Nov",
          "Dec",
        ]

        const chartData = await Promise.all(
          months.map(async (monthName, index) => {

            const month = `${selectedYear}-${String(
              index + 1
            ).padStart(2, "0")}`

            const summaries = await Promise.all(
              categories.map((category) =>
                getMonthlySummary(
                  category.id,
                  month
                )
              )
            )

            const spending = summaries.reduce(
              (sum, summary) =>
                sum + Number(summary.monthlyTotal),
              0
            )

            return {
              month: monthName,
              spending,
            }
          })
        )

        setMonthlyChartData(chartData)

      } catch (error) {
        console.error(
          "Failed to load monthly chart:",
          error
        )
      }
    }

    loadMonthlyChart()
  }, [selectedYear, categories])

  return (
    <div className="min-h-screen bg-slate-100 flex">

      <Sidebar />

      <div className="flex-1 min-w-0">

        <Topbar />

        <main className="p-6">

          {/* Welcome */}
          <div className="mb-8">

            <h1 className="text-2xl font-bold text-slate-900">
              Good evening, {username} 👋
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
              value={`₹${totalExpenses.toLocaleString(
                "en-IN"
              )}`}
              description="This month"
            />

            <StatCard
              title="Monthly Budget"
              value={`₹${monthlyBudget.toLocaleString(
                "en-IN"
              )}`}
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

                <select
                  value={selectedYear}
                  onChange={(e) =>
                    setSelectedYear(
                      Number(e.target.value)
                    )
                  }
                  className="border border-slate-200 rounded-lg px-3 py-2 text-sm text-slate-600 outline-none"
                >
                  <option value={2026}>
                    2026
                  </option>

                  <option value={2025}>
                    2025
                  </option>

                  <option value={2024}>
                    2024
                  </option>
                </select>

              </div>

              {/* Chart */}
              <div className="h-64 rounded-xl bg-slate-50 p-4">

                {monthlyChartData.length === 0 ? (

                  <div className="h-full flex items-center justify-center">
                    <p className="text-sm text-slate-400">
                      No spending data available.
                    </p>
                  </div>

                ) : (

                  <ResponsiveContainer
                    width="100%"
                    height="100%"
                  >
                    <BarChart
                      data={monthlyChartData}
                    >

                      <CartesianGrid
                        strokeDasharray="3 3"
                        vertical={false}
                      />

                      <XAxis
                        dataKey="month"
                        tickLine={false}
                        axisLine={false}
                      />

                      <YAxis
                        tickLine={false}
                        axisLine={false}
                        tickFormatter={(value) =>
                          `₹${value}`
                        }
                      />

                      <Tooltip
                        formatter={(value) =>
                          `₹${Number(
                            value
                          ).toLocaleString("en-IN")}`
                        }
                      />

                      <Bar
                        dataKey="spending"
                        fill="#4f46e5"
                        radius={[
                          6,
                          6,
                          0,
                          0,
                        ]}
                      />

                    </BarChart>
                  </ResponsiveContainer>

                )}

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

                  <span
                    className={`font-semibold ${
                      budgetPercentage > 100
                        ? "text-red-600"
                        : "text-slate-900"
                    }`}
                  >
                    {budgetPercentage}%
                  </span>

                </div>

                <div className="w-full h-3 bg-slate-100 rounded-full overflow-hidden">

                  <div
                    className={`h-full rounded-full transition-all duration-500 ${
                      budgetPercentage > 100
                        ? "bg-red-500"
                        : "bg-indigo-600"
                    }`}
                    style={{
                      width: `${Math.min(
                        budgetPercentage,
                        100
                      )}%`,
                    }}
                  />

                </div>

                <div className="flex justify-between mt-3 text-sm">

                  <span className="text-slate-500">
                    ₹{monthlySpent.toLocaleString(
                      "en-IN"
                    )}{" "}
                    spent
                  </span>

                  <span className="text-slate-500">
                    ₹{monthlyBudget.toLocaleString(
                      "en-IN"
                    )}
                  </span>

                </div>

              </div>

            </div>

          </div>

          {/* Category Budget Status */}
          <div className="mt-6 bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">

            <h2 className="font-semibold text-slate-900">
              Category Budget Status
            </h2>

            <p className="text-sm text-slate-500 mt-1">
              Monthly budget status by category
            </p>

            {categorySummaries.length === 0 ? (

              <div className="mt-5">
                <p className="text-sm text-slate-400">
                  No category budget data available.
                </p>
              </div>

            ) : (

              <div className="mt-5 space-y-4">

                {categorySummaries.map((summary) => (

                  <div
                    key={summary.categoryId}
                    className="flex items-center justify-between border-b border-slate-100 pb-4 last:border-b-0 last:pb-0"
                  >

                    <div>

                      <p className="font-medium text-slate-900">
                        {summary.categoryName}
                      </p>

                      <p className="text-sm text-slate-500 mt-1">
                        ₹{Number(
                          summary.monthlyTotal
                        ).toLocaleString("en-IN")}
                        {" / "}
                        ₹{Number(
                          summary.budgetLimit
                        ).toLocaleString("en-IN")}
                      </p>

                    </div>

                    {summary.budgetExceeded ? (

                      <span className="text-sm font-semibold text-red-600">
                        ⚠ Budget Exceeded
                      </span>

                    ) : (

                      <span className="text-sm font-semibold text-green-600">
                        ✓ Within Budget
                      </span>

                    )}

                  </div>

                ))}

              </div>

            )}

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
                        {expense.description ||
                          "Expense"}
                      </p>

                      <p className="text-sm text-slate-500">
                        {expense.categoryName ||
                          "Uncategorized"}
                        {" • "}
                        {expense.date}
                      </p>

                    </div>

                    <p className="font-semibold text-slate-900">
                      ₹
                      {Math.abs(
                        expense.amount
                      ).toLocaleString(
                        "en-IN"
                      )}
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