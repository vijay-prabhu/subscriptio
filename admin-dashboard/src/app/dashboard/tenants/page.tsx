"use client";

import { useEffect, useState } from "react";
import { api } from "@/lib/api";
import { formatDate } from "@/lib/utils";
import { StatusBadge } from "@/components/ui/badge";
import type { Tenant, PageResponse } from "@/types";

export default function TenantsPage() {
  const [tenants, setTenants] = useState<Tenant[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api<PageResponse<Tenant>>("/api/v1/admin/tenants?page=0&size=50")
      .then((data) => setTenants(data.content))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="text-zinc-500">Loading...</div>;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold text-white">Tenants</h2>
      </div>

      <div className="bg-zinc-900 border border-zinc-800 rounded-xl overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b border-zinc-800">
              <th className="text-left px-4 py-3 text-zinc-500 font-medium">Name</th>
              <th className="text-left px-4 py-3 text-zinc-500 font-medium">Slug</th>
              <th className="text-left px-4 py-3 text-zinc-500 font-medium">Status</th>
              <th className="text-left px-4 py-3 text-zinc-500 font-medium">Created</th>
            </tr>
          </thead>
          <tbody>
            {tenants.map((tenant) => (
              <tr key={tenant.id} className="border-b border-zinc-800/50 hover:bg-zinc-800/30">
                <td className="px-4 py-3 text-white font-medium">{tenant.name}</td>
                <td className="px-4 py-3 text-zinc-400">{tenant.slug}</td>
                <td className="px-4 py-3"><StatusBadge status={tenant.status} /></td>
                <td className="px-4 py-3 text-zinc-400">{formatDate(tenant.createdAt)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
