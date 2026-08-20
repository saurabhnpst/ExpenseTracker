import { useState } from "react"
import { useNavigate } from "react-router-dom"

import { register } from "../services/authService"
import { useAuth } from "../context/AuthContext"

function Register() {
  const navigate = useNavigate()
  const { login } = useAuth()

  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")
  const [confirmPassword, setConfirmPassword] = useState("")

  const [error, setError] = useState("")
  const [success, setSuccess] = useState("")
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (
    e: React.FormEvent
  ) => {
    e.preventDefault()

    setError("")
    setSuccess("")

    // Username validation
    if (username.trim().length < 3) {
      setError(
        "Username must be at least 3 characters."
      )
      return
    }

    if (username.trim().length > 30) {
      setError(
        "Username must not exceed 30 characters."
      )
      return
    }

    if (!/^[a-zA-Z0-9_]+$/.test(username.trim())) {
      setError(
        "Username can contain only letters, numbers and underscore."
      )
      return
    }

    // Password validation
    if (password.length < 8) {
      setError(
        "Password must be at least 8 characters."
      )
      return
    }

    if (password.length > 100) {
      setError(
        "Password must not exceed 100 characters."
      )
      return
    }

    if (!/[A-Z]/.test(password)) {
      setError(
        "Password must contain at least one uppercase letter."
      )
      return
    }

    if (!/[a-z]/.test(password)) {
      setError(
        "Password must contain at least one lowercase letter."
      )
      return
    }

    if (!/[0-9]/.test(password)) {
      setError(
        "Password must contain at least one number."
      )
      return
    }

    if (!/[@#$%^&*!]/.test(password)) {
      setError(
        "Password must contain at least one special character (@#$%^&*!)."
      )
      return
    }

    // Confirm password
    if (password !== confirmPassword) {
      setError(
        "Passwords do not match."
      )
      return
    }

    try {
      setLoading(true)

      const response = await register({
        username: username.trim(),
        password,
      })

      // Automatically login after successful registration
      if (response.token) {
        login(response.token)
        navigate("/dashboard")
        return
      }

      setSuccess(
        "Account created successfully. Please sign in."
      )

      setTimeout(() => {
        navigate("/login")
      }, 1000)

    } catch (error: any) {
      console.error(
        "Registration failed:",
        error
      )

      const message =
        error?.response?.data?.message ||
        error?.response?.data ||
        "Unable to create account."

      setError(
        typeof message === "string"
          ? message
          : "Unable to create account."
      )

    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center px-4">

      <div className="w-full max-w-md">

        {/* Brand */}
        <div className="text-center mb-8">

          <div className="inline-flex items-center justify-center w-14 h-14 rounded-2xl bg-indigo-600 mb-4 shadow-lg shadow-indigo-600/20">

            <span className="text-2xl font-bold text-white">
              $
            </span>

          </div>

          <h1 className="text-3xl font-bold text-white">
            ExpenseTracker
          </h1>

          <p className="text-slate-400 mt-2">
            Start managing your money smarter.
          </p>

        </div>

        {/* Register Card */}
        <div className="bg-white rounded-2xl shadow-2xl p-8">

          <div className="mb-6">

            <h2 className="text-2xl font-semibold text-slate-900">
              Create account
            </h2>

            <p className="text-sm text-slate-500 mt-1">
              Create an account to start tracking expenses.
            </p>

          </div>

          {/* Error */}
          {error && (
            <div className="mb-5 rounded-xl border border-red-200 bg-red-50 px-4 py-3">
              <p className="text-sm text-red-600">
                {error}
              </p>
            </div>
          )}

          {/* Success */}
          {success && (
            <div className="mb-5 rounded-xl border border-green-200 bg-green-50 px-4 py-3">
              <p className="text-sm text-green-600">
                {success}
              </p>
            </div>
          )}

          <form
            onSubmit={handleSubmit}
            className="space-y-5"
          >

            {/* Username */}
            <div>

              <label
                htmlFor="username"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Username
              </label>

              <input
                id="username"
                type="text"
                value={username}
                onChange={(e) =>
                  setUsername(e.target.value)
                }
                placeholder="Choose a username"
                className="w-full rounded-xl border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20"
                required
              />

              <p className="text-xs text-slate-400 mt-1">
                3–30 characters • letters, numbers and underscore
              </p>

            </div>

            {/* Password */}
            <div>

              <label
                htmlFor="password"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Password
              </label>

              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) =>
                  setPassword(e.target.value)
                }
                placeholder="Create a strong password"
                className="w-full rounded-xl border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20"
                required
              />

              <p className="text-xs text-slate-400 mt-1">
                8+ characters with uppercase, lowercase, number and special character
              </p>

            </div>

            {/* Confirm Password */}
            <div>

              <label
                htmlFor="confirmPassword"
                className="block text-sm font-medium text-slate-700 mb-2"
              >
                Confirm password
              </label>

              <input
                id="confirmPassword"
                type="password"
                value={confirmPassword}
                onChange={(e) =>
                  setConfirmPassword(e.target.value)
                }
                placeholder="Confirm your password"
                className="w-full rounded-xl border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20"
                required
              />

            </div>

            {/* Submit */}
            <button
              type="submit"
              disabled={loading}
              className="w-full rounded-xl bg-indigo-600 py-3.5 font-semibold text-white transition hover:bg-indigo-700 active:scale-[0.99] disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {loading
                ? "Creating account..."
                : "Create account"}
            </button>

          </form>

          {/* Login */}
          <div className="mt-6 text-center text-sm text-slate-500">

            Already have an account?{" "}

            <button
              type="button"
              onClick={() =>
                navigate("/login")
              }
              className="font-semibold text-indigo-600 hover:text-indigo-700"
            >
              Sign in
            </button>

          </div>

        </div>

        <p className="text-center text-xs text-slate-500 mt-6">
          Secure expense management
        </p>

      </div>

    </div>
  )
}

export default Register