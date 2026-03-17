"use client";

import { useEffect, useState } from "react";
import { api } from "@/lib/api";
import { formatCurrency } from "@/lib/utils";
import { MetricCard } from "@/components/dashboard/MetricCard";
import { RevenueChart } from "@/components/dashboard/RevenueChart";
import type { DashboardMetrics, Tenant, PageResponse } from "@/types";

export default function DashboardPage() {
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null);
  const [tenantId, setTenantId] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const tenants = await api<PageResponse<Tenant>>("/api/v1/admin/tenants?page=0&size=1");
        if (tenants.content.length > 0) {
          const tid = tenants.content[0].id;
          setTenantId(tid);
          const m = await api<DashboardMetrics>(`/api/v1/admin/metrics/dashboard?tenantId=${tid}`);
          setMetrics(m);
        }
      } catch (err) {
        console.error("Failed to load metrics", err);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  if (loading) {
    return <div className="text-zinc-500">Loading...</div>;
  }

  return (
    <div className="space-y-6">
      <h2 className="text-lg font-semibold text-white">Dashboard</h2>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <MetricCard
          title="Monthly Recurring Revenue"
          value={formatCurrency(metrics?.mrr || 0)}
        />
        <MetricCard
          title="Active Subscriptions"
          value={String(metrics?.activeSubscriptions || 0)}
        />
        <MetricCard
          title="Trialing"
          value={String(metrics?.trialingSubscriptions || 0)}
        />
        <MetricCard
          title="Past Due"
          value={String(metrics?.pastDueSubscriptions || 0)}
          className={metrics?.pastDueSubscriptions ? "border-amber-800" : ""}
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <RevenueChart data={metrics?.revenueByPlan || []} />
        <div className="bg-zinc-900 border border-zinc-800 rounded-xl p-6">
          <h3 className="text-sm font-medium text-zinc-400 mb-4">Quick Stats</h3>
          <div className="space-y-3">
            <div className="flex justify-between text-sm">
              <span className="text-zinc-500">Total Invoices</span>
              <span className="text-white">{metrics?.totalInvoices || 0}</span>
            </div>
            <div className="flex justify-between text-sm">
              <span className="text-zinc-500">Active + Trialing</span>
              <span className="text-white">
                {(metrics?.activeSubscriptions || 0) + (metrics?.trialingSubscriptions || 0)}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
