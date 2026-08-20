import { useEffect, useState } from "react"

import {
  createCategory,
  deleteCategory,
  getCategories,
  updateCategory,
} from "../services/categoryService"

import type { Category } from "../types/category"

function Categories() {
  const [categories, setCategories] = useState<Category[]>([])

  const [loading, setLoading] = useState(true)
  const [error, setError] = useState("")

  const [showForm, setShowForm] = useState(false)
  const [editingCategory, setEditingCategory] =
    useState<Category | null>(null)

  const [name, setName] = useState("")
  const [budgetLimit, setBudgetLimit] =
    useState("")

  const [saving, setSaving] = useState(false)

  // ----------------------------------------
  // Load categories
  // ----------------------------------------
  const loadCategories = async () => {
    try {
      setLoading(true)
      setError("")

      const data = await getCategories()

      setCategories(data)
    } catch (error) {
      console.error(
        "Failed to load categories:",
        error
      )

      setError(
        "Unable to load categories."
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadCategories()
  }, [])

  // ----------------------------------------
  // Reset form
  // ----------------------------------------
  const resetForm = () => {
    setName("")
    setBudgetLimit("")
    setEditingCategory(null)
    setShowForm(false)
  }

  // ----------------------------------------
  // Add category
  // ----------------------------------------
  const handleAddCategory = () => {
    setEditingCategory(null)
    setName("")
    setBudgetLimit("")
    setError("")
    setShowForm(true)
  }

  // ----------------------------------------
  // Edit category
  // ----------------------------------------
  const handleEditCategory = (
    category: Category
  ) => {
    setEditingCategory(category)
    setName(category.name)
    setBudgetLimit(
      category.budgetLimit.toString()
    )
    setError("")
    setShowForm(true)
  }

  // ----------------------------------------
  // Submit category
  // ----------------------------------------
  const handleSubmit = async (
    e: React.FormEvent
  ) => {
    e.preventDefault()

    if (!name.trim() || !budgetLimit) {
      setError(
        "Please fill all fields."
      )
      return
    }

    const budget = Number(budgetLimit)

    if (budget < 0) {
      setError(
        "Budget cannot be negative."
      )
      return
    }

    try {
      setSaving(true)
      setError("")

      const categoryData = {
        name: name.trim(),
        budgetLimit: budget,
      }

      if (editingCategory) {
        await updateCategory(
          editingCategory.id,
          categoryData
        )
      } else {
        await createCategory(
          categoryData
        )
      }

      resetForm()

      await loadCategories()
    } catch (error) {
      console.error(
        "Failed to save category:",
        error
      )

      setError(
        "Unable to save category."
      )
    } finally {
      setSaving(false)
    }
  }

  // ----------------------------------------
  // Delete category
  // ----------------------------------------
  const handleDelete = async (
    id: number
  ) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this category?"
    )

    if (!confirmed) {
      return
    }

    try {
      setError("")

      await deleteCategory(id)

      await loadCategories()
    } catch (error) {
      console.error(
        "Failed to delete category:",
        error
      )

      setError(
        "Unable to delete category."
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
              Categories
            </h1>

            <p className="text-sm text-slate-500 mt-1">
              Manage your expense categories
            </p>
          </div>

          <button
            onClick={handleAddCategory}
            className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2.5 rounded-lg font-medium transition"
          >
            + Add Category
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
                  {editingCategory
                    ? "Edit Category"
                    : "Add Category"}
                </h2>

                <p className="text-sm text-slate-500 mt-1">
                  Enter category details
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

              {/* Name */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Category Name
                </label>

                <input
                  type="text"
                  value={name}
                  onChange={(e) =>
                    setName(e.target.value)
                  }
                  placeholder="e.g. Food"
                  className="w-full border border-slate-200 rounded-lg px-3 py-2.5 outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              {/* Budget */}
              <div>
                <label className="block text-sm font-medium text-slate-700 mb-2">
                  Monthly Budget
                </label>

                <input
                  type="number"
                  min="0"
                  step="0.01"
                  value={budgetLimit}
                  onChange={(e) =>
                    setBudgetLimit(
                      e.target.value
                    )
                  }
                  placeholder="e.g. 5000"
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
                    : editingCategory
                    ? "Update Category"
                    : "Add Category"}
                </button>

              </div>

            </form>

          </div>
        )}

        {/* Categories */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">

          <div className="p-6 border-b border-slate-100">

            <h2 className="font-semibold text-slate-900">
              All Categories
            </h2>

            <p className="text-sm text-slate-500 mt-1">
              Your expense categories and budgets
            </p>

          </div>

          {loading ? (

            <div className="p-10 text-center">
              <p className="text-sm text-slate-500">
                Loading categories...
              </p>
            </div>

          ) : categories.length === 0 ? (

            <div className="p-10 text-center">

              <p className="text-slate-500">
                No categories found.
              </p>

              <button
                onClick={handleAddCategory}
                className="mt-4 text-indigo-600 font-medium"
              >
                Add your first category
              </button>

            </div>

          ) : (

            <div className="overflow-x-auto">

              <table className="w-full">

                <thead className="bg-slate-50">

                  <tr>

                    <th className="text-left px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                      Category
                    </th>

                    <th className="text-left px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                      Monthly Budget
                    </th>

                    <th className="text-right px-6 py-4 text-xs font-semibold text-slate-500 uppercase">
                      Actions
                    </th>

                  </tr>

                </thead>

                <tbody className="divide-y divide-slate-100">

                  {categories.map(
                    (category) => (

                      <tr
                        key={category.id}
                        className="hover:bg-slate-50"
                      >

                        <td className="px-6 py-4">

                          <span className="inline-flex px-3 py-1 bg-indigo-50 text-indigo-600 rounded-full text-sm font-medium">
                            {category.name}
                          </span>

                        </td>

                        <td className="px-6 py-4">

                          <span className="font-semibold text-slate-900">
                            ₹
                            {Number(
                              category.budgetLimit
                            ).toLocaleString(
                              "en-IN"
                            )}
                          </span>

                        </td>

                        <td className="px-6 py-4">

                          <div className="flex justify-end gap-4">

                            <button
                              onClick={() =>
                                handleEditCategory(
                                  category
                                )
                              }
                              className="text-sm text-indigo-600 hover:text-indigo-800 font-medium"
                            >
                              Edit
                            </button>

                            <button
                              onClick={() =>
                                handleDelete(
                                  category.id
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

        </div>

      </main>

    </div>
  )
}

export default Categories