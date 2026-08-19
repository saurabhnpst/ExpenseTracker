interface StatCardProps {
  title: string
  value: string
  description: string
}

function StatCard({
  title,
  value,
  description,
}: StatCardProps) {
  return (
    <div className="bg-white rounded-2xl border border-slate-200 p-6 shadow-sm">

      <p className="text-sm font-medium text-slate-500">
        {title}
      </p>

      <h3 className="mt-2 text-2xl font-bold text-slate-900">
        {value}
      </h3>

      <p className="mt-2 text-xs text-slate-500">
        {description}
      </p>

    </div>
  )
}

export default StatCard