import Sidebar from "../components/Sidebar"
import Topbar from "../components/Topbar"
import StatCard from "../components/StatCard"

function Dashboard() {
  return (
    <div className="min-h-screen bg-slate-100 flex">

      <Sidebar />

      <div className="flex-1 min-w-0">

        <Topbar />

        <main className="p-6">

          {/* Welcome */}
          <div className="mb-8">
            <h1 className="text-2xl font-bold text-slate-900">
              Good evening, Saurabh 👋
            </h1>

            <p className="mt-1 text-slate-500">
              Here's what's happening with your finances.
            </p>
          </div>

          {/* Stats */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">

            <StatCard
              title="Total Expenses"
              value="₹25,450"
              description="This month"
            />

            <StatCard
              title="Monthly Budget"
              value="₹30,000"
              description="Current budget"
            />

            <StatCard
              title="Categories"
              value="6"
              description="Active categories"
            />

          </div>

          {/* Main content */}
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-5 mt-6">

            {/* Chart placeholder */}
            <div className="lg:col-span-2 bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">

              <div className="flex items-center justify-between mb-6">

                <div>
                  <h2 className="font-semibold text-slate-900">
                    Monthly Spending
                  </h2>

                  <p className="text-sm text-slate-500 mt-1">
                    Your spending overview
                  </p>
                </div>

                <select className="border border-slate-200 rounded-lg px-3 py-2 text-sm text-slate-600 outline-none">
                  <option>2026</option>
                </select>

              </div>

              <div className="h-64 rounded-xl bg-slate-50 flex items-center justify-center">
                <p className="text-sm text-slate-400">
                  Spending chart will appear here
                </p>
              </div>

            </div>

            {/* Budget */}
            <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">

              <h2 className="font-semibold text-slate-900">
                Budget Overview
              </h2>

              <p className="text-sm text-slate-500 mt-1">
                Monthly spending
              </p>

              <div className="mt-8">

                <div className="flex justify-between text-sm mb-2">
                  <span className="text-slate-500">
                    Used
                  </span>

                  <span className="font-semibold text-slate-900">
                    85%
                  </span>
                </div>

                <div className="w-full h-3 bg-slate-100 rounded-full overflow-hidden">
                  <div className="h-full w-[85%] bg-indigo-600 rounded-full" />
                </div>

                <div className="flex justify-between mt-3 text-sm">
                  <span className="text-slate-500">
                    ₹25,450 spent
                  </span>

                  <span className="text-slate-500">
                    ₹30,000
                  </span>
                </div>

              </div>

            </div>

          </div>

          {/* Recent Expenses */}
          <div className="mt-6 bg-white rounded-2xl border border-slate-200 shadow-sm">

            <div className="p-6 border-b border-slate-100">

              <h2 className="font-semibold text-slate-900">
                Recent Expenses
              </h2>

              <p className="text-sm text-slate-500 mt-1">
                Your latest transactions
              </p>

            </div>

            <div className="divide-y divide-slate-100">

              <div className="p-5 flex items-center justify-between">
                <div>
                  <p className="font-medium text-slate-900">
                    Dinner
                  </p>

                  <p className="text-sm text-slate-500">
                    Food • 19 Aug 2026
                  </p>
                </div>

                <p className="font-semibold text-slate-900">
                  ₹500
                </p>
              </div>

              <div className="p-5 flex items-center justify-between">
                <div>
                  <p className="font-medium text-slate-900">
                    Uber
                  </p>

                  <p className="text-sm text-slate-500">
                    Travel • 18 Aug 2026
                  </p>
                </div>

                <p className="font-semibold text-slate-900">
                  ₹300
                </p>
              </div>

              <div className="p-5 flex items-center justify-between">
                <div>
                  <p className="font-medium text-slate-900">
                    Movie
                  </p>

                  <p className="text-sm text-slate-500">
                    Entertainment • 17 Aug 2026
                  </p>
                </div>

                <p className="font-semibold text-slate-900">
                  ₹450
                </p>
              </div>

            </div>

          </div>

        </main>

      </div>

    </div>
  )
}

export default Dashboard