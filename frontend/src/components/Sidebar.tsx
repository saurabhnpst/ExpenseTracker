import { NavLink } from "react-router-dom"
import { useAuth } from "../context/AuthContext"

function Sidebar() {
  const { logout } = useAuth()

  const navItems = [
    {
      name: "Dashboard",
      path: "/dashboard",
      icon: "▦",
    },
    {
      name: "Expenses",
      path: "/expenses",
      icon: "↕",
    },
    {
      name: "Categories",
      path: "/categories",
      icon: "◈",
    },
  ]

  return (
    <aside className="hidden md:flex w-64 min-h-screen flex-col bg-slate-950 text-white">

      {/* Logo */}
      <div className="h-20 flex items-center px-6 border-b border-slate-800">
        <div className="w-10 h-10 rounded-xl bg-indigo-600 flex items-center justify-center font-bold text-lg">
          $
        </div>

        <div className="ml-3">
          <h1 className="font-bold text-lg">
            ExpenseTracker
          </h1>

          <p className="text-xs text-slate-500">
            Personal finance
          </p>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-4 py-6 space-y-2">

        <p className="px-3 mb-3 text-xs font-semibold uppercase tracking-wider text-slate-500">
          Menu
        </p>

        {navItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition ${
                isActive
                  ? "bg-indigo-600 text-white"
                  : "text-slate-400 hover:bg-slate-800 hover:text-white"
              }`
            }
          >
            <span className="text-lg w-5">
              {item.icon}
            </span>

            {item.name}
          </NavLink>
        ))}

      </nav>

      {/* Logout */}
      <div className="p-4 border-t border-slate-800">

        <button
          onClick={logout}
          className="w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium text-slate-400 hover:bg-red-500/10 hover:text-red-400 transition"
        >
          <span>↪</span>
          Logout
        </button>

      </div>

    </aside>
  )
}

export default Sidebar