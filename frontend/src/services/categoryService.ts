import api from "./api"
import type {
  Category,
  CategoryRequest,
} from "../types/category"

export interface MonthlySummary {
  categoryId: number
  categoryName: string
  month: string
  monthlyTotal: number
  budgetLimit: number
  budgetExceeded: boolean
}

export const getCategories = async (): Promise<Category[]> => {
  const response = await api.get<Category[]>(
    "/categories"
  )

  return response.data
}

export const getCategoryById = async (
  id: number
): Promise<Category> => {
  const response = await api.get<Category>(
    `/categories/${id}`
  )

  return response.data
}

export const createCategory = async (
  data: CategoryRequest
): Promise<Category> => {
  const response = await api.post<Category>(
    "/categories",
    data
  )

  return response.data
}

export const updateCategory = async (
  id: number,
  data: CategoryRequest
): Promise<Category> => {
  const response = await api.put<Category>(
    `/categories/${id}`,
    data
  )

  return response.data
}

export const deleteCategory = async (
  id: number
): Promise<void> => {
  await api.delete(`/categories/${id}`)
}

export const getExpensesByCategory = async (
  categoryId: number
): Promise<any[]> => {
  const response = await api.get<any[]>(
    `/categories/${categoryId}/expenses`
  )

  return response.data
}

export const getMonthlySummary = async (
  categoryId: number,
  month: string
): Promise<MonthlySummary> => {
  const response = await api.get<MonthlySummary>(
    `/categories/${categoryId}/monthly-summary`,
    {
      params: {
        month,
      },
    }
  )

  return response.data
}