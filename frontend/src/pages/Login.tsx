import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { login as loginUser } from "../services/authService"
import { useAuth } from "../context/AuthContext"

function Login() {
  const [username, setUsername] = useState("")
  const [password, setPassword] = useState("")

  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    try {
      const response = await loginUser({
        username,
        password,
      })

      // Save JWT and update authentication state
      login(response.token!)

      console.log("Login successful")

      // Redirect to dashboard
      navigate("/dashboard")

    } catch (error) {
      console.error("Login failed:", error)
    }
  }

  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center px-4">

      <div className="w-full max-w-md">

        {/* Logo / Brand */}
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
            Manage your money smarter.
          </p>

        </div>

        {/* Login Card */}
        <div className="bg-white rounded-2xl shadow-2xl p-8">

          <div className="mb-6">

            <h2 className="text-2xl font-semibold text-slate-900">
              Welcome back
            </h2>

            <p className="text-sm text-slate-500 mt-1">
              Sign in to continue to your dashboard.
            </p>

          </div>

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
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter your username"
                className="w-full rounded-xl border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20"
                required
              />

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
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your password"
                className="w-full rounded-xl border border-slate-300 px-4 py-3 text-slate-900 outline-none transition focus:border-indigo-500 focus:ring-2 focus:ring-indigo-500/20"
                required
              />

            </div>

            {/* Submit */}
            <button
              type="submit"
              className="w-full rounded-xl bg-indigo-600 py-3.5 font-semibold text-white transition hover:bg-indigo-700 active:scale-[0.99]"
            >
              Sign in
            </button>

          </form>

          {/* Register */}
          <div className="mt-6 text-center text-sm text-slate-500">

            Don't have an account?{" "}

            <button
              type="button"
              className="font-semibold text-indigo-600 hover:text-indigo-700"
              onClick={() => navigate("/register")}
            >
              Create account
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

export default Login