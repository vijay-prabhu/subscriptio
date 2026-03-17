import { cn } from "@/lib/utils";

export function MetricCard({ title, value, subtitle, className }: {
  title: string;
  value: string;
  subtitle?: string;
  className?: string;
}) {
  return (
    <div className={cn("bg-zinc-900 border border-zinc-800 rounded-xl p-6", className)}>
      <p className="text-sm text-zinc-500">{title}</p>
      <p className="text-2xl font-bold text-white mt-1">{value}</p>
      {subtitle && <p className="text-xs text-zinc-500 mt-1">{subtitle}</p>}
    </div>
  );
}
