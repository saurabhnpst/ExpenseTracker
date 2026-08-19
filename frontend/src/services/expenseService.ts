import api from "./api"
import type {
  Expense,
  ExpenseRequest,
} from "../types/expense"

export interface ExpensePage {
  content: Expense[]
  empty: boolean
  first: boolean
  last: boolean
  number: number
  numberOfElements: number
  size: number
  totalElements: number
  totalPages: number
}

export const getExpenses = async (
  page: number = 0,
  size: number = 10,
  categoryId?: number
): Promise<ExpensePage> => {

  const response = await api.get<ExpensePage>(
    "/expenses",
    {
      params: {
        page,
        size,
        ...(categoryId !== undefined && { categoryId }),
      },
    }
  )

  return response.data
}

export const getExpenseById = async (
  id: number
): Promise<Expense> => {

  const response = await api.get<Expense>(
    `/expenses/${id}`
  )

  return response.data
}

export const createExpense = async (
  data: ExpenseRequest
): Promise<Expense> => {

  const response = await api.post<Expense>(
    "/expenses",
    data
  )

  return response.data
}

export const updateExpense = async (
  id: number,
  data: ExpenseRequest
): Promise<Expense> => {

  const response = await api.put<Expense>(
    `/expenses/${id}`,
    data
  )

  return response.data
}

export const deleteExpense = async (
  id: number
): Promise<void> => {

  await api.delete(`/expenses/${id}`)
}