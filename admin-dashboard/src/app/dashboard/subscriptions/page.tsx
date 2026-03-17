"use client";

import { useEffect, useState } from "react";
import { api } from "@/lib/api";
import { formatCurrency, formatDate } from "@/lib/utils";
import { StatusBadge } from "@/components/ui/badge";
import type { Subscription, Tenant, PageResponse } from "@/types";

const STATUS_FILTERS = ["ALL", "TRIALING", "ACTIVE", "PAST_DUE", "CANCELLED", "EXPIRED"];

export default function SubscriptionsPage() {
  const [subscriptions, setSubscriptions] = useState<Subscription[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState("ALL");
  const [tenantId, setTenantId] = useState<string>("");

  useEffect(() => {
    async function load() {
      try {
        const tenants = await api<PageResponse<Tenant>>("/api/v1/admin/tenants?page=0&size=1");
        if (tenants.content.length > 0) {
          setTenantId(tenants.content[0].id);
        }
      } catch (err) {
        console.error(err);
      }
    }
    load();
  }, []);

  useEffect(() => {
    if (!tenantId) return;
    setLoading(true);
    const statusParam = filter !== "ALL" ? `&status=${filter}` : "";
    api<PageResponse<Subscription>>(
      `/api/v1/admin/subscriptions?tenantId=${tenantId}&page=0&size=50${statusParam}`
    )
      .then((data) => setSubscriptions(data.content))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [tenantId, filter]);

  return (
    <div className="space-y-6">
      <h2 className="text-lg font-semibold text-white">Subscriptions</h2>

      <div className="flex gap-2">
        {STATUS_FILTERS.map((s) => (
          <button
            key={s}
            onClick={() => setFilter(s)}
            className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-colors ${
              filter === s
                ? "bg-blue-600 text-white"
                : "bg-zinc-800 text-zinc-400 hover:text-white"
            }`}
          >
            {s}
          </button>
        ))}
      </div>

      {loading ? (
        <div className="text-zinc-500">Loading...</div>
      ) : subscriptions.length === 0 ? (
        <div className="text-zinc-500 text-sm">No subscriptions found</div>
      ) : (
        <div className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-zinc-800">
                <th className="text-left px-4 py-3 text-zinc-500 font-medium">Plan</th>
                <th className="text-left px-4 py-3 text-zinc-500 font-medium">Status</th>
                <th className="text-left px-4 py-3 text-zinc-500 font-medium">Price</th>
                <th className="text-left px-4 py-3 text-zinc-500 font-medium">Period End</th>
                <th className="text-left px-4 py-3 text-zinc-500 font-medium">Created</th>
              </tr>
            </thead>
            <tbody>
              {subscriptions.map((sub) => (
                <tr key={sub.id} className="border-b border-zinc-800/50 hover:bg-zinc-800/30">
                  <td className="px-4 py-3 text-white font-medium">{sub.plan.name}</td>
                  <td className="px-4 py-3"><StatusBadge status={sub.status} /></td>
                  <td className="px-4 py-3 text-zinc-400">
                    {formatCurrency(sub.plan.price)}/{sub.plan.billingInterval}
                  </td>
                  <td className="px-4 py-3 text-zinc-400">{formatDate(sub.currentPeriodEnd)}</td>
                  <td className="px-4 py-3 text-zinc-400">{formatDate(sub.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
