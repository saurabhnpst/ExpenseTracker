import { useEffect, useState } from "react"

import {
  createExpense,
  deleteExpense,
  getExpenses,
  updateExpense,
} from "../services/expenseService"

import { getCategories } from "../services/categoryService"

import type { Expense } from "../types/expense"
import type { Category } from "../types/category"

function Expenses() {
  const [expenses, setExpenses] = useState<Expense[]>([])
  const [categories, setCategories] = useState<Category[]>([])

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  const [showForm, setShowForm] = useState(false)
  const [editingExpense, setEditingExpense] =
    useState<Expense | null>(null)

  const [amount, setAmount] = useState("")
  const [date, setDate] = useState("")
  const [description, setDescription] = useState("")
  const [categoryId, setCategoryId] = useState("")

  const [saving, setSaving] = useState(false)

  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)

  // ----------------------------------------
  // Load expenses and categories
  // ----------------------------------------
  const loadData = async () => {
    try {
      setLoading(true)
      setError("")

      const [expenseData, categoryData] =
        await Promise.all([
          getExpenses(page, 10),
          getCategories(),
        ])

      setExpenses(expenseData.content)
      setTotalPages(expenseData.totalPages)
      setCategories(categoryData)
    } catch (error) {
      console.error(
        "Failed to load expenses:",
        error
      )

      setError(
        "Unable to load expenses."
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
  }, [page])

  // ----------------------------------------
  // Reset form
  // ----------------------------------------
  const resetForm = () => {
    setAmount("")
    setDate("")
    setDescription("")
    setCategoryId("")
    setEditingExpense(null)
    setShowForm(false)
  }

  // ----------------------------------------
  // Open add form
  // ----------------------------------------
  const handleAddExpense = () => {
    setEditingExpense(null)
    setAmount("")
    setDate(
      new Date().toISOString().split("T")[0]
    )
    setDescription("")
    setCategoryId("")
    setShowForm(true)
  }

  // ----------------------------------------
  // Open edit form
  // ----------------------------------------
  const handleEditExpense = (
    expense: Expense
  ) => {
    setEditingExpense(expense)
    setAmount(expense.amount.toString())
    setDate(expense.date)
    setDescription(expense.description)
    setCategoryId(
      expense.categoryId.toString()
    )
    setShowForm(true)
  }

  // ----------------------------------------
  // Submit form
  // ----------------------------------------
  const handleSubmit = async (
    e: React.FormEvent
  ) => {
    e.preventDefault()

    if (
      !amount ||
      !date ||
      !description ||
      !categoryId
    ) {
      setError(
        "Please fill all fields."
      )
      return
    }

    try {
      setSaving(true)
      setError("")

      const expenseData = {
        amount: Number(amount),
        date,
        description,
        categoryId: Number(categoryId),
      }

      if (editingExpense) {
        await updateExpense(
          editingExpense.id,
          expenseData
        )
      } else {
        await createExpense(
          expenseData
        )
      }

      resetForm()
      await loadData()
    } catch (error) {
      console.error(
        "Failed to save expense:",
        error
      )

      setError(
        "Unable to save expense."
      )
    } finally {
      setSaving(false)
    }
  }

  // ----------------------------------------
  // Delete expense
  // ----------------------------------------
  const handleDelete = async (
    id: number
  ) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this expense?"
    )

    if (!confirmed) {
      return
    }

    try {
      setError("")

      await deleteExpense(id)

      await loadData()
    } catch (error) {
      console.error(
        "Failed to delete expense:",
        error
      )

      setError(
        "Unable to delete expense."
      )
    }
  }

  return (
    <div className="min-h-screen bg-slate-100">

      {/* Header */}
      <div className="bg-white border-b border-slate-200">

        <div className="max-w-7xl mx-auto px-6 py-5 flex items-center justify-between">

          <div>
            <h1 className="text-2xl font-bold text-slate-900">
              Expenses
            </h1>

            <p className="text-sm text-slate-500 mt-1">
              Manage your expenses
            </p>
          </div>

          <button
            onClick={handleAddExpense}
            className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2.5 rounded-lg font-medium transition"
          >
            + Add Expense
          </button>

        </div>

      </div>

      <main className="max-w-7xl mx-auto p-6">

        {/* Error */}
        {error && (
          <div className="mb-5 bg-red-50 border border-red-200 text-red-600 rounded-xl p-4">
            {error}
          </div>
        )}

        {/* Add / Edit Form */}
        {showForm && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 mb-6">

            <div className="flex items-center justify-between mb-6">

              <div>
                <h2 className="text-lg font-semibold text-slate-900">
                  {editingExpense
                    ? "Edit Expense"
                    : "Add Expense"}
                </h2>

                <p className="text-sm text-slate-500 mt-1">
                  Enter expense details
                </p>
              </div>

              <button
                onClick={resetForm}
                className="text-slate-400 hover:text-slate-700 text-xl"
              >
                ✕
              </button>

            </div>

            <form
              onSubmit={handleSubmit}
              className="grid grid-cols-1 md:grid-cols-2 gap-5"
            >

              {/* Amount */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Amount
                </label>

                <input
                  type="number"
                  min="0"
                  step="0.01"
                  value={amount}
                  onChange={(e) =>
                    setAmount(e.target.value)
                  }
                  placeholder="Enter amount"
                  className="w-full border border-slate-200 rounded-lg px-3 py-2.5 outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              {/* Date */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Date
                </label>

                <input
                  type="date"
                  value={date}
                  onChange={(e) =>
                    setDate(e.target.value)
                  }
                  className="w-full border border-slate-200 rounded-lg px-3 py-2.5 outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              {/* Category */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Category
                </label>

                <select
                  value={categoryId}
                  onChange={(e) =>
                    setCategoryId(
                      e.target.value
                    )
                  }
                  className="w-full border border-slate-200 rounded-lg px-3 py-2.5 outline-none focus:ring-2 focus:ring-indigo-500"
                >

                  <option value="">
                    Select category
                  </option>

                  {categories.map(
                    (category) => (
                      <option
                        key={category.id}
                        value={category.id}
                      >
                        {category.name}
                      </option>
                    )
                  )}

                </select>
              </div>

              {/* Description */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Description
                </label>

                <input
                  type="text"
                  value={description}
                  onChange={(e) =>
                    setDescription(
                      e.target.value
                    )
                  }
                  placeholder="e.g. Lunch at restaurant"
                  className="w-full border border-slate-200 rounded-lg px-3 py-2.5 outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              {/* Buttons */}
              <div className="md:col-span-2 flex justify-end gap-3">

                <button
                  type="button"
                  onClick={resetForm}
                  className="px-4 py-2.5 border border-slate-200 rounded-lg text-slate-600 hover:bg-slate-50"
                >
                  Cancel
                </button>

                <button
                  type="submit"
                  disabled={saving}
                  className="px-5 py-2.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg font-medium disabled:opacity-50"
                >
                  {saving
                    ? "Saving..."
                    : editingExpense
                    ? "Update Expense"
                    : "Add Expense"}
                </button>

              </div>

            </form>

          </div>
        )}

        {/* Expenses Table */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">

          <div className="p-6 border-b border-slate-100">

            <h2 className="font-semibold text-slate-900">
              All Expenses
            </h2>

            <p className="text-sm text-slate-500 mt-1">
              Your expense history
            </p>

          </div>

          {loading ? (

            <div className="p-10 text-center">
              <p className="text-sm text-slate-500">
                Loading expenses...
              </p>
            </div>

          ) : expenses.length === 0 ? (

            <div className="p-10 text-center">
              <p className="text-slate-500">
                No expenses found.
              </p>

              <button
                onClick={handleAddExpense}
                className="mt-4 text-indigo-600 font-medium"
              >
                Add your first expense
              </button>
            </div>

          ) : (

            <div className="overflow-x-auto">

              <table className="w-full">

                <thead className="bg-slate-50">

                  <tr>

                    <th className="text-left px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                      Description
                    </th>

                    <th className="text-left px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                      Category
                    </th>

                    <th className="text-left px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                      Date
                    </th>

                    <th className="text-right px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                      Amount
                    </th>

                    <th className="text-right px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                      Actions
                    </th>

                  </tr>

                </thead>

                <tbody className="divide-y divide-slate-100">

                  {expenses.map(
                    (expense) => (

                      <tr
                        key={expense.id}
                        className="hover:bg-slate-50"
                      >

                        <td className="px-6 py-4">

                          <p className="font-medium text-slate-900">
                            {expense.description}
                          </p>

                        </td>

                        <td className="px-6 py-4">

                          <span className="px-2.5 py-1 bg-indigo-50 text-indigo-600 rounded-full text-sm">
                            {expense.categoryName ||
                              "Uncategorized"}
                          </span>

                        </td>

                        <td className="px-6 py-4 text-sm text-slate-500">
                          {expense.date}
                        </td>

                        <td className="px-6 py-4 text-right font-semibold text-slate-900">
                          ₹
                          {Math.abs(
                            expense.amount
                          ).toLocaleString(
                            "en-IN"
                          )}
                        </td>

                        <td className="px-6 py-4">

                          <div className="flex justify-end gap-3">

                            <button
                              onClick={() =>
                                handleEditExpense(
                                  expense
                                )
                              }
                              className="text-sm text-indigo-600 hover:text-indigo-800 font-medium"
                            >
                              Edit
                            </button>

                            <button
                              onClick={() =>
                                handleDelete(
                                  expense.id
                                )
                              }
                              className="text-sm text-red-500 hover:text-red-700 font-medium"
                            >
                              Delete
                            </button>

                          </div>

                        </td>

                      </tr>

                    )
                  )}

                </tbody>

              </table>

            </div>

          )}

          {/* Pagination */}
          {!loading &&
            totalPages > 1 && (
              <div className="px-6 py-4 border-t border-slate-100 flex items-center justify-between">

                <button
                  disabled={page === 0}
                  onClick={() =>
                    setPage(
                      (current) =>
                        current - 1
                    )
                  }
                  className="px-4 py-2 border border-slate-200 rounded-lg text-sm disabled:opacity-40"
                >
                  Previous
                </button>

                <span className="text-sm text-slate-500">
                  Page {page + 1} of{" "}
                  {totalPages}
                </span>

                <button
                  disabled={
                    page >=
                    totalPages - 1
                  }
                  onClick={() =>
                    setPage(
                      (current) =>
                        current + 1
                    )
                  }
                  className="px-4 py-2 border border-slate-200 rounded-lg text-sm disabled:opacity-40"
                >
                  Next
                </button>

              </div>
            )}

        </div>

      </main>

    </div>
  )
}

export default Expenses