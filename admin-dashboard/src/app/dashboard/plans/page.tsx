"use client";

import { useEffect, useState } from "react";
import { api } from "@/lib/api";
import { formatCurrency } from "@/lib/utils";
import { Badge } from "@/components/ui/badge";
import type { Plan, Tenant, PageResponse } from "@/types";

export default function PlansPage() {
  const [plans, setPlans] = useState<Plan[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const tenants = await api<PageResponse<Tenant>>("/api/v1/admin/tenants?page=0&size=1");
        if (tenants.content.length > 0) {
          const data = await api<PageResponse<Plan>>(
            `/api/v1/admin/plans?tenantId=${tenants.content[0].id}&page=0&size=50`
          );
          setPlans(data.content);
        }
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  if (loading) return <div className="text-zinc-500">Loading...</div>;

  return (
    <div className="space-y-6">
      <h2 className="text-lg font-semibold text-white">Plans</h2>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {plans.map((plan) => (
          <div key={plan.id} className="bg-zinc-900 border border-zinc-800 rounded-xl p-6 space-y-3">
            <div className="flex items-center justify-between">
              <h3 className="font-semibold text-white">{plan.name}</h3>
              <Badge variant={plan.isActive ? "success" : "default"}>
                {plan.isActive ? "Active" : "Inactive"}
              </Badge>
            </div>
            <p className="text-zinc-500 text-sm">{plan.description}</p>
            <div className="flex items-baseline gap-1">
              <span className="text-2xl font-bold text-white">{formatCurrency(plan.price)}</span>
              <span className="text-zinc-500 text-sm">/ {plan.billingInterval}</span>
            </div>
            {plan.trialDays > 0 && (
              <p className="text-xs text-blue-400">{plan.trialDays}-day free trial</p>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
