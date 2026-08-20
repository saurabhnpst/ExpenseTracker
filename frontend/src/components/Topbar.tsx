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

function Topbar() {

  const username = getCurrentUsername()

  const initial =
    username.charAt(0).toUpperCase()

  return (
    <header className="h-20 bg-white border-b border-slate-200 flex items-center justify-between px-6">

      <div>

        <p className="text-sm text-slate-500">
          Overview
        </p>

        <h2 className="text-xl font-semibold text-slate-900">
          Dashboard
        </h2>

      </div>

      <div className="flex items-center gap-3">

        <div className="hidden sm:block text-right">

          <p className="text-sm font-semibold text-slate-800">
            {username}
          </p>

          <p className="text-xs text-slate-500">
            Personal account
          </p>

        </div>

        <div className="w-10 h-10 rounded-full bg-indigo-100 flex items-center justify-center text-indigo-700 font-semibold">
          {initial}
        </div>

      </div>

    </header>
  )
}

export default Topbar