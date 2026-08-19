export interface Category {
  id: number
  name: string
  budgetLimit: number
}

export interface CategoryRequest {
  name: string
  budgetLimit: number
}