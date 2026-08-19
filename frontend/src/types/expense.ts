export interface Expense {
  id: number
  amount: number
  date: string
  description: string
  categoryId: number
  categoryName: string
}

export interface ExpenseRequest {
  amount: number
  date: string
  description: string
  categoryId: number
}