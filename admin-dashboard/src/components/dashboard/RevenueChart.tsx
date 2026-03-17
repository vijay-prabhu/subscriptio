"use client";

import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from "recharts";
import { formatCurrency } from "@/lib/utils";

interface PlanBreakdown {
  plan: string;
  revenue: number;
  count: number;
}

export function RevenueChart({ data }: { data: PlanBreakdown[] }) {
  if (!data || data.length === 0) {
    return (
      <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-6">
        <h3 className="text-sm font-medium text-zinc-400 mb-4">Revenue by Plan</h3>
        <p className="text-zinc-600 text-sm">No revenue data yet</p>
      </div>
    );
  }

  return (
    <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-6">
      <h3 className="text-sm font-medium text-zinc-400 mb-4">Revenue by Plan</h3>
      <ResponsiveContainer width="100%" height={250}>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="#27272a" />
          <XAxis dataKey="plan" tick={{ fill: "#a1a1aa", fontSize: 12 }} />
          <YAxis tick={{ fill: "#a1a1aa", fontSize: 12 }} tickFormatter={(v) => `$${v}`} />
          <Tooltip
            contentStyle={{ background: "#18181b", border: "1px solid #27272a", borderRadius: 8 }}
            labelStyle={{ color: "#fff" }}
            formatter={(value) => [formatCurrency(Number(value)), "Revenue"]}
          />
          <Bar dataKey="revenue" fill="#3b82f6" radius={[4, 4, 0, 0]} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
